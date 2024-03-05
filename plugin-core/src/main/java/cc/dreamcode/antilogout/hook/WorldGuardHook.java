package cc.dreamcode.antilogout.hook;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardHook {

    public boolean isInRegion(Player source, String regionName) {
        WorldGuard worldGuard = WorldGuard.getInstance();
        WorldGuardPlatform platform = worldGuard.getPlatform();
        RegionManager regionManager = platform.getRegionContainer().get(BukkitAdapter.adapt(source.getWorld()));
        if (regionManager == null) {
            throw new IllegalStateException("Region manager is null");
        }
        Location location = source.getLocation();
        ProtectedRegion region = regionManager.getRegion(regionName);
        if (region == null) {
            throw new IllegalStateException("Region is null");
        }
        return region.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}
