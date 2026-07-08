package net.chronoschambers.chambers.point.processor.impl;

import net.chronoschambers.chambers.model.SignData;
import net.chronoschambers.chambers.paste.PasteResult;
import net.chronoschambers.chambers.point.collector.PointCollector;
import net.chronoschambers.chambers.point.model.ChamberPoint;
import net.chronoschambers.chambers.point.processor.PointProcessor;
import org.jetbrains.annotations.NotNull;

public class SignPointProcessor implements PointProcessor {

    private final @NotNull String groupId;

    public SignPointProcessor(@NotNull String groupId) {
        this.groupId = groupId;
    }

    @Override
    public void process(@NotNull PasteResult pasteResult, @NotNull PointCollector collector) {
        for (SignData signData : pasteResult.signData().values()) {
            if (!signData.line(0).equalsIgnoreCase("[spawn]") || !signData.line(1).equalsIgnoreCase(groupId)) {
                continue;
            }

            collector.add(groupId, new ChamberPoint(groupId, signData.location()));
        }
    }

}
