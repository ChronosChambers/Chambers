package net.chronoschambers.chambers.point.model;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;

/**
 * A group of chamber points identified by an id.
 *
 * @param id the group identifier
 * @param points the points in this group
 */
public record ChamberPointGroup(@NotNull String id, @NotNull Collection<ChamberPoint> points) {

    /**
     * Creates an empty point group.
     *
     * @param id the group identifier
     */
    public ChamberPointGroup(@NotNull String id) {
        this(id, new HashSet<>());
    }

}
