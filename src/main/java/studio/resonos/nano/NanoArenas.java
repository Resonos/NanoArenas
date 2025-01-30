package studio.resonos.nano;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import studio.resonos.nano.api.command.CommandHandler;
import studio.resonos.nano.api.gui.SpiGUI;
import studio.resonos.nano.core.arena.Arena;
import studio.resonos.nano.core.arena.ArenaListener;
import studio.resonos.nano.core.util.CC;
import studio.resonos.nano.core.util.Config;
import studio.resonos.nano.core.util.file.type.BasicConfigurationFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

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
        Collections.singletonList(
                new ArenaListener()
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
        registerProcessors();
        registerCommands();
        new BukkitRunnable() {
            @Override
            public void run() {
                NanoArenas.get().getLogger().info("Started Reset timer Task");
                scheduleArenaResets();
            }
        }.runTaskTimer(this, 0, 8400);
    }

//    public void pluginLicenseFail(){
//        Bukkit.getConsoleSender().sendMessage(CC.CHAT_BAR);
//        Bukkit.getConsoleSender().sendMessage(CC.translate(" &b&lNano Arenas"));
//        Bukkit.getConsoleSender().sendMessage(CC.translate(" "));
//        Bukkit.getConsoleSender().sendMessage(CC.translate(" &4ERROR: &cYour license key is invalid!"));
//        Bukkit.getConsoleSender().sendMessage(CC.translate(" &4ERROR: &CPlease check your license key and try again!"));
//        Bukkit.getConsoleSender().sendMessage(CC.CHAT_BAR);
//        Bukkit.getPluginManager().disablePlugin(this);
//    }
//
//    public void pluginLicenseKeyDefault(){
//        Bukkit.getConsoleSender().sendMessage(CC.CHAT_BAR);
//        Bukkit.getConsoleSender().sendMessage(CC.translate(" &b&lNano Arenas"));
//        Bukkit.getConsoleSender().sendMessage(CC.translate(" "));
//        Bukkit.getConsoleSender().sendMessage(CC.translate(" &4ERROR: &cYour license key seems like it is the default license in the config."));
//        Bukkit.getConsoleSender().sendMessage(CC.translate(" &4ERROR: &CPlease check your license key and try again!"));
//        Bukkit.getConsoleSender().sendMessage(CC.translate("&4If you have not purchased the plugin, please purchase it at https://discord.gg/G3BSFvsdZ5"));
//        Bukkit.getConsoleSender().sendMessage(CC.CHAT_BAR);
//        Bukkit.getPluginManager().disablePlugin(this);
//        System.exit(-1);
//    }

    @Override
    public void onDisable() {
        Arena.getArenas().forEach(Arena::reset);
        Arena.getArenas().forEach(Arena::save);
    }

    private void registerProcessors() {
        CommandHandler.registerProcessors("studio.resonos.arenas.api.command.processors", this);
    }

    private void registerCommands() {
        CommandHandler.registerCommands("studio.resonos.arenas.core.commands.arena", this);
    }

    private void scheduleArenaResets() {
        for (Arena arena : Arena.getArenas()) {
            if (!(arena.getResetTime() > 0)) {
                return;
            }
            long resetDelay = arena.getResetTime();
            new BukkitRunnable() {
                @Override
                public void run() {
                    arena.reset();

                    scheduleArenaResets();
                }
            }.runTaskLater(this, resetDelay);

            break;
        }
    }

}
