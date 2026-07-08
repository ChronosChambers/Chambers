package net.chronoschambers.chambers.world;

import net.chronoschambers.chambers.model.Chamber;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

/**
 * Represents a world that manages chamber placement and tracking.
 */
public interface ChamberWorld {

    /**
     * Gets the next available location for chamber generation.
     *
     * @return an empty optional if no space is available
     */
    Optional<Location> getNextLocation();

    /**
     * Checks whether a location is inside any registered chamber.
     *
     * @param location the location to check
     * @return true if the location is inside a chamber
     */
    boolean isInChamber(@NotNull Location location);

    /**
     * Gets the chamber located at a specific position.
     *
     * @param location the location to query
     * @return the chamber if found at the location
     */
    Optional<Chamber> getChamberAt(@NotNull Location location);

    /**
     * Registers a chamber into the world.
     *
     * @param chamber the chamber to add
     */
    void addChamber(@NotNull Chamber chamber);

    /**
     * Removes a chamber from the world.
     *
     * @param chamber the chamber to remove
     */
    void removeChamber(@NotNull Chamber chamber);

    /**
     * Gets all registered chambers.
     *
     * @return collection of chambers
     */
    Collection<Chamber> chambers();

}
