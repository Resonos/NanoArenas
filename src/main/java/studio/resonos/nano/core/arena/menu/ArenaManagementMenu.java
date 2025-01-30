package studio.resonos.nano.core.arena.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import studio.resonos.nano.NanoArenas;
import studio.resonos.nano.api.gui.buttons.SGButton;
import studio.resonos.nano.api.gui.menu.SGMenu;
import studio.resonos.nano.core.arena.Arena;
import studio.resonos.nano.core.util.ItemBuilder;

/**
 * @Author Athulsib
 * Package: studio.resonos.arenas.core.arena.menu
 * Created on: 12/16/2023
 */
public class ArenaManagementMenu {

    public static void openMenu(Player player) {

        // Create a GUI with calculated rows
        SGMenu Menu = NanoArenas.spiGUI.create("&dArena Management", 2);

        Menu.setAutomaticPaginationEnabled(true);

        for (Arena arena : Arena.getArenas()) {
            Menu.addButton(new SGButton(new ItemBuilder(arena.getIcon())
                    .name("&d&l" + arena.getName())
                    .lore("")
                    .lore("&dArena Information:")
                    .lore(" &d&fType: &b" + arena.getType().toString())
                    .lore(" &d&fIs Setup: &b" + (arena.isSetup() ? "&a✓" : "&c✗"))
                    .lore(" &d&fStatus: &b" + (arena.isActive() ? "&aEnabled" : "&cDisabled"))
                    .lore("")
                    .lore("&b&lLEFT-CLICK &bto teleport to arena.")
                    .lore("&b&lRIGHT-CLICK &bto see arena status.")
                    .lore("&b&lMIDDLE-CLICK &bto delete arena.")
                    .build())
                    .withListener((InventoryClickEvent click) -> {
                        switch (click.getClick()) {
                            case LEFT:
                                player.performCommand("arena teleport " + arena.getName());
                                break;
                            case RIGHT:
                                player.performCommand("arena info " + arena.getName());
                                break;
                            case MIDDLE:
                                player.performCommand("arena delete " + arena.getName());
                                break;
                        }
                    }));

            // Show the GUI
            player.openInventory(Menu.getInventory());

        }
    }
}
