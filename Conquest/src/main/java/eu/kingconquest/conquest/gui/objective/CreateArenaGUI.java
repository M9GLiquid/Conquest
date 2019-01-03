package eu.kingconquest.conquest.gui.objective;

import eu.kingconquest.conquest.chatinteract.NamePrompt;
import eu.kingconquest.conquest.core.Arena;
import eu.kingconquest.conquest.core.ChestGui;
import eu.kingconquest.conquest.util.ChatInteract;
import eu.kingconquest.conquest.util.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CreateArenaGUI extends ChestGui {
    private Player player;
    private String name = "Not Set";
    private Location perimitorA, perimitorB, spawn;

    private ChestGui previous;
    private NamePrompt namePrompt = null;

    public CreateArenaGUI(Player player, ChestGui previousGui) {
        super();
        this.player = player;
        previous = previousGui;
    }

    @Override
    public void create() {
        createGui(player, "&6Arena Create Gui", 54);
        display();
    }

    @Override
    public void display() {
        init();

        playerInfo(player);
        homeButton();
        previous(this);
        next(this);

        discardButton(7);
        saveButton(8);

        setNameButton(13);
        setPerimitorAButton(20);
        setPerimitorBButton(21);

        setMainSpawnButton(31);
    }

    private void init() {
        clearSlots();
    }

    @SuppressWarnings("all")
    private void discardButton(int slot) {
        setItem(slot, new ItemStack(Material.REDSTONE_BLOCK), player -> {
                    close(player);
                    previous.create();
                }, "&4<< Home",
                "\n"
                        + "&cGo back without Saving!\n");
    }

    @SuppressWarnings("all")
    private void saveButton(int slot) {
        setItem(slot, new ItemStack(Material.EMERALD_BLOCK), player -> {
            Arena arena = new Arena(this.name //Name
                    , perimitorA //Perimitor Location
                    , perimitorB //Perimitor Location
                    , spawn //Spawn location
            );
            arena.create(player);
        }, "&2Create new!", "");
        close(player);
        previous.create();
    }

    @SuppressWarnings("all")
    private void setNameButton(int slot) {
        if (Validate.notNull(namePrompt)) {
            name = namePrompt.get();
            namePrompt = null;
            display();
        }

        setItem(slot, new ItemStack(Material.BOOK), player -> {
                    namePrompt = new NamePrompt(this);
                    new ChatInteract(player, namePrompt, "Cancel");
                    player.closeInventory();
                }, "&4Set Name!",
                "\n"
                        + "\n&bClick to set name");
    }

    @SuppressWarnings("all")
    private void setMainSpawnButton(int slot) {
        setItem(slot, new ItemStack(Material.RED_BED), player -> {
                    spawn = player.getLocation().clone();
                    clearSlots();
                    display();
                }, "&4Set Spawn",
                "\n"
                        + "\n&bClick to set spawn");
    }

    @SuppressWarnings("all")
    private void setPerimitorAButton(int slot) {
        setItem(slot, new ItemStack(Material.BLACK_BANNER), player -> {
                    perimitorA = player.getLocation().clone();
                    display();
                }, "&4Set Location A",
                "\n"
                        + "\n&bClick to set location A");
    }

    @SuppressWarnings("all")
    private void setPerimitorBButton(int slot) {
        setItem(slot, new ItemStack(Material.BLACK_BANNER), player -> {
                    perimitorB = player.getLocation().clone();
                    display();
                }, "&4Set Location B",
                "\n"
                        + "\n&bClick to set location B");
    }
}
