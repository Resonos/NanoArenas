package studio.resonos.nano.core.arena.selection;

import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import lombok.Getter;
import org.bukkit.World;
import studio.resonos.nano.core.arena.impl.StandaloneArena;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @Author Athishh
 * Package: me.athishh.lotus.core.arena.generator
 * Created on: 12/16/2023
 */
@Getter
public class Schematic {

    private final Clipboard clipBoard;
    public long size;

    public Schematic(File file) throws IOException {
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        assert format != null;
        try (ClipboardReader reader = format.getReader(Files.newInputStream(file.toPath()))) {
            clipBoard = reader.read();
            size = (clipBoard.getRegion().getVolume());
        }
    }

    public void paste(World world, int x, int y, int z) {
        try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(world), -1)) {
            editSession.setFastMode(true);
            Operation operation = new ClipboardHolder(clipBoard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(x, y, z))
                    .copyEntities(false)
                    .copyBiomes(true)
                    .ignoreAirBlocks(false)
                    .build();
            Operations.completeBlindly(operation);
            //Operations.complete(operation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}