package net.chronoschambers.chambers.point.model;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a point inside a chamber with optional metadata.
 *
 * @param groupId the group this point belongs to
 * @param location the world location of the point
 * @param metadata additional data associated with the point
 */
public record ChamberPoint(@NotNull String groupId, @NotNull Location location, @NotNull Map<String, Object> metadata) {

    /**
     * Creates a chamber point without metadata.
     *
     * @param groupId the group this point belongs to
     * @param location the world location of the point
     */
    public ChamberPoint(@NotNull String groupId, @NotNull Location location) {
        this(groupId, location, new HashMap<>());
    }

}
