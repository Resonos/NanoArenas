package studio.resonos.nano.core.util;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * @Author: Athishh
 * Package: me.athishh.lotus.core.util
 * Created on: 12/22/2023
 */
public class VectorUtil {

    public static org.bukkit.Location vectorToLocation(World world, Vector3 vector) {
        return new org.bukkit.Location(
                world,
                vector.getBlockX(),
                vector.getBlockY(),
                vector.getBlockZ()
        );
    }

    public static Location vectorToLocation(World world, BlockVector3 vector) {
        return new Location(
                world,
                vector.x(),
                vector.y(),
                vector.z()
        );
    }


}