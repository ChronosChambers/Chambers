package net.chronoschambers.chambers.model;

import net.chronoschambers.chambers.paste.PasteResult;
import net.chronoschambers.chambers.point.collector.PointCollector;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a generated chamber instance.
 *
 * @param id unique identifier of the chamber
 * @param pasteResult result of the schematic paste
 * @param collector collected points associated with this chamber
 */
public record Chamber(@NotNull String id, @NotNull PasteResult pasteResult, @NotNull PointCollector collector) {

}
