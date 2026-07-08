package net.chronoschambers.chambers.world.impl;

import net.chronoschambers.chambers.model.Chamber;
import net.chronoschambers.chambers.world.ChamberWorld;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultChamberWorld implements ChamberWorld {

    private final NamespacedKey CHAMBER_ID_KEY = new NamespacedKey("chambers", "chamber_id");
    private final ConcurrentHashMap<String, Chamber> chambers = new ConcurrentHashMap<>();

    private final int chamberSpacing;
    private final Location currentLocation;

    public DefaultChamberWorld(@NotNull Location startingLocation, int chamberSpacing) {
        this.chamberSpacing = chamberSpacing;
        this.currentLocation = startingLocation;
    }

    @Override
    public Optional<Location> getNextLocation() {
        return Optional.of(currentLocation.add(chamberSpacing, 0, chamberSpacing).clone());
    }

    @Override
    public boolean isInChamber(@NotNull Location location) {
        return location.getChunk().getPersistentDataContainer().has(CHAMBER_ID_KEY, PersistentDataType.STRING);
    }

    @Override
    public Optional<Chamber> getChamberAt(@NotNull Location location) {
        Chunk chunk = location.getChunk();

        if (!chunk.getPersistentDataContainer().has(CHAMBER_ID_KEY, PersistentDataType.STRING)) {
            return Optional.empty();
        }

        return Optional.ofNullable(chambers.get(chunk.getPersistentDataContainer().get(CHAMBER_ID_KEY, PersistentDataType.STRING)));
    }

    @Override
    public void addChamber(@NotNull Chamber chamber) {
        chambers.put(chamber.id(), chamber);

        getChamberChunks(chamber).forEach(chunk -> chunk.getPersistentDataContainer().set(CHAMBER_ID_KEY, PersistentDataType.STRING, chamber.id()));
    }

    @Override
    public void removeChamber(@NotNull Chamber chamber) {
        chambers.remove(chamber.id());

        getChamberChunks(chamber).forEach(chunk -> chunk.getPersistentDataContainer().remove(CHAMBER_ID_KEY));
    }

    @Override
    public Collection<Chamber> chambers() {
        return List.copyOf(chambers.values());
    }

    private List<Chunk> getChamberChunks(@NotNull Chamber chamber) {
        Location minimumPoint = chamber.pasteResult().minimumPoint();
        Location maximumPoint = chamber.pasteResult().maximumPoint();

        int minChunkX = minimumPoint.getBlockX() >> 4;
        int maxChunkX = maximumPoint.getBlockX() >> 4;

        int minChunkZ = minimumPoint.getBlockZ() >> 4;
        int maxChunkZ = maximumPoint.getBlockZ() >> 4;

        List<Chunk> chunks = new ArrayList<>();

        for (int x = minChunkX; x <= maxChunkX; x++) {
            for (int z = minChunkZ; z <= maxChunkZ; z++) {
                Chunk chunk = minimumPoint.getWorld().getChunkAt(x, z);

                chunks.add(chunk);
            }
        }

        return chunks;
    }

}
