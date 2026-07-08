package net.chronoschambers.chambers.paste;

import net.chronoschambers.chambers.model.SignData;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Represents the result of a schematic paste.
 *
 * @param origin the origin location of the paste
 * @param minimumPoint the minimum (corner) bound of the pasted region
 * @param maximumPoint the maximum (corner) bound of the pasted region
 * @param signData sign data within the pasted region
 */
public record PasteResult(@NotNull Location origin, @NotNull Location minimumPoint, @NotNull Location maximumPoint, @NotNull Map<Location, SignData> signData) {

    /**
     * Checks if the given location is within the pasted region bounds.
     *
     * @param location the location to check
     * @return true if within bounds, otherwise false
     */
    public boolean contains(@NotNull Location location) {
        return location.getBlockX() >= minimumPoint.getBlockX()
                && location.getBlockX() <= maximumPoint.getBlockX()
                && location.getBlockY() >= minimumPoint.getBlockY()
                && location.getBlockY() <= maximumPoint.getBlockY()
                && location.getBlockZ() >= minimumPoint.getBlockZ()
                && location.getBlockZ() <= maximumPoint.getBlockZ();
    }

}
