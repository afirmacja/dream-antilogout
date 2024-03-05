package cc.dreamcode.antilogout.user.task;

import cc.dreamcode.antilogout.AntiLogoutConfig;
import cc.dreamcode.antilogout.user.User;
import cc.dreamcode.antilogout.user.UserCache;
import cc.dreamcode.platform.bukkit.component.scheduler.Scheduler;
import cc.dreamcode.utilities.TimeUtil;
import cc.dreamcode.utilities.bukkit.StringColorUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Collections;

@RequiredArgsConstructor
@Scheduler(delay = 20L, interval = 20L)
public final class UserProtectionShowTask implements Runnable {

    private final UserCache userCache;
    private final AntiLogoutConfig antiLogoutConfig;

    @Override
    public void run() {
        for (User user : this.userCache.values()) {
            Player player = Bukkit.getPlayer(user.getUniqueId());
            if (this.antiLogoutConfig.getPluginWrapper().isUseBossBarForProtectionInfo()) {
                showBossBar(user, player);
            } else {
                showActionBar(user, player);
            }
        }
    }

    void showActionBar(User user, Player player) {
        if (!user.hasProtection() || player == null) {
            return;
        }

        this.antiLogoutConfig.getMessageWrapper().getInfoProtection().send(player,
                Collections.singletonMap(
                        "time",
                        TimeUtil.convertDurationSeconds(Duration.ofMillis(user.getProtection() - System.currentTimeMillis()))
                ));
    }

    void showBossBar(User user, Player player) {
        NamespacedKey key = NamespacedKey.minecraft("antilogout-protection-" + user.getNickname());
        if (!user.hasProtection() || player == null) {
            BossBar bossBar = user.getProtectionBossBar();
            if (bossBar != null) {
                bossBar.removeAll();
                Bukkit.removeBossBar(key);
            }
            return;
        }

        AntiLogoutConfig.PluginWrapper.BossBarWrapper bar = this.antiLogoutConfig.getPluginWrapper().getProtectionBossBar();
        BossBar bossBar = user.getProtectionBossBar();
        if (bossBar == null) {
            bossBar = Bukkit.createBossBar(key, "", bar.getColor(), bar.getStyle());
        }

        double progress = bar.getProgress();
        if (progress > 1) {
            progress = 1.0;
        } else if (progress < 0) {
            progress = 0.0;
        }
        bossBar.setProgress(progress);

        bossBar.setTitle(StringColorUtil.fixColor(bar.getTitle(),
                Collections.singletonMap(
                        "time",
                        TimeUtil.convertDurationSeconds(Duration.ofMillis(user.getProtection() - System.currentTimeMillis()))
                )
        ));
        bossBar.addPlayer(player);
    }

}