package studio.resonos.nano.core.arena.impl;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import studio.resonos.nano.NanoArenas;
import studio.resonos.nano.core.arena.Arena;
import studio.resonos.nano.core.arena.ArenaType;
import studio.resonos.nano.core.util.LocationUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class StandaloneArena extends Arena {

    public StandaloneArena(String name, Location location1, Location location2) {
        super(name, location1, location2);
    }

    @Override
    public ArenaType getType() {
        return ArenaType.STANDALONE;
    }

    @Override
    public void save() {
        String path = "arenas." + getName();

        FileConfiguration configuration = NanoArenas.get().getArenasConfig().getConfiguration();
        configuration.set(path, null);
        configuration.set(path + ".type", getType().name());
        configuration.set(path + ".icon.material", getIcon().getType().name());
        configuration.set(path + ".icon.durability", getIcon().getDurability());
        configuration.set(path + ".spawn", LocationUtil.serialize(spawn));
        configuration.set(path + ".cuboid.location1", LocationUtil.serialize(getLowerCorner()));
        configuration.set(path + ".cuboid.location2", LocationUtil.serialize(getUpperCorner()));
        configuration.set(path + ".resetDelay", getResetTime());

        try {
            configuration.save(NanoArenas.get().getArenasConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete() {
        super.delete();

        FileConfiguration configuration = NanoArenas.get().getArenasConfig().getConfiguration();
        configuration.set("arenas." + getName(), null);

        try {
            configuration.save(NanoArenas.get().getArenasConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
