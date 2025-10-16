package studio.resonos.nano.core.arena.selection;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.world.World;
import lombok.Data;
import org.bukkit.entity.Player;
import studio.resonos.nano.core.util.CC;

@Data
public class Selection {

    public static Region createOrGetSelection(Player player) {

        com.sk89q.worldedit.entity.Player actor = BukkitAdapter.adapt(player); // WorldEdit's native Player class extends Actor
        SessionManager manager = WorldEdit.getInstance().getSessionManager();
        LocalSession localSession = manager.get(actor);

        // note: not necessarily the player's current world, see the concepts page
        World selectionWorld = localSession.getSelectionWorld();
        try {
            if (selectionWorld == null) throw new IncompleteRegionException();
            return localSession.getSelection(selectionWorld);
        } catch (IncompleteRegionException ex) {
            player.sendMessage(CC.translate("&8[&bNanoArenas&8] &cYour selection is incomplete."));
            return null;
        }
    }

}