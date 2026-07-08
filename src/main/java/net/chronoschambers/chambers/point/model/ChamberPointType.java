package net.chronoschambers.chambers.point.model;

import net.chronoschambers.chambers.point.processor.PointProcessor;
import net.chronoschambers.chambers.point.processor.impl.SignPointProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the different types of chamber points and their associated processors.
 */
public enum ChamberPointType {

    PLAYER_SPAWN("player_spawn", new SignPointProcessor("player_spawn")),
    ENEMY_SPAWN("enemy_spawn", new SignPointProcessor("enemy_spawn")),
    BOSS_SPAWN("boss_spawn", new SignPointProcessor("boss_spawn")),
    POWER_UP("power_up", new SignPointProcessor("power_up")),
    LOOT("loot", new SignPointProcessor("loot")),
    HOURGLASS("hourglass", new SignPointProcessor("hourglass"));

    private static final List<PointProcessor> PROCESSORS;

    static {
        List<PointProcessor> processors = new ArrayList<>();

        for (ChamberPointType type : values()) {
            processors.add(type.processor());
        }

        PROCESSORS = List.copyOf(processors);
    }

    private final @NotNull String id;
    private final @NotNull PointProcessor processor;

    ChamberPointType(@NotNull String id, @NotNull PointProcessor processor) {
        this.id = id;
        this.processor = processor;
    }

    /**
     * Returns all point processors for all chamber point types.
     *
     * @return immutable list of processors
     */
    public static List<PointProcessor> processors() {
        return PROCESSORS;
    }

    /**
     * Gets the string identifier of this point type.
     *
     * @return the type id
     */
    public @NotNull String id() {
        return id;
    }

    /**
     * Gets the processor associated with this point type.
     *
     * @return the point processor
     */
    public @NotNull PointProcessor processor() {
        return processor;
    }

}
