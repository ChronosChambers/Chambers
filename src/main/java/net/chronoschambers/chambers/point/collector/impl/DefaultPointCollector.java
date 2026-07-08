package net.chronoschambers.chambers.point.collector.impl;

import net.chronoschambers.chambers.point.model.ChamberPoint;
import net.chronoschambers.chambers.point.model.ChamberPointGroup;
import net.chronoschambers.chambers.point.collector.PointCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultPointCollector implements PointCollector {

    private final ConcurrentHashMap<String, ChamberPointGroup> groups = new ConcurrentHashMap<>();

    @Override
    public void add(@NotNull String id, @NotNull ChamberPoint point) {
        groups.computeIfAbsent(id, s -> new ChamberPointGroup(id)).points().add(point);
    }

    @Override
    public Map<String, ChamberPointGroup> groups() {
        return Map.copyOf(groups);
    }

}
