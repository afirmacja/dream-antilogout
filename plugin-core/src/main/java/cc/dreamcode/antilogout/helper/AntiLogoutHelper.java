package cc.dreamcode.antilogout.helper;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

@UtilityClass
public class AntiLogoutHelper {

    public static Player findAttackerByEvent(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        boolean isPlayer = attacker instanceof Player, isProjectile = attacker instanceof Projectile;
        if (!isPlayer && !isProjectile) {
            return null;
        }

        if (isProjectile) {
            ProjectileSource source = ((Projectile) attacker).getShooter();
            if (source instanceof Player) {
                return (Player) source;
            }
        }

        return isPlayer ? (Player) attacker : null;
    }
}
