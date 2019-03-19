package eu.kingconquest.conquest.gui.objective;

import eu.kingconquest.conquest.chatinteract.NamePrompt;
import eu.kingconquest.conquest.core.*;
import eu.kingconquest.conquest.util.ChatInteract;
import eu.kingconquest.conquest.util.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CreateGUI extends ChestGui{
	private Player p;
    private ActiveWorld activeWorld;
	private String name = "Not Set";
	private Location loc;
	private Location spawn;
	private Kingdom owner;

	private ChestGui previous;
	
	public CreateGUI(Player player, ChestGui previousGui){
		super();
		p = player;
		previous = previousGui;
        activeWorld = ActiveWorld.getActiveWorld(p.getWorld());
        owner = Kingdom.getKingdom("Neutral", activeWorld);
        System.out.println(Kingdom.getKingdom("Neutral", activeWorld));
		create();
	}

	@Override
	public void create(){
        owner = Kingdom.getNeutral(activeWorld);
		createGui(p, "&6Create Gui", 27);
		display();
	}

	@Override
	public void display(){
		init();
		
		playerInfo(p);
		homeButton();
		previous(this);
		infoIcon(4);
		next(this);
		
		discardButton(7);
		saveButton(8);
		
		nameButton(13);
		locationButton(22);
		spawnButton(31);
		//Slot 12
		/*if (previous instanceof TownGUI) {
			//ownerButton();
			//childrenButton();
		}
		if (previous instanceof VillageGUI) {
			//ownerButton();
		 	//parentButton();
		 }*/
	}

	private void init(){
		clearSlots();
	}

    @SuppressWarnings("all")
	private void infoIcon(int slot){
		setItem(slot, new ItemStack(Material.PAPER), player -> {
		},"&aInformation"
		, displayInfo());		
	}

    @SuppressWarnings("all")
	private void discardButton(int slot){
		setItem(slot, new ItemStack(Material.REDSTONE_BLOCK), player -> {
			close(player);
			previous.create();
		}, "&4<< Home", 
				"\n"
				+ "&cGo back without Saving!\n");
	}

    @SuppressWarnings("all")
	private void saveButton(int slot){
			setItem(slot, new ItemStack(Material.EMERALD_BLOCK), player -> {
				if (previous instanceof KingdomGUI) {
					Kingdom kingdom = new Kingdom(this.name //Name
							, null //UUID
							, loc //Fixed location
							, spawn //Spawn location
							);
					kingdom.create(player);
					
				} else if (previous instanceof TownGUI) {
					Town town = new Town(
							this.name //Name
							, loc //Fixed location
							, spawn //Spawn location
							, owner //Owner
							); 
					town.create(player);
				}else if (previous instanceof VillageGUI) {
					Village village = new Village(
							this.name //Name
							, loc //Fixed location
							, spawn //Spawn location
							, owner //Owner
                            , Kingdom.getKingdom("Neutral", ActiveWorld.getActiveWorld(loc.getWorld())) //Pre Owner
							, null //Parent
							);
					village.create(player);
				}
				close(player);
				previous.create();
			}, "&2Create new!", displayInfo());
	}

	private String displayInfo(){

		String str = "&aName: &r" + name;
		if (previous instanceof KingdomGUI)
		str += "\n&aKing: &r" + "None"
			+ "\n&aMembers: &r0";
		
		if (previous instanceof TownGUI)
			str += "\n&aOwner: &r" + owner.getName();
		
		if (previous instanceof VillageGUI)
			str += "\n&aOwner: &r" + owner.getName()
				+ "\n&aParent: &rNot Set";
			
		str += "\n&aLocation:";
		if (Validate.notNull(loc)) {
			str += "\n- &cX: &r"+ Math.floor(loc.getX())
					+"\n- &cY: &r"+ Math.floor(loc.getY())
					+"\n- &cZ: &r"+ Math.floor(loc.getZ());
		}else
			str += "\n- Not Set";
		str += "\n&aSpawn:";
		if (Validate.notNull(spawn)){
			str += "\n- &cX: &r"+ Math.floor(spawn.getX())
					+"\n- &cY: &r"+ Math.floor(spawn.getY())
					+"\n- &cZ: &r"+ Math.floor(spawn.getZ());
			
		}else
			str += "\n- Not Set";
		return str;
	}

	private NamePrompt namePrompt = null;

    @SuppressWarnings("all")
    private void nameButton(int slot){
        if (Validate.notNull(namePrompt)){
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
	private void spawnButton(int slot){
        setItem(slot, new ItemStack(Material.RED_BED), player -> {
			spawn = player.getLocation().clone();
			clearSlots();
			display();
		}, "&4Set Spawn",
				"\n"
				+ "\n&bClick to set spawn");
	}

    @SuppressWarnings("all")
	private void locationButton(int slot){
        setItem(slot, new ItemStack(Material.BLACK_BANNER), player -> {
			loc = player.getLocation().clone();
			display();
		}, "&4Set Location",
				"\n"
				+ "\n&bClick to set location");
	}

}
