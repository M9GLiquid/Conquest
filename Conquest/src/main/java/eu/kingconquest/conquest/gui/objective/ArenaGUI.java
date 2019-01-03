package eu.kingconquest.conquest.gui.objective;

import eu.kingconquest.conquest.core.Arena;
import eu.kingconquest.conquest.core.ChestGui;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.util.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class ArenaGUI extends ChestGui {
    private PlayerWrapper wrapper;
    private Player player;
    private ChestGui previous;

    public ArenaGUI(Player player, Object previousGui) {
        super();
        this.player = player;
        this.previous = (ChestGui) previousGui;
        create();
    }

    @Override
    public void create() {
        wrapper = PlayerWrapper.getWrapper(player);
        if (wrapper.isInArena(player.getWorld()))
            createGui(player, "&6Arena Gui", 18);
        else
            createGui(player, "&6Arena Gui", Arena.getArenas().size() - 1);
        display();
    }

    @Override
    public void display() {
        setCurrentItem(0);
        clearSlots();
        //Slot 0
        playerInfo(player);
        //Slot 1
        homeButton();
        //Slot 3
        previous(this);
        //Slot 5
        next(this);
        //Slot 7
        if (Validate.hasPerm(player, ".admin.create.arena"))
            createButton();
        //Slot 8
        backButton(previous);

        //Slot MAIN
        for (int i = 9; i < 54; i++) {
            if (getCurrentItem() > (Arena.getArenas(player.getWorld()).size() - 1) || getItems() < 1)
                break;

            Arena arena = Arena.getArenas(player.getWorld()).get(getCurrentItem());
            if (Validate.hasPerm(player, ".admin.edit.arena"))
                editButton(i, arena);
            else if (Validate.hasPerm(player, ".basic")) {
                if (wrapper.isInArena(player.getWorld())) {
                    if (wrapper.getArena(player.getWorld()).equals(arena))
                        if (Validate.hasPerm(player, ".basic.leave"))
                            leave(13, arena);
                } else if (Validate.hasPerm(player, ".basic.join"))
                    join(i, arena);
            }
            setCurrentItem(getCurrentItem() + 1);
        }
    }

    private void editButton(int i, Arena arena) {
        setItem(i, new ItemStack(Material.BEACON), player ->
                        new EditGUI(player, arena, this), "&3Edit " + arena.getName()
                , displayInfo(arena));
    }

    private String displayInfo(Arena arena) {

        String str = "\n&aName: &6" + arena.getName();
        if (Validate.notNull(arena.getAllPlayers()))
            str += "\n&aMembers: &f" + arena.getAllPlayers().size();
        else
            str += "\n&aMembers: &fNone";
        str += "\n&aLocation:"
                + "\n- &cX: &f" + Math.floor(arena.getLocation().getX())
                + "\n- &cY: &f" + Math.floor(arena.getLocation().getY())
                + "\n- &cZ: &f" + Math.floor(arena.getLocation().getZ())
                + "\n&aSpawn:"
                + "\n- &cX: &f" + Math.floor(arena.getSpawn().getX())
                + "\n- &cY: &f" + Math.floor(arena.getSpawn().getY())
                + "\n- &cZ: &f" + Math.floor(arena.getSpawn().getZ());
        return str;
    }

    private void createButton() {
        setItem(7, new ItemStack(Material.DIAMOND_PICKAXE), player -> {
                    new CreateGUI(player, this);
                    setCurrentItem(0);
                }, "&3Create new Arena!", ""
        );
    }

    private void join(int slot, Arena arena) {
        setItem(slot, new ItemStack(Material.EMERALD_BLOCK), player -> {
                    setCurrentItem(0);
                    arena.join(player);
                    create();
                }, "&aJoin Arena!"
                , displayInfo(arena));
    }

    @SuppressWarnings("all")
    private void leave(int slot, Arena arena) {
        setItem(9, new ItemStack(Material.REDSTONE_BLOCK), player -> {
                    setCurrentItem(0);
                    arena.leave(player);
                    display();
                }, "&cLeave Arena!"
                , displayInfo(arena));
    }
}