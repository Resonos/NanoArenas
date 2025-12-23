package studio.resonos.nano.core.arena;

import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.CuboidRegion;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.inventory.ItemStack;
import studio.resonos.nano.NanoArenas;
import studio.resonos.nano.api.event.ArenaResetEvent;
import studio.resonos.nano.core.arena.impl.StandaloneArena;
import studio.resonos.nano.core.arena.selection.Schematic;
import studio.resonos.nano.core.util.CC;
import studio.resonos.nano.core.util.ItemBuilder;
import studio.resonos.nano.core.util.LocationUtil;
import studio.resonos.nano.core.util.cuboid.Cuboid;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Arena extends Cuboid {

    /*
     * Contains all arenas & kits of arenas
     * */
    @Getter
    private static final List<Arena> arenas = new ArrayList<>();
    @Getter
    private static final List<String> arenaNames = new ArrayList<>();

    @Getter private static final List<Entity> entities = new ArrayList<>();
    /*
     * Arena metadata
     * */
    @Getter
    protected String name, displayName;
    @Setter
    protected ItemStack icon;
    @Setter
    protected Location spawn;
    @Getter
    @Setter
    protected int resetTime = -1;
    @Getter
    @Setter
    private boolean autoResetPaused = false;

    private static final ExecutorService RESET_EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "Nano-Reset-Thread");
        t.setDaemon(true);
        return t;
    });

    private static final ConcurrentMap<String, Future<?>> resetTasks = new ConcurrentHashMap<>();

    /*
     * Default arena constructor
     * */
    public Arena(String name, Location location1, Location location2) {
        super(location1, location2);
        this.name = name;
        this.displayName = CC.translate("&b&l" + name);
        this.icon = getUniqueIcon();

    }

    public static void init() {
        FileConfiguration configuration = NanoArenas.get().getArenasConfig().getConfiguration();

        if (configuration.contains("arenas")) {
            for (String arenaName : configuration.getConfigurationSection("arenas").getKeys(false)) {
                String path = "arenas." + arenaName;

                Location location1 = LocationUtil.deserialize(configuration.getString(path + ".cuboid.location1"));
                Location location2 = LocationUtil.deserialize(configuration.getString(path + ".cuboid.location2"));

                Arena arena;

                arena = new StandaloneArena(arenaName, location1, location2);

                if (configuration.contains(path + ".icon")) {
                    arena.setIcon(new ItemBuilder(Material.valueOf(configuration.getString(path + ".icon.material")))
                            .durability(configuration.getInt(path + ".icon.durability"))
                            .build());
                }
                if (configuration.contains(path + ".spawn")) {
                    arena.setSpawn(LocationUtil.deserialize(configuration.getString(path + ".spawn")));
                }
                if (configuration.contains(path + ".resetDelay")) {
                    arena.setResetTime(configuration.getInt(path + ".resetDelay"));
                }

                Arena.getArenaNames().add(arena.getName());
                Arena.getArenas().add(arena);
            }
        }

        NanoArenas.get().getLogger().info("Loaded " + Arena.getArenas().size() + " arenas");
    }

    public static Arena getByName(String name) {
        for (Arena arena : arenas) {
            if (arena.getName() != null &&
                    arena.getName().equalsIgnoreCase(name)) {
                return arena;
            }
        }

        return null;
    }

    public static Material getRandomMaterial() {
        List<Material> valid = new ArrayList<>();
        for (Material m : Material.values()) {
            if (m != Material.AIR && m.isItem()) {
                valid.add(m);
            }
        }
        if (valid.isEmpty()) {
            return Material.STONE; // safe fallback
        }
        return valid.get(new Random().nextInt(valid.size()));
    }


    public static boolean isIconUnique(Material icon) {
        // Check if the given icon is unique among all arenas
        for (Arena arena : arenas) {
            if (arena.getIcon().getType() == icon) {
                return false; // Icon is not unique
            }
        }
        return true; // Icon is unique

    }

    public static ItemStack getUniqueIcon() {
        Material randIcon;
        // Iterate through arenas and check for unique icons
        do {
            randIcon = getRandomMaterial();
        } while (!isIconUnique(randIcon));

        return new ItemStack(randIcon);
    }



    public boolean isSetup() {
        return getLowerCorner() != null && getUpperCorner() != null /*&& spawn != null*/;
    }

    public Location getSpawn() {
        if (spawn == null) {
            return null;
        }

        return spawn.clone();
    }

    public void save() {

    }

    public void delete() {
        NanoArenas.get().getResetScheduler().cancel(this);
        arenaNames.remove(this.getName());
        arenas.remove(this);
    }

    public ItemStack getIcon() {
        return this.icon.clone();
    }

    public void createSchematic() {
        /*
         * Automatically create & save a schematic for the arena
         * */
        CuboidRegion region = new CuboidRegion(
                BukkitAdapter.adapt(getWorld()),
                LocationUtil.locationToBlockVector(getLowerCorner()),
                LocationUtil.locationToBlockVector(getUpperCorner()));

        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        // set schematic paste point to spawnA inorder to continue working with current system
        clipboard.setOrigin(LocationUtil.locationToBlockVector(getUpperCorner()));

        try (EditSession editSession = WorldEdit.getInstance()
                .getEditSessionFactory()
                .getEditSession(region.getWorld(), -1)) {

            ForwardExtentCopy forwardExtentCopy =
                    new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());

            forwardExtentCopy.setCopyingEntities(false);
            Operations.completeBlindly(forwardExtentCopy);
        }


        // ensure schematics directory exists (creates parents as needed)
        File schematicsDir = new File(NanoArenas.get().getDataFolder(), "data" + File.separator + "arenas");
        if (!schematicsDir.exists()) {
            schematicsDir.mkdirs();
        }

        File file = new File(schematicsDir, getName() + ".schem");

        try (ClipboardWriter writer = BuiltInClipboardFormat.FAST_V3.getWriter(Files.newOutputStream(file.toPath()))) {
            writer.write(clipboard);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Failed to save: " + getDisplayName());
            e.printStackTrace();
        }


    }

    public File getSchematicFile() {
        return new File(NanoArenas.get().getDataFolder(), "data" + File.separator + "arenas" + File.separator + getName() + ".schem");
    }

    public Schematic getSchematic() throws IOException {
        return new Schematic(getSchematicFile());
    }


    public void reset() {
        if (!isSetup()) {
            Bukkit.getConsoleSender().sendMessage(CC.translate("&8[&bNanoArenas&8] &cArena " + this.getName() + " is not setup correctly. Cannot reset."));
            return;
        }

        // Check if a reset is already in progress for this arena
        Future<?> existing = resetTasks.get(this.getName());
        if (existing != null && !existing.isDone()) {
            Bukkit.getConsoleSender().sendMessage(CC.translate("&8[&bNanoArenas&8] &eArena " + this.getName() + " is already being reset. Please wait."));
            return;
        }

        for (Entity entity : getWorld().getEntities()) {
            if (entity.getLocation().toVector().isInAABB(getLowerCorner().toVector(), getUpperCorner().toVector())) {
                if (entity instanceof org.bukkit.entity.Player) {
                    if (spawn != null) entity.teleport(spawn);
                } else if (entity instanceof Item || entity instanceof Projectile || entity instanceof EnderCrystal
                        || entity instanceof Minecart || entity instanceof Boat ||
                        entity instanceof FallingBlock || entity instanceof ExplosiveMinecart) {
                    entity.remove();
                }
            }
        }

        Future<?> future = RESET_EXECUTOR.submit(() -> {
            try {
                long start = System.currentTimeMillis();
                Schematic schematic = getSchematic();
                schematic.paste(getWorld(), getUpperX(), getUpperY(), getUpperZ());
                long end = System.currentTimeMillis();

                Bukkit.getServer().getScheduler().runTask(NanoArenas.get(), () -> {
                    Bukkit.getServer().getPluginManager().callEvent(new ArenaResetEvent(this, end - start, schematic.size));
                    Bukkit.getConsoleSender().sendMessage(CC.translate("&8[&bNanoArenas&8] &aReset arena " + this.getName() + " in " + (end - start) + "ms"));
                });
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getConsoleSender().sendMessage(CC.translate("&8[&bNanoArenas&8] &4Failed to reset arena &e" + this.getName() + ". &4Is the schematic file missing or corrupted?"));
            } finally {
                resetTasks.remove(this.getName());
            }
        });

        resetTasks.put(this.getName(), future);
    }

}
