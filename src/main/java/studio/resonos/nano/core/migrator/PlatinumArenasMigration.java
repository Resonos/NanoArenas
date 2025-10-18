package studio.resonos.nano.core.migrator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import studio.resonos.nano.NanoArenas;
import studio.resonos.nano.core.arena.Arena;
import studio.resonos.nano.core.arena.impl.StandaloneArena;
import studio.resonos.nano.core.arena.schedule.ArenaResetScheduler;
import studio.resonos.nano.core.util.CC;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class PlatinumArenasMigration {

    private static final byte SECTION_SPLIT = '\u0002';

    public static List<Arena> migrateFromPlatinumArenas() {
        File pluginsFolder = NanoArenas.get().getDataFolder().getParentFile();
        File platinumArenasFolder = new File(pluginsFolder, "PlatinumArenas");

        List<Arena> migratedArenas = new ArrayList<>();

        if (!platinumArenasFolder.exists() || !platinumArenasFolder.isDirectory()) {
            NanoArenas.get().getLogger().warning("PlatinumArenas folder not found: " + platinumArenasFolder.getPath());
            return migratedArenas;
        }

        File arenasFolder = new File(platinumArenasFolder, "Arenas");
        if (!arenasFolder.exists()) {
            NanoArenas.get().getLogger().warning("PlatinumArenas/Arenas folder not found");
            return migratedArenas;
        }

        File[] arenaFiles = arenasFolder.listFiles((dir, name) ->
                name.toLowerCase().endsWith(".dat") || name.toLowerCase().endsWith(".datc"));

        if (arenaFiles == null || arenaFiles.length == 0) {
            NanoArenas.get().getLogger().info("No PlatinumArenas arena files found");
            return migratedArenas;
        }

        Bukkit.getConsoleSender().sendMessage(CC.translate("&8[&bNanoArenas&8] &fFound " + arenaFiles.length + " PlatinumArenas arena files to migrate."));
        Bukkit.getConsoleSender().sendMessage(CC.translate("&8[&bNanoArenas&8] &fStarting migration of PlatinumArenas arenas..."));

        long startTime = System.currentTimeMillis();
        int successCount = 0;

        for (File file : arenaFiles) {
            try {
                long fileStartTime = System.currentTimeMillis();
                Arena arena = loadPlatinumArenaBasicInfo(file);
                migratedArenas.add(arena);
                successCount++;
                Arena.getArenas().add(arena);
                Arena.getArenaNames().add(arena.getName());
                arena.save();
                arena.createSchematic();
                NanoArenas.get().getResetScheduler().schedule(arena); // add to scheduling
                long fileDuration = System.currentTimeMillis() - fileStartTime;
                Bukkit.getConsoleSender().sendMessage(CC.translate("&8[&bNanoArenas&8] &fMigrated arena &b" + arena.getName() + " &fin &a" + fileDuration + "ms"));
            } catch (Exception e) {
                NanoArenas.get().getLogger().warning("Failed to migrate arena from file: " + file.getName());
                e.printStackTrace();
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        Bukkit.getConsoleSender().sendMessage(CC.translate("&8[&bNanoArenas&8] &fMigrated &a" + successCount + " &farenas from &ePlatinumArenas &fin &a" + duration + "ms"));

        return migratedArenas;
    }

    private static Arena loadPlatinumArenaBasicInfo(File file) throws Exception {
        byte[] readBytes = Files.readAllBytes(file.toPath());

        if (file.getName().toLowerCase().endsWith(".datc")) {
            readBytes = decompress(readBytes);
            if (readBytes == null) {
                throw new Exception("Failed to decompress arena file");
            }
        }

        int firstSectionSplit = indexOf(readBytes, SECTION_SPLIT);
        if (firstSectionSplit == -1) {
            throw new Exception("Invalid arena file format - no section split found");
        }

        byte[] header = Arrays.copyOfRange(readBytes, 0, firstSectionSplit);
        String headerString = new String(header, StandardCharsets.US_ASCII);

        String[] headerParts = headerString.split(",");

        if (headerParts.length < 9) {
            throw new Exception("Invalid header format");
        }

        String name = headerParts[1];
        String worldName = headerParts[2];
        int x1 = Integer.parseInt(headerParts[3]);
        int y1 = Integer.parseInt(headerParts[4]);
        int z1 = Integer.parseInt(headerParts[5]);
        int x2 = Integer.parseInt(headerParts[6]);
        int y2 = Integer.parseInt(headerParts[7]);
        int z2 = Integer.parseInt(headerParts[8]);

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            NanoArenas.get().getLogger().warning("World '" + worldName + "' not found for arena '" + name + "', using default world");
            world = Bukkit.getWorlds().get(0);
        }

        Location corner1 = new Location(world, x1, y1, z1);
        Location corner2 = new Location(world, x2, y2, z2);

        return new StandaloneArena(name, corner1, corner2);
    }

    private static byte[] decompress(byte[] bytes) {
        Inflater decompresser = new Inflater();
        decompresser.setInput(bytes);

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bytes.length);
            byte[] buffer = new byte[1024];
            while (!decompresser.finished()) {
                int count = decompresser.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            decompresser.end();
            return outputStream.toByteArray();
        } catch (DataFormatException e) {
            e.printStackTrace();
            decompresser.end();
        }
        return null;
    }

    private static int indexOf(byte[] array, byte value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value) {
                return i;
            }
        }
        return -1;
    }
}