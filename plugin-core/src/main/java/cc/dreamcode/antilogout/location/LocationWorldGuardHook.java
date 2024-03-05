package cc.dreamcode.antilogout.location;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class LocationWorldGuardHook {

    public static boolean isInRegion(Location location, List<String> regionNames) {
        WorldGuard worldGuard = WorldGuard.getInstance();
        WorldGuardPlatform platform = worldGuard.getPlatform();
        RegionManager regionManager = platform.getRegionContainer().get(BukkitAdapter.adapt(requireNonNull(location.getWorld())));
        if (regionManager == null) {
            throw new IllegalStateException("Region manager for " + location.getWorld().getName() + " was not found!");
        }

        for (String regionName : regionNames) {
            ProtectedRegion region = regionManager.getRegion(regionName);
            if (region == null) {
                throw new IllegalStateException("Region " + regionName + " was not found!");
            }
            if (region.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ())) {
                return true;
            }
        }
        return false;
    }

    public static Location findRegionCenter(Location location, List<String> regionNames) {
        WorldGuard worldGuard = WorldGuard.getInstance();
        WorldGuardPlatform platform = worldGuard.getPlatform();
        RegionManager regionManager = platform.getRegionContainer().get(BukkitAdapter.adapt(requireNonNull(location.getWorld())));
        if (regionManager == null) {
            throw new IllegalStateException("Region manager for " + location.getWorld().getName() + " was not found!");
        }

        for (String regionName : regionNames) {
            ProtectedRegion region = regionManager.getRegion(regionName);
            if (region == null) {
                throw new IllegalStateException("Region " + regionName + " was not found!");
            }
            if (region.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ())) {
                int maxX = region.getMaximumPoint().getBlockX();
                int minX = region.getMinimumPoint().getBlockX();
                int maxZ = region.getMaximumPoint().getBlockZ();
                int minZ = region.getMinimumPoint().getBlockZ();
                return new Location(location.getWorld(), (double) (maxX + minX) / 2, location.getY(), (double) (maxZ + minZ) / 2);
            }
        }
        return null;
    }

}