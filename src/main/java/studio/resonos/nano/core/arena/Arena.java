package studio.resonos.nano.core.arena;

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
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import studio.resonos.nano.NanoArenas;
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
    private List<String> kits = new ArrayList<>();
    @Getter
    @Setter
    private List<Location> placedBlocks;
    @Getter
    @Setter
    private List<BlockState> changedBlocks;


    /*
     * Default arena constructor
     * */
    public Arena(String name, Location location1, Location location2) {
        super(location1, location2);
        this.name = name;
        this.displayName = CC.translate("&d&l" + name);
        this.icon = getUniqueIcon();
        this.placedBlocks = new ArrayList<>();
        this.changedBlocks = new ArrayList<>();
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
            if (arena.getType() != ArenaType.DUPLICATE && arena.getName() != null &&
                    arena.getName().equalsIgnoreCase(name)) {
                return arena;
            }
        }

        return null;
    }

    public static Material getRandomMaterial() {
        // Add all possible materials to the array
        Material[] materials = Material.values();

        // Get a random material from the array
        Random random = new Random();
        return materials[random.nextInt(materials.length)];
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

    public ArenaType getType() {
        return ArenaType.DUPLICATE;
    }

    public boolean isSetup() {
        return getLowerCorner() != null && getUpperCorner() != null && spawn != null;
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
        clipboard.setOrigin(LocationUtil.locationToBlockVector(getSpawn()));
        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(region.getWorld(), -1);

        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());
        forwardExtentCopy.setCopyingEntities(false);
        Operations.complete(forwardExtentCopy);

        // ensure schematics directory exists (creates parents as needed)
        File schematicsDir = new File(NanoArenas.get().getDataFolder(), "data" + File.separator + "arenas");
        if (!schematicsDir.exists()) {
            schematicsDir.mkdirs();
        }

        File file = new File(schematicsDir, getName() + ".nano");

        try (ClipboardWriter writer = BuiltInClipboardFormat.FAST.getWriter(Files.newOutputStream(file.toPath()))) {
            writer.write(clipboard);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Failed to save: " + getDisplayName());
            e.printStackTrace();
        }
    }

    public File getSchematicFile() {
        return new File(NanoArenas.get().getDataFolder(), "data" + File.separator + "arenas" + File.separator + getName() + ".nano");
    }

    public Schematic getSchematic() throws IOException {
        return new Schematic(getSchematicFile());
    }


    public void reset() {
        if (isSetup()) {
            try {
                long start = System.currentTimeMillis();

                Schematic schematic = getSchematic();
                schematic.paste(getWorld(), getSpawn().getBlockX(), getSpawn().getBlockY(), getSpawn().getBlockZ());

                long end = System.currentTimeMillis();
                NanoArenas.get().getLogger().info("Reset arena " + this.getName() + " in " + (end - start) + "ms");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            NanoArenas.get().getLogger().info("Arena " + this.getName() + " is not setup correctly. Cannot reset.");
        }

    }

}
