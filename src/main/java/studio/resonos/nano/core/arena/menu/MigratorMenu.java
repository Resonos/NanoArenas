package studio.resonos.nano.core.arena.menu;


import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import studio.resonos.nano.NanoArenas;
import studio.resonos.nano.api.gui.buttons.SGButton;
import studio.resonos.nano.api.gui.menu.SGMenu;
import studio.resonos.nano.core.migrator.PlatinumArenasMigration;
import studio.resonos.nano.core.util.CC;
import studio.resonos.nano.core.util.ItemBuilder;

/**
 * @Author: Athishh
 * Package: me.athishh.lotus.core.menus.migrator
 * Created on: 2/6/2024
 */
public class MigratorMenu {

    public static void OpenMenu(Player player) {
        // Open the menu
            SGMenu menu = NanoArenas.spiGUI.create("&b&lNano Migration &c&l&o(BETA)", 3, "");


        menu.setButton(13, new SGButton(new ItemBuilder(Material.FIRE_CORAL_BLOCK)
                .name("&c[&7PlatinumArenas&c]")
                //.lore(CC.CHAT_BAR)
                .lore(" ")
                .lore("&fHave you recently switched from &ePlatinumArenas?")
                .lore("&fSave your time & let us import all your old arenas instantly!")
                .lore("&fWe will automatically detect your old arenas, import and")
                .lore("&fset them up for you. Our algorithm will handle the full setup.")
                .lore("&b&oZero extra work from your side ;).")
                .lore(" ")
                .lore("&bThis will migrate the following data:")
                .lore("&a•&f Arenas")
                .lore(" ")
                .lore("&a&oClick to migrate from &c&o[&7&oPlatinumArenas&c&o]")
                //.lore(CC.CHAT_BAR)
                .build()).withListener((InventoryClickEvent event) -> {

            player.sendMessage(CC.translate("&8(&b❀&8) &aMigrating from &ePlatinumArenas..."));
            player.sendMessage(CC.translate("&8(&b❀&8) &eThis may take a while depending on size and number of arenas you have."));
            PlatinumArenasMigration.migrateFromPlatinumArenas();
        }));

        player.openInventory(menu.getInventory());

    }
}