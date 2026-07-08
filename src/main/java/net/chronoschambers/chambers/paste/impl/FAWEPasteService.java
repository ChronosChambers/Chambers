package net.chronoschambers.chambers.paste.impl;

import com.fastasyncworldedit.core.FaweAPI;
import com.fastasyncworldedit.core.extent.processor.lighting.RelightMode;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockState;
import net.chronoschambers.chambers.model.SignData;
import net.chronoschambers.chambers.paste.PasteResult;
import net.chronoschambers.chambers.paste.PasteService;
import net.chronoschambers.chambers.paste.model.PasteContext;
import net.chronoschambers.chambers.scheduler.Scheduler;
import org.bukkit.Location;
import org.enginehub.linbus.tree.LinCompoundTag;
import org.enginehub.linbus.tree.LinStringTag;
import org.enginehub.linbus.tree.LinTag;
import org.enginehub.linbus.tree.LinTagType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FAWEPasteService implements PasteService {

    private static final Logger logger = Logger.getLogger("FAWEPasteService");

    private final @NotNull Scheduler scheduler;
    private final @NotNull File schematicFolder;

    public FAWEPasteService(@NotNull Scheduler scheduler, @NotNull File schematicFolder) {
        this.scheduler = scheduler;
        this.schematicFolder = schematicFolder;
    }

    @Override
    public void paste(@NotNull PasteContext context, @NotNull Consumer<Optional<PasteResult>> callback) {
        scheduler.async(() -> {
            File schematicFile = new File(schematicFolder, context.schematicName());

            if (!schematicFile.exists()) {
                scheduler.sync(() -> callback.accept(Optional.empty()));
                return;
            }

            ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(schematicFile);

            if (clipboardFormat == null) {
                logger.log(Level.SEVERE, "Unable to identify clipboard format for schematic: " + context.schematicName());
                return;
            }

            Location location = context.location();

            try (FileInputStream inputStream = new FileInputStream(schematicFile);
                 ClipboardReader reader = clipboardFormat.getReader(inputStream)) {

                Clipboard clipboard = reader.read();

                clipboard.transform(new AffineTransform().rotateY(context.rotation()));

                if (clipboard == null) {
                    logger.log(Level.SEVERE, "Unable to read clipboard for schematic: " + context.schematicName());
                    return;
                }

                BlockVector3 minimumPoint = clipboard.getMinimumPoint();
                BlockVector3 maximumPoint = clipboard.getMaximumPoint();

                BlockVector3 center = minimumPoint.add(maximumPoint).divide(2);
                clipboard.setOrigin(center);

                HashMap<Location, SignData> signData = new HashMap<>();

                for (BlockVector3 position : clipboard) {
                    BlockState block = clipboard.getBlock(position);

                    if (block.isAir()) {
                        continue;
                    }

                    String[] signLines = readSignLines(block.getNbt());

                    if (signLines == null) {
                        continue;
                    }

                    Location signLocation = location.clone().add(position.x(), position.y(), position.z());

                    signData.put(signLocation, new SignData(signLocation, signLines));
                }

                Location minimumLocation = new Location(location.getWorld(), location.getX() + minimumPoint.x() - center.x(),
                        location.getY() + minimumPoint.y() - center.y(),
                        location.getZ() + minimumPoint.z() - center.z());

                Location maximumLocation = new Location(location.getWorld(), location.getX() + maximumPoint.x() - center.x(),
                        location.getY() + maximumPoint.y() - center.y(),
                        location.getZ() + maximumPoint.z() - center.z());

                com.sk89q.worldedit.world.World weWorld = FaweAPI.getWorld(location.getWorld().getName());

                EditSession editSession = WorldEdit.getInstance()
                        .newEditSessionBuilder()
                        .world(weWorld)
                        .fastMode(true)
                        .limitUnlimited()
                        .build();

                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
                        .ignoreAirBlocks(true)
                        .copyEntities(false)
                        .build();

                Operations.completeBlindly(operation);
                editSession.flushQueue();

                scheduler.sync(() -> {
                    CuboidRegion region = new CuboidRegion(
                            weWorld,
                            BlockVector3.at(minimumLocation.getBlockX(), minimumLocation.getBlockY(), minimumLocation.getBlockZ()),
                            BlockVector3.at(maximumLocation.getBlockX(), maximumLocation.getBlockY(), maximumLocation.getBlockZ())
                    );

                    FaweAPI.fixLighting(
                            weWorld,
                            region,
                            null,
                            RelightMode.OPTIMAL
                    );

                    callback.accept(Optional.of(new PasteResult(location, minimumLocation, maximumLocation, signData)));
                });
            } catch (IOException | WorldEditException e) {
                logger.log(Level.SEVERE, "Error pasting schematic: " + context.schematicName(), e);
                scheduler.sync(() -> callback.accept(Optional.empty()));
            }
        });
    }

    private @Nullable String[] readSignLines(@Nullable LinCompoundTag nbt) {
        if (nbt == null) {
            return null;
        }

        Set<String> keys = nbt.value().keySet();

        if (!keys.contains("front_text") || keys.contains("Text1")) {
            return null;
        }

        LinCompoundTag frontText = nbt.getTag("front_text", LinTagType.compoundTag());

        LinTagType<LinTag<?>> messagesTag = frontText.getTag("messages", LinTagType.listTag()).elementType();

        if (messagesTag.name().equalsIgnoreCase(LinTagType.stringTag().name())) {
            List<LinStringTag> messages = frontText.getListTag("messages", LinTagType.stringTag()).value();
            return messages.stream().map(LinStringTag::value).toArray(String[]::new);
        }

        List<LinCompoundTag> messages = frontText.getListTag("messages", LinTagType.compoundTag()).value();

        return messages.stream().map(tag -> {
            Map<String, LinTag<?>> messageMap = tag.value();

            if (!messageMap.containsKey("text") || !messageMap.containsKey("")) {
                return "";
            }

            LinStringTag signText = (LinStringTag) messageMap.get("text");

            return signText != null ? signText.value() : "";
        }).toArray(String[]::new);
    }

}
