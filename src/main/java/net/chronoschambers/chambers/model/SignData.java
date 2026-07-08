package net.chronoschambers.chambers.model;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * Represents sign data at a specific location.
 *
 * @param location the sign location
 * @param lines the sign lines
 */
public record SignData(@NotNull Location location, @NotNull String[] lines) {

    /**
     * Gets a line by index.
     *
     * @param index the line index
     * @return the line, or empty if out of bounds
     */
    public @NotNull String line(int index) {
        return index < 0 || index >= lines.length ? "" : lines[index];
    }

}
