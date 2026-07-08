package net.chronoschambers.chambers.paste.model;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the context for a schematic paste operation.
 *
 * @param schematicName the name of the schematic to paste
 * @param location the target paste location
 * @param rotation the rotation in degrees applied to the schematic
 */
public record PasteContext(@NotNull String schematicName, @NotNull Location location, double rotation) {

}
