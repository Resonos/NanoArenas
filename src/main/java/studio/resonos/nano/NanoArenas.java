package studio.resonos.nano;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import studio.resonos.nano.api.command.CommandHandler;
import studio.resonos.nano.api.gui.SpiGUI;
import studio.resonos.nano.core.arena.Arena;
import studio.resonos.nano.core.arena.schedule.ArenaResetScheduler;
import studio.resonos.nano.core.util.CC;
import studio.resonos.nano.core.util.Config;
import studio.resonos.nano.core.util.file.type.BasicConfigurationFile;

import java.util.HashMap;
import java.util.Map;

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

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(CC.CHAT_BAR);
        Bukkit.getConsoleSender().sendMessage(CC.translate(" &b&lNano Arenas"));
        Bukkit.getConsoleSender().sendMessage(CC.translate(" "));
        Bukkit.getConsoleSender().sendMessage(CC.translate(" &aSuccessfully authenticated with the License Server!"));
        Bukkit.getConsoleSender().sendMessage(CC.translate(" &aThank you for purchasing Nano Arenas!"));
        Bukkit.getConsoleSender().sendMessage(CC.CHAT_BAR);
        nanoArenas = this;
        arenasConfig = new BasicConfigurationFile(this, "arenas");
        spiGUI = new SpiGUI(this);
        Arena.init();
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
        System.out.println("Registered Commands");
    }

}
