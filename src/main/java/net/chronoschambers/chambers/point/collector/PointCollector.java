package net.chronoschambers.chambers.point.collector;

import net.chronoschambers.chambers.point.model.ChamberPoint;
import net.chronoschambers.chambers.point.model.ChamberPointGroup;
import net.chronoschambers.chambers.point.model.ChamberPointType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Collects and manages chamber points grouped by identifier.
 */
public interface PointCollector {

    /**
     * Adds a point to the specified group.
     *
     * @param id the group identifier
     * @param point the point to add
     */
    void add(@NotNull String id, @NotNull ChamberPoint point);

    /**
     * Gets all point groups.
     *
     * @return map of group id to group data
     */
    Map<String, ChamberPointGroup> groups();

    /**
     * Gets a point group by id.
     *
     * @param id the group identifier
     * @return the group if present
     */
    default Optional<ChamberPointGroup> getGroup(@NotNull String id) {
        return Optional.ofNullable(groups().get(id));
    }

    /**
     * Gets a point group by type.
     *
     * @param type the point type
     * @return the group if present
     */
    default Optional<ChamberPointGroup> getGroup(@NotNull ChamberPointType type) {
        return Optional.ofNullable(groups().get(type.id()));
    }

    /**
     * Adds multiple points to the specified group.
     *
     * @param id the group identifier
     * @param points points to add
     */
    default void addAll(@NotNull String id, @NotNull Collection<ChamberPoint> points) {
        points.forEach(point -> add(id, point));
    }

}
