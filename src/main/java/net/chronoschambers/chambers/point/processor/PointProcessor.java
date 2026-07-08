package net.chronoschambers.chambers.point.processor;

import net.chronoschambers.chambers.paste.PasteResult;
import net.chronoschambers.chambers.point.collector.PointCollector;
import org.jetbrains.annotations.NotNull;

/**
 * Processes points extracted from a pasted chamber.
 */
public interface PointProcessor {

    /**
     * Processes a paste result and collects relevant points.
     *
     * @param pasteResult the result of the schematic paste
     * @param collector the point collector to store processed points
     */
    void process(@NotNull PasteResult pasteResult, @NotNull PointCollector collector);

}
