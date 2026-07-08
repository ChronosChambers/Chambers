package net.chronoschambers.chambers.generator;

import net.chronoschambers.chambers.exception.ChamberGenerateException;
import net.chronoschambers.chambers.model.Chamber;
import net.chronoschambers.chambers.paste.PasteResult;
import net.chronoschambers.chambers.paste.PasteService;
import net.chronoschambers.chambers.paste.model.PasteContext;
import net.chronoschambers.chambers.point.collector.PointCollector;
import net.chronoschambers.chambers.point.processor.PointProcessor;
import net.chronoschambers.chambers.scheduler.Scheduler;
import net.chronoschambers.chambers.world.ChamberWorld;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Handles the generation and placement of {@link Chamber} instances within a {@link ChamberWorld}.
 */
public class ChamberGenerator {

    private final @NotNull Scheduler scheduler;
    private final @NotNull ChamberWorld world;
    private final @NotNull PasteService pasteService;
    private final @NotNull Supplier<PointCollector> collectorSupplier;

    /**
     * Creates a new chamber generator.
     *
     * @param scheduler scheduler used for task execution
     * @param world the chamber world to place chambers in
     * @param pasteService service used to paste schematics
     * @param collectorSupplier supplies new {@link PointCollector} instances per generation
     */
    public ChamberGenerator(@NotNull Scheduler scheduler, @NotNull ChamberWorld world, @NotNull PasteService pasteService, @NotNull Supplier<PointCollector> collectorSupplier) {
        this.scheduler = scheduler;
        this.world = world;
        this.pasteService = pasteService;
        this.collectorSupplier = collectorSupplier;
    }

    /**
     * Gets the chamber world this generator operates on.
     *
     * @return the chamber world
     */
    public @NotNull ChamberWorld getWorld() {
        return world;
    }

    /**
     * Generates a chamber with no rotation.
     *
     * @param id unique chamber identifier
     * @param schematicName name of the schematic to paste
     * @param processors processors applied to the pasted result
     * @param callback called with the generated chamber, or empty if generation fails
     */
    public void generate(@NotNull String id, @NotNull String schematicName, @NotNull List<PointProcessor> processors, @NotNull Consumer<Optional<Chamber>> callback) {
        generate(id, schematicName, 0, processors, callback);
    }

    /**
     * Generates a chamber at the next available location in the world.
     *
     * @param id unique chamber identifier
     * @param schematicName name of the schematic to paste
     * @param rotation rotation applied to the schematic
     * @param processors processors applied to the pasted result
     * @param callback called with the generated chamber, or empty if generation fails
     *
     * @throws ChamberGenerateException if no valid location can be found
     */
    public void generate(@NotNull String id, @NotNull String schematicName, float rotation, @NotNull List<PointProcessor> processors, @NotNull Consumer<Optional<Chamber>> callback) {
        Location nextLocation = world.getNextLocation().orElse(null);

        if (nextLocation == null) {
            callback.accept(Optional.empty());
            throw new ChamberGenerateException("Failed to find next location for chamber");
        }

        pasteService.paste(new PasteContext(schematicName, nextLocation, rotation), result -> {
            if (result.isEmpty()) {
                callback.accept(Optional.empty());
                return;
            }

            PasteResult pasteResult = result.get();

            PointCollector collector = collectorSupplier.get();

            for (PointProcessor processor : processors) {
                processor.process(pasteResult, collector);
            }

            Chamber chamber = new Chamber(id, pasteResult, collector);
            world.addChamber(chamber);

            callback.accept(Optional.of(chamber));
        });
    }

}
