package cc.dreamcode.antilogout.user;

import cc.dreamcode.antilogout.AntiLogoutConfig;
import cc.dreamcode.antilogout.helper.AntiLogoutHelper;
import cc.dreamcode.antilogout.location.LocationTeller;
import cc.dreamcode.utilities.TimeUtil;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.persistence.PersistencePath;
import eu.okaeri.persistence.document.DocumentPersistence;
import eu.okaeri.tasker.core.Tasker;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UserListener implements Listener {

    private final Tasker tasker;
    private final UserCache userCache;
    private final UserRepository userRepository;
    private final AntiLogoutConfig antiLogoutConfig;
    private final DocumentPersistence documentPersistence;

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.tasker.newSharedChain(player.getUniqueId().toString())
                .supplyAsync(() -> {
                    User user = this.userRepository.findByPath(player.getUniqueId()).orElseGet(() -> {
                        User update = (User) this.documentPersistence.update(ConfigManager.create(User.class),
                                this.userRepository.getCollection());
                        update.setPath(PersistencePath.of(player.getUniqueId().toString()));
                        update.setProtection(Instant.now().plus(this.antiLogoutConfig.getPluginWrapper().getProtectionTime()).toEpochMilli());
                        return update;
                    });
                    user.setNickname(player.getName());
                    return user;
                })
                .acceptAsync(this.userCache::add)
                .execute();
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.tasker.newSharedChain(player.getUniqueId().toString())
                .supplyAsync(() -> this.userCache.findUserByPlayer(player))
                .acceptAsync(user -> {
                    if (user.isInCombat()) {
                        player.setHealth(0);
                        this.antiLogoutConfig.getMessageWrapper().getAnnounceLoggedOutWhileInCombat().sendAll(Collections.singletonMap("player", player.getName()));
                    }

                    this.userCache.remove(user);
                    this.userRepository.save(user);
                })
                .execute();
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        Player victim = event.getEntity();
        this.tasker.newSharedChain(victim.getUniqueId().toString())
                .supplyAsync(() -> this.userCache.findUserByPlayer(victim))
                .acceptAsync(user -> {
                    user.resetCombat();
                    if (this.antiLogoutConfig.getPluginWrapper().isProtectionAfterDeath()) {
                        user.setProtection(Instant.now().plus(this.antiLogoutConfig.getPluginWrapper().getProtectionTime()).toEpochMilli());
                    }
                    this.userRepository.save(user);
                })
                .execute();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void creativeDamage(EntityDamageByEntityEvent event) {
        if (this.antiLogoutConfig.getPluginWrapper().isCanDamagePlayersWhileInCreative()) {
            return;
        }

        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player damager = (Player) event.getDamager();
        if (damager.getGameMode().equals(GameMode.CREATIVE)) {
            event.setCancelled(true);
            this.antiLogoutConfig.getMessageWrapper().getErrorCannotDamageWhileInCreative().send(damager);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void entityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity();
        if (LocationTeller.isInBlockedRegion(victim.getLocation(),
                this.antiLogoutConfig.getPluginWrapper().getBlockedRegions(),
                this.antiLogoutConfig.getPluginWrapper().getRegions())) {
            return;
        }

        User victimUser = this.userCache.findUserByPlayer(victim);
        if (victimUser == null) {
            return;
        }

        Player attacker = AntiLogoutHelper.findAttackerByEvent(event);
        if (this.antiLogoutConfig.getPluginWrapper().isFromMonsters() && attacker == null) {
            return;
        }

        if (victim.equals(attacker)) {
            return;
        }

        User attackerUser = null;
        if (attacker != null) {
            attackerUser = this.userCache.findUserByPlayer(attacker);
            if (attackerUser.getProtection() > System.currentTimeMillis()) {
                this.antiLogoutConfig.getMessageWrapper().getErrorYouHaveProtection().send(attacker, Collections.singletonMap("time",
                        TimeUtil.convertDurationMills(Duration.ofMillis(attackerUser.getProtection() - System.currentTimeMillis()))));
                event.setCancelled(true);
                return;
            }
        }

        if (victimUser.getProtection() > System.currentTimeMillis()) {
            if (attacker != null) {
                this.antiLogoutConfig.getMessageWrapper().getErrorAttackedPlayerHasProtection().send(attacker, Collections.singletonMap("time",
                        TimeUtil.convertDurationMills(Duration.ofMillis(victimUser.getProtection() - System.currentTimeMillis()))));
            }
            event.setCancelled(true);
            return;
        }

        long combatTime = this.antiLogoutConfig.getPluginWrapper().getCombatTime().toMillis();
        victimUser.setLastAttackTime(System.currentTimeMillis() + combatTime);
        if (attacker != null && !attacker.hasPermission(this.antiLogoutConfig.getPluginWrapper().getAntiLogoutBypass())) {
            attackerUser.setLastAttackTime(System.currentTimeMillis() + combatTime);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage().toLowerCase().split(" ")[0].replace("/", "");
        if (!this.userCache.findUserByPlayer(player).isInCombat()) {
            return;
        }

        if (player.hasPermission(this.antiLogoutConfig.getPluginWrapper().getCommandNotAllowedWhileInCombatBypass())) {
            return;
        }

        if (this.antiLogoutConfig.getPluginWrapper().getAllowedCommands().contains(message)) {
            return;
        }

        event.setCancelled(true);
        this.antiLogoutConfig.getMessageWrapper().getErrorCannotUseCommandWhileInCombat().send(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Location from = event.getFrom(), to = event.getTo();
        if (to == null || (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ())) {
            return;
        }

        Player player = event.getPlayer();
        if (!this.userCache.findUserByPlayer(player).isInCombat() || player.hasPermission(this.antiLogoutConfig.getPluginWrapper().getAntiLogoutBypass())) {
            return;
        }

        Location regionCenter = LocationTeller.findRegionCenter(to,
                this.antiLogoutConfig.getPluginWrapper().getBlockedRegions(),
                this.antiLogoutConfig.getPluginWrapper().getRegions());
        if (regionCenter != null) {
            Location l = regionCenter.subtract(player.getLocation());
            double distance = player.getLocation().distance(regionCenter);

            Vector vector = l.toVector().add(new Vector(0, 5, 0)).multiply(1.25 / distance);
            player.setVelocity(vector.multiply(-1.5));
        }
    }

}