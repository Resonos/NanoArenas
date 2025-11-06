package studio.resonos.nano;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import studio.resonos.nano.api.command.CommandHandler;
import studio.resonos.nano.api.gui.SpiGUI;
import studio.resonos.nano.core.arena.Arena;
import studio.resonos.nano.core.arena.listener.ArenaResetBroadcastListener;
import studio.resonos.nano.core.arena.schedule.ArenaResetScheduler;
import studio.resonos.nano.core.managers.AdminAlertManager;
import studio.resonos.nano.core.util.CC;
import studio.resonos.nano.core.util.Config;
import studio.resonos.nano.core.util.file.type.BasicConfigurationFile;

/**
 * @Author Athulsib
 * Package: studio.resonos.arenas.core.arena.generator
 * Created on: 12/16/2023
 */

@Getter
@Setter
public class NanoArenas extends JavaPlugin {

    public static SpiGUI spiGUI;
    private static NanoArenas nanoArenas;
    @Getter
    private BasicConfigurationFile arenasConfig;
    public Config mainConfig;

    public static NanoArenas get() {
        return nanoArenas;
    }
    private ArenaResetScheduler resetScheduler;
    private AdminAlertManager manager;

    @Override
    public void onEnable() {
        String str = """
                  _   _          _   _  ____ \s
                 | \\ | |   /\\   | \\ | |/ __ \\\s
                 |  \\| |  /  \\  |  \\| | |  | |
                 | . ` | / /\\ \\ | . ` | |  | |
                 | |\\  |/ ____ \\| |\\  | |__| |
                 |_| \\_/_/    \\_\\_| \\_|\\____/\s
                
                
                """;
        Bukkit.getConsoleSender().sendMessage(CC.CHAT_BAR);
        //Bukkit.getConsoleSender().sendMessage(CC.translate(" &b&lNano Arenas"));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&b  _   _          _   _  ____  "));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&b  | \\ | |   /\\   | \\ | |/ __ \\ "));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&b  |  \\| |  /  \\  |  \\| | |  | |"));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&b  | . ` | / /\\ \\ | . ` | |  | |"));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&b  | |\\  |/ ____ \\| |\\  | |__| |"));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&b  |_| \\_/_/    \\_\\_| \\_|\\____/ "));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&b "));
        Bukkit.getConsoleSender().sendMessage(CC.translate((" &b▐ &fAuthor&7: &bAthishh")));
        Bukkit.getConsoleSender().sendMessage(CC.translate((" &b▐ &fVersion &7: &b" + getDescription().getVersion())));
        Bukkit.getConsoleSender().sendMessage(CC.translate(" "));
        Bukkit.getConsoleSender().sendMessage(CC.translate(" &aThank you for Using Nano Arenas!"));
        Bukkit.getConsoleSender().sendMessage(CC.translate(" &aJoin our discord for support &b&ndsc.gg/resonos"));
        Bukkit.getConsoleSender().sendMessage(CC.CHAT_BAR);
        nanoArenas = this;
        arenasConfig = new BasicConfigurationFile(this, "arenas");
        spiGUI = new SpiGUI(this);
        manager = new AdminAlertManager();
        Arena.init();
        Bukkit.getServer().getPluginManager().registerEvents(new ArenaResetBroadcastListener(manager), this);
        resetScheduler = new ArenaResetScheduler(this);
        registerProcessors();
        registerCommands();
        // schedule a short delayed startup pass to allow arenas to load first
        new BukkitRunnable() {
            @Override
            public void run() {
                NanoArenas.get().getLogger().info("Started Reset timer Task");
                resetScheduler.scheduleAll();
            }
        }.runTaskLater(this, 10 * 20L);
    }


    @Override
    public void onDisable() {
        // cancel scheduler tasks
        if (resetScheduler != null) {
            resetScheduler.cancelAll();
        };

        Arena.getArenas().forEach(Arena::reset);
        Arena.getArenas().forEach(Arena::save);
    }

    private void registerProcessors() {
        CommandHandler.registerProcessors("studio.resonos.nano.api.command.processors", this);
    }

    private void registerCommands() {
        CommandHandler.registerCommands("studio.resonos.nano.core.commands.arena", this);
        CommandHandler.registerCommands("studio.resonos.nano.core.commands.dev", this);
    }

}
