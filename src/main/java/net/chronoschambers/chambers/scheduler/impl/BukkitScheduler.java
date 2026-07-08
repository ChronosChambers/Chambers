package net.chronoschambers.chambers.scheduler.impl;

import net.chronoschambers.chambers.scheduler.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class BukkitScheduler implements Scheduler {

    private final @NotNull JavaPlugin plugin;

    public BukkitScheduler(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void async(@NotNull Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    @Override
    public void sync(@NotNull Runnable runnable) {
        Bukkit.getScheduler().runTask(plugin, runnable);
    }

}
