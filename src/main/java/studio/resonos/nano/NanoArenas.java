package studio.resonos.nano;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import studio.resonos.nano.api.command.CommandHandler;
import studio.resonos.nano.api.gui.SpiGUI;
import studio.resonos.nano.core.arena.Arena;
import studio.resonos.nano.core.arena.ArenaListener;
import studio.resonos.nano.core.util.CC;
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
 * @Author Athishh
 * Package: studio.resonos.arenas.core.arena.generator
 * Created on: 12/16/2023
 */

@Getter
@Setter
public class NanoArenas extends JavaPlugin {

    public static SpiGUI spiGUI;
    private static NanoArenas nanoArenas;
    @Getter
    private BasicConfigurationFile arenasConfig,mainConfig;


    public static NanoArenas get() {
        return nanoArenas;
    }

    @Override
    public void onEnable() {
        mainConfig=new BasicConfigurationFile(this,"config");
        String licenseKey=mainConfig.getString("CONFIG.LICENSE_KEY");
        final String apiURL = "http://license.java.api.resonos.studio:3000/api/client";
        final String apiKey = "hjVfr{J&M2q{gRtdytnHvs-Sd#voJe;d%Jl(yLbjbhK~F~4C&6";
        final String product = "NanoArenas";

        try {
            URL url = new URL(apiURL);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", apiKey);
            connection.setDoOutput(true);

            String jsonInputString = String.format("{\"licensekey\": \"%s\", \"product\": \"%s\"}", licenseKey, product);

            try(OutputStream outputStream = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                outputStream.write(input, 0, input.length);
            }

            try(BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                // You can use 3rd Party libarys to parse and handle the JSON object
                // I'm using javas inbuilt features to check the response string

                if(response.toString().contains("\"status_id\":\"SUCCESS\"")){
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
                else{

                    pluginLicenseFail();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void pluginLicenseFail(){
        Bukkit.getConsoleSender().sendMessage(CC.CHAT_BAR);
        Bukkit.getConsoleSender().sendMessage(CC.translate(" &b&lNano Arenas"));
        Bukkit.getConsoleSender().sendMessage(CC.translate(" "));
        Bukkit.getConsoleSender().sendMessage(CC.translate(" &4ERROR: &cYour license key is invalid!"));
        Bukkit.getConsoleSender().sendMessage(CC.translate(" &4ERROR: &CPlease check your license key and try again!"));
        Bukkit.getConsoleSender().sendMessage(CC.CHAT_BAR);
        Bukkit.getPluginManager().disablePlugin(this);
    }

    public void pluginLicenseKeyDefault(){
        Bukkit.getConsoleSender().sendMessage(CC.CHAT_BAR);
        Bukkit.getConsoleSender().sendMessage(CC.translate(" &b&lNano Arenas"));
        Bukkit.getConsoleSender().sendMessage(CC.translate(" "));
        Bukkit.getConsoleSender().sendMessage(CC.translate(" &4ERROR: &cYour license key seems like it is the default license in the config."));
        Bukkit.getConsoleSender().sendMessage(CC.translate(" &4ERROR: &CPlease check your license key and try again!"));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&4If you have not purchased the plugin, please purchase it at https://discord.gg/G3BSFvsdZ5"));
        Bukkit.getConsoleSender().sendMessage(CC.CHAT_BAR);
        Bukkit.getPluginManager().disablePlugin(this);
        System.exit(-1);
    }

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
