# ChronosChambers

A Minecraft plugin library for generating and managing chamber structures. ChronosChambers provides a flexible API for schematic-based chamber generation with point-of-interest tracking and world management.

## Features

- **Schematic-Based Generation**: Generate chambers from WorldEdit schematics with rotation support
- **World Management**: Automatic chamber placement and space allocation in dedicated worlds
- **Point Collection**: Extract and track points of interest within chambers (signs, spawn points, etc.)
- **FAWE Integration**: Built-in FastAsyncWorldEdit integration for efficient schematic pasting
- **Async Operations**: Non-blocking chamber generation with callback-based API
- **Extensible Architecture**: Pluggable processors and collectors for custom point extraction

## Requirements

- **Java**: 21
- **Minecraft Server**: PaperMC 1.21.11
- **Plugins**: 
- **FastAsyncWorldEdit (FAWE)** (if using FAWEPasteService)

## Installation

1. Build the project:
   ```bash
   mvn clean package
   ```

2. Place the generated JAR file (with dependencies) in your server's `plugins` folder

3. Restart the server

## Building

```bash
mvn clean package
```

The build process creates a shaded JAR with all dependencies included using the Maven Shade Plugin.

## Usage

### Basic Chamber Generation

```java
// Create a chamber world
ChamberWorld world = new DefaultChamberWorld(world, spacing);

// Create a paste service (FAWE integration)
PasteService pasteService = new FAWEPasteService(plugin);

// Create a scheduler
Scheduler scheduler = new BukkitScheduler(plugin);

// Create the chamber generator
ChamberGenerator generator = new ChamberGenerator(
    scheduler,
    world,
    pasteService,
    () -> new DefaultPointCollector()
);

// Generate a chamber
generator.generate(
    "chamber-1",
    "my_schematic",
    List.of(new SignPointProcessor()),
    chamber -> {
        if (chamber.isPresent()) {
            plugin.getLogger().info("Chamber generated successfully!");
        }
    }
);
```

### Point Processing

Implement custom `PointProcessor` to extract specific points from chambers:

```java
public class SpawnPointProcessor implements PointProcessor {
    @Override
    public void process(PasteResult pasteResult, PointCollector collector) {
        // Extract spawn points from the pasted schematic
        Location spawnLoc = /* find spawn location */;
        collector.addPoint(new ChamberPoint("spawns", spawnLoc));
    }
}
```

### Accessing Chamber Points

```java
Chamber chamber = /* get chamber */;
PointCollector collector = chamber.collector();

// Get all points in a group
Collection<ChamberPoint> spawnPoints = collector.getPoints("spawns");

// Get points by type
Collection<ChamberPoint> signPoints = collector.getPointsByType(ChamberPointType.SIGN);
```

## Architecture

### Core Components

- **`ChamberGenerator`**: Handles chamber generation and placement
- **`ChamberWorld`**: Manages world space and chamber tracking
- **`PasteService`**: Abstracts schematic pasting operations
- **`PointCollector`**: Collects and organizes points within chambers
- **`PointProcessor`**: Processes paste results to extract points
- **`Scheduler`**: Provides async task execution

## API Reference

### ChamberGenerator

```java
// Generate with no rotation
void generate(String id, String schematicName, 
             List<PointProcessor> processors, 
             Consumer<Optional<Chamber>> callback)

// Generate with rotation
void generate(String id, String schematicName, float rotation,
             List<PointProcessor> processors,
             Consumer<Optional<Chamber>> callback)
```

### ChamberWorld

```java
Optional<Location> getNextLocation()
boolean isInChamber(Location location)
Optional<Chamber> getChamberAt(Location location)
void addChamber(Chamber chamber)
void removeChamber(Chamber chamber)
Collection<Chamber> chambers()
```

## Dependencies

- [Paper API](https://papermc.io/) 1.21.11-R0.1-SNAPSHOT (provided)
- [FastAsyncWorldEdit](https://github.com/IntellectualSites/FastAsyncWorldEdit) (provided)

## License

MIT