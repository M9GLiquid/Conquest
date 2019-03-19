package eu.kingconquest.conquest.gui.objective;

import eu.kingconquest.conquest.core.ActiveWorld;
import eu.kingconquest.conquest.core.ChestGui;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.util.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class VillageGUI extends ChestGui{
    private Player player;
	private ChestGui previous;

    public VillageGUI(Player player, Object previousGui) {
		super();
        this.player = player;
		this.previous = (ChestGui) previousGui;
		create();
	}

	@Override
	public void create(){
        createGui(player, "&6Village Gui", Village.getVillages(ActiveWorld.getActiveWorld(player.getWorld())).size());
		setCurrentItem(0);
		display();
	}

	@Override
	public void display() {
		clearSlots();
		//Slot 0
        playerInfo(player);
		//SLot 1
		homeButton();
		//Slot 3
		previous(this);
		//Slot 5
		next(this);
		//Slot 7
        if (Validate.hasPerm(player, ".admin.create.village"))
			createButton();
		//Slot 8
		backButton(previous);
		
		//Slot MAIN
		for(int i = 9; i < 54; i++) {
			if (getCurrentItem() > getItems() -1 || getItems() < 1)
				break;

            if (Validate.hasPerm(player, "admin.edit.village"))
                editButton(i, Village.getVillages(ActiveWorld.getActiveWorld(player.getWorld())).get(getCurrentItem()));
			
			setCurrentItem(getCurrentItem()+1);
		}
	}
	
	private void editButton(int i, Village village){
        setItem(i, new ItemStack(Material.BEACON), player ->
                        new EditGUI(player, village, this), "&aEdit " + village.getOwner().getColor() + village.getName()
		, displayInfo(village));
	}

	private String displayInfo(Village village) {
		String str = "\n&aName: &f" + village.getOwner().getColor() + village.getName();
		str += "\n&aOwner: &f" + village.getOwner().getColor() + village.getOwner().getName()
				+ "\n&aPre-Owner: &f" + village.getPreOwner().getColor() + village.getPreOwner().getName();
		if (Validate.notNull(village.getParent()))
			str += "\n&aParent: &f" + village.getParent().getName();
		else
			str += "\n&aParent: &fNone";			
		str +=  "\n&aLocation:"
				+ "\n- &cX: &f"+ Math.floor(village.getLocation().getX())
				+ "\n- &cY: &f"+ Math.floor(village.getLocation().getY())
				+ "\n- &cZ: &f"+ Math.floor(village.getLocation().getZ())
				+ "\n&aSpawn:"
				+ "\n- &cX: &f"+ Math.floor(village.getSpawn().getX())
				+ "\n- &cY: &f"+ Math.floor(village.getSpawn().getY())
				+ "\n- &cZ: &f"+ Math.floor(village.getSpawn().getZ());
		return str;
	}
	
	private void createButton(){
		setItem(7, new ItemStack(Material.DIAMOND_PICKAXE), player -> {
			setCurrentItem(0);
			new CreateGUI(player, this);
		}, "&3Create new Village!",  ""
				);
	}
}
