package cc.dreamcode.antilogout.location;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;

public final class LocationTeller {

    public static boolean isInBlockedRegion(Location location, List<String> blockedRegions, List<Region> regions) {
        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            return LocationWorldGuardHook.isInRegion(location, blockedRegions);
        }

        for (Region region : regions) {
            double x = location.getX();
            double y = location.getY();
            double z = location.getZ();
            return x >= (double) region.getMinX() && x < (double) (region.getMaxX() + 1)
                    && y >= (double) region.getMinY() && y < (double) (region.getMaxY() + 1)
                    && z >= (double) region.getMinZ() && z < (double) (region.getMaxZ() + 1);

        }
        return false;
    }

    public static Location findRegionCenter(Location location, List<String> blockedRegions, List<Region> regions) {
        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            return LocationWorldGuardHook.findRegionCenter(location, blockedRegions);
        }

        for (Region region : regions) {
            double x = location.getX();
            double y = location.getY();
            double z = location.getZ();
            if (x >= (double) region.getMinX() && x < (double) (region.getMaxX() + 1)
                    && y >= (double) region.getMinY() && y < (double) (region.getMaxY() + 1)
                    && z >= (double) region.getMinZ() && z < (double) (region.getMaxZ() + 1)) {
                return new Location(location.getWorld(),
                        (double) (region.getMaxX() + region.getMinX()) / 2,
                        location.getY(),
                        (double) (region.getMaxZ() + region.getMinZ()) / 2);
            }

        }
        return null;
    }
}