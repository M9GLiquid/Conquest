package eu.kingconquest.conquest.gui.objective;

import eu.kingconquest.conquest.core.ActiveWorld;
import eu.kingconquest.conquest.core.ChestGui;
import eu.kingconquest.conquest.core.Town;
import eu.kingconquest.conquest.util.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TownGUI extends ChestGui{
    private Player player;
	private ChestGui previous;

    public TownGUI(Player player, Object previousGui) {
		super();
        this.player = player;
		this.previous = (ChestGui) previousGui;
		create();
	}

	@Override
	public void create(){
        createGui(player, "&6Town Gui", Town.getTowns().size());
		setCurrentItem(0);
		display();
	}
	
	@Override
	public void display() {
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
        if (Validate.hasPerm(player, ".admin.create.town"))
			createButton();
		//Slot 8
		backButton(previous);

		//Slot MAIN
		for(int i = 9; i < 54; i++) {
            if (getCurrentItem() > (Town.getTowns(ActiveWorld.getActiveWorld(player.getWorld())).size() - 1) || getItems() < 1)
				break;

            if (Validate.hasPerm(player, "admin.village.edit"))
                editButton(i, Town.getTowns(ActiveWorld.getActiveWorld(player.getWorld())).get(getCurrentItem()));
			
			setCurrentItem(getCurrentItem()+1);
		}
	}
	
	private void editButton(int i, Town town){
        setItem(i, new ItemStack(Material.BEACON), player ->
                        new EditGUI(player, town, this), "&aEdit " + town.getOwner().getColor() + town.getName()
		, displayInfo(town));
	}

	private String displayInfo(Town town) {
		String str = "\n&aName: &f" + town.getOwner().getColor() + town.getName();
		str += "\n&aOwner: &f" + town.getOwner().getColor() + town.getOwner().getName();
		if (Validate.notNull(town.getChildren()))
			str += "\n&aChildren: &f" + town.getChildren().size();
		else
			str += "\n&aChildren: &fNone";			
		str += "\n&aLocation:"
				+ "\n- &cX: &f"+ Math.floor(town.getLocation().getX())
				+ "\n- &cY: &f"+ Math.floor(town.getLocation().getY())
				+ "\n- &cZ: &f"+ Math.floor(town.getLocation().getZ())
				+ "\n&aSpawn:"
				+ "\n- &cX: &f"+ Math.floor(town.getSpawn().getX())
				+ "\n- &cY: &f"+ Math.floor(town.getSpawn().getY())
				+ "\n- &cZ: &f"+ Math.floor(town.getSpawn().getZ());
		return str;
	}
	
	private void createButton(){
		setItem(7, new ItemStack(Material.DIAMOND_PICKAXE), player -> {
			setCurrentItem(0);
			new CreateGUI(player, this);
			close(player);
		}, "&3Create new Town!", ""
				);
	}
}
