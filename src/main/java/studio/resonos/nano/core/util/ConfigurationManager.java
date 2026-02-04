package studio.resonos.nano.core.util;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Manages all plugin configuration with reload capability
 */
@Getter
public class ConfigurationManager {
    
    private final JavaPlugin plugin;
    private final Logger logger;
    private File configFile;
    private FileConfiguration config;
    
    // Default configuration values
    private static final int DEFAULT_RESET_INTERVAL_TICKS = 20;
    private static final boolean DEFAULT_AUTO_RESET_ENABLED = true;
    private static final boolean DEFAULT_ALERTS_ENABLED = true;
    private static final boolean DEFAULT_DEBUG_MODE = false;
    
    public ConfigurationManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        createConfig();
    }
    
    /**
     * Creates the config file with default values
     */
    private void createConfig() {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        addDefaults();
    }
    
    /**
     * Adds default configuration values if they don't exist
     */
    private void addDefaults() {
        // General Settings
        config.addDefault("general.debug-mode", DEFAULT_DEBUG_MODE);
        config.addDefault("general.language", "en");
        
        // Reset Settings
        config.addDefault("reset.enabled", DEFAULT_AUTO_RESET_ENABLED);
        config.addDefault("reset.interval-ticks", DEFAULT_RESET_INTERVAL_TICKS);
        config.addDefault("reset.max-concurrent-resets", 5);
        config.addDefault("reset.reset-timeout-seconds", 30);
        
        // Entity Cleanup Settings
        config.addDefault("cleanup.entities.remove-players", false);
        config.addDefault("cleanup.entities.remove-items", true);
        config.addDefault("cleanup.entities.remove-projectiles", true);
        config.addDefault("cleanup.entities.remove-ender-crystals", true);
        config.addDefault("cleanup.entities.remove-minecarts", true);
        config.addDefault("cleanup.entities.remove-boats", true);
        config.addDefault("cleanup.entities.remove-falling-blocks", true);
        config.addDefault("cleanup.entities.remove-explosive-minecarts", true);
        config.addDefault("cleanup.entities.custom-types", Arrays.asList("ARMOR_STAND", "ITEM_FRAME"));
        
        // Schematic Settings
        config.addDefault("schematic.format", "FAST_V3");
        config.addDefault("schematic.directory", "data/arenas");
        config.addDefault("schematic.copy-entities", false);
        config.addDefault("schematic.auto-create-on-save", true);
        config.addDefault("schematic.paste.copy-entities", false);
        config.addDefault("schematic.paste.copy-biomes", true);
        config.addDefault("schematic.paste.ignore-air-blocks", false);
        config.addDefault("schematic.paste.allow-air", true);
        config.addDefault("schematic.paste.fast-mode", true);
        
        // Alert System Settings
        config.addDefault("alerts.enabled", DEFAULT_ALERTS_ENABLED);
        config.addDefault("alerts.auto-enable-on-join", true);
        config.addDefault("alerts.reset-message", "&8[&bNanoArenas&8] &fArena &b{arena}&f has been reset&7(&e{size}&f)&7(&a{duration}ms&f)&7.");
        
        // Permission Settings
        config.addDefault("permissions.admin", "nano.admin");
        config.addDefault("permissions.arena", "nano.arena");
        config.addDefault("permissions.alerts", "nano.alerts");
        config.addDefault("permissions.kit", "nano.kit");
        config.addDefault("permissions.reload", "nano.reload");
        
        // Message Settings
        config.addDefault("messages.prefix", "&8[&bNanoArenas&8] ");
        config.addDefault("messages.color.success", "&a");
        config.addDefault("messages.color.error", "&c");
        config.addDefault("messages.color.info", "&b");
        config.addDefault("messages.color.warning", "&e");
        
        // GUI Settings
        config.addDefault("gui.rows", 6);
        config.addDefault("gui.auto-refresh-interval-ticks", 20);
        config.addDefault("gui.close-on-outside-click", true);
        
        // Performance Settings
        config.addDefault("performance.async-resets", true);
        config.addDefault("performance.reset-thread-pool-size", 2);
        config.addDefault("performance.lag-detection.enabled", true);
        config.addDefault("performance.lag-detection.threshold-ms", 100);
        config.addDefault("performance.lag-detection.skip-reset-threshold-ms", 500);
        
        // Save defaults
        if (!configFile.exists()) {
            config.options().copyDefaults(true);
            saveConfig();
        }
    }
    
    /**
     * Reloads the configuration from file
     */
    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        addDefaults();
        logger.info("Configuration reloaded successfully!");
    }
    
    /**
     * Saves the configuration to file
     */
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            logger.severe("Could not save config to " + configFile + ": " + e.getMessage());
        }
    }
    
    // General Settings Getters
    public boolean isDebugMode() {
        return config.getBoolean("general.debug-mode");
    }
    
    public String getLanguage() {
        return config.getString("general.language", "en");
    }
    
    // Reset Settings Getters
    public boolean isResetEnabled() {
        return config.getBoolean("reset.enabled");
    }
    
    public int getResetIntervalTicks() {
        return config.getInt("reset.interval-ticks", DEFAULT_RESET_INTERVAL_TICKS);
    }
    
    public int getMaxConcurrentResets() {
        return config.getInt("reset.max-concurrent-resets", 5);
    }
    
    public int getResetTimeoutSeconds() {
        return config.getInt("reset.reset-timeout-seconds", 30);
    }
    
    // Entity Cleanup Settings Getters
    public boolean shouldTeleportPlayers() {
        return config.getBoolean("cleanup.entities.remove-players", false);
    }
    
    public boolean shouldRemoveItems() {
        return config.getBoolean("cleanup.entities.remove-items", true);
    }
    
    public boolean shouldRemoveProjectiles() {
        return config.getBoolean("cleanup.entities.remove-projectiles", true);
    }
    
    public boolean shouldRemoveEnderCrystals() {
        return config.getBoolean("cleanup.entities.remove-ender-crystals", true);
    }
    
    public boolean shouldRemoveMinecarts() {
        return config.getBoolean("cleanup.entities.remove-minecarts", true);
    }
    
    public boolean shouldRemoveBoats() {
        return config.getBoolean("cleanup.entities.remove-boats", true);
    }
    
    public boolean shouldRemoveFallingBlocks() {
        return config.getBoolean("cleanup.entities.remove-falling-blocks", true);
    }
    
    public boolean shouldRemoveExplosiveMinecarts() {
        return config.getBoolean("cleanup.entities.remove-explosive-minecarts", true);
    }
    
    public List<String> getCustomEntityTypes() {
        return config.getStringList("cleanup.entities.custom-types");
    }
    
    // Schematic Settings Getters
    public String getSchematicFormat() {
        return config.getString("schematic.format", "FAST_V3");
    }
    
    public String getSchematicDirectory() {
        return config.getString("schematic.directory", "data/arenas");
    }
    
    public boolean shouldCopyEntities() {
        return config.getBoolean("schematic.copy-entities", false);
    }
    
    public boolean shouldAutoCreateSchematicOnSave() {
        return config.getBoolean("schematic.auto-create-on-save", true);
    }
    
    // Schematic Paste Settings Getters
    public boolean shouldPasteCopyEntities() {
        return config.getBoolean("schematic.paste.copy-entities", false);
    }
    
    public boolean shouldPasteCopyBiomes() {
        return config.getBoolean("schematic.paste.copy-biomes", true);
    }
    
    public boolean shouldPasteIgnoreAirBlocks() {
        return config.getBoolean("schematic.paste.ignore-air-blocks", false);
    }
    
    public boolean shouldPasteAllowAir() {
        return config.getBoolean("schematic.paste.allow-air", true);
    }
    
    public boolean shouldPasteFastMode() {
        return config.getBoolean("schematic.paste.fast-mode", true);
    }
    
    // Alert System Settings Getters
    public boolean isAlertsEnabled() {
        return config.getBoolean("alerts.enabled", DEFAULT_ALERTS_ENABLED);
    }
    
    public boolean shouldAutoEnableAlertsOnJoin() {
        return config.getBoolean("alerts.auto-enable-on-join", true);
    }
    
    public String getResetMessageTemplate() {
        return config.getString("alerts.reset-message", "&8[&bNanoArenas&8] &fArena &b{arena}&f has been reset&7(&e{size}&f)&7(&a{duration}ms&f)&7.");
    }
    
    // Permission Settings Getters
    public String getAdminPermission() {
        return config.getString("permissions.admin", "nano.admin");
    }
    
    public String getArenaPermission() {
        return config.getString("permissions.arena", "nano.arena");
    }
    
    public String getAlertsPermission() {
        return config.getString("permissions.alerts", "nano.alerts");
    }
    
    public String getKitPermission() {
        return config.getString("permissions.kit", "nano.kit");
    }
    
    public String getReloadPermission() {
        return config.getString("permissions.reload", "nano.reload");
    }
    
    // Message Settings Getters
    public String getMessagePrefix() {
        return config.getString("messages.prefix", "&8[&bNanoArenas&8] ");
    }
    
    public String getSuccessColor() {
        return config.getString("messages.color.success", "&a");
    }
    
    public String getErrorColor() {
        return config.getString("messages.color.error", "&c");
    }
    
    public String getInfoColor() {
        return config.getString("messages.color.info", "&b");
    }
    
    public String getWarningColor() {
        return config.getString("messages.color.warning", "&e");
    }
    
    // GUI Settings Getters
    public int getGuiRows() {
        return config.getInt("gui.rows", 6);
    }
    
    public int getGuiAutoRefreshIntervalTicks() {
        return config.getInt("gui.auto-refresh-interval-ticks", 20);
    }
    
    public boolean shouldCloseGuiOnOutsideClick() {
        return config.getBoolean("gui.close-on-outside-click", true);
    }
    
    // Performance Settings Getters
    public boolean isAsyncResetsEnabled() {
        return config.getBoolean("performance.async-resets", true);
    }
    
    public int getResetThreadPoolSize() {
        return config.getInt("performance.reset-thread-pool-size", 2);
    }
    
    public boolean isLagDetectionEnabled() {
        return config.getBoolean("performance.lag-detection.enabled", true);
    }
    
    public int getLagThresholdMs() {
        return config.getInt("performance.lag-detection.threshold-ms", 100);
    }
    
    public int getSkipResetThresholdMs() {
        return config.getInt("performance.lag-detection.skip-reset-threshold-ms", 500);
    }
}
