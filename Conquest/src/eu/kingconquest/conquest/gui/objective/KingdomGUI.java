package eu.kingconquest.conquest.gui.objective;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.core.ChestGui;
import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.util.Validate;


public class KingdomGUI extends ChestGui{
	private PlayerWrapper wrapper ;
	private Player p;
	private ChestGui previous;

	public KingdomGUI(Player p, Object previousGui) {
		super();
		this.p = p;
		this.previous = (ChestGui) previousGui;
		create();
	}

	@Override
	public void create(){
		wrapper = PlayerWrapper.getWrapper(p);
		if (wrapper.isInKingdom(p.getWorld()))
			createGui(p, "&6Kingdom Gui", 18);
		else
			createGui(p, "&6Kingdom Gui", Kingdom.getKingdoms().size() -1);
		display();
	}
	
	@Override
	public void display() {
		setCurrentItem(0);
		clearSlots();
		//Slot 0
		playerInfo(p);
		//Slot 1
		homeButton();
		//Slot 3
		previous(this);
		//Slot 5
		next(this);
		//Slot 7
		if (Validate.hasPerm(p, ".admin.create.kingdom"))
			createButton();
		//Slot 8
		backButton(previous);
		
		//Slot MAIN
		for(int i = 9; i < 54; i++) {
			if (getCurrentItem() > (Kingdom.getKingdoms(p.getWorld()).size() -1) || getItems() < 1)
				break;
			
			Kingdom kingdom = Kingdom.getKingdoms(p.getWorld()).get(getCurrentItem());
			if (kingdom.isNeutral()){
				setCurrentItem(getCurrentItem()+1);
				i--;
				continue;
			}
			if (Validate.hasPerm(p, ".admin.edit.kingdom")) 
				editButton(i, kingdom);
			else if  (Validate.hasPerm(p, ".basic")){
				if (wrapper.isInKingdom(p.getWorld())){
					if (wrapper.getKingdom(p.getWorld()).equals(kingdom))
						if (Validate.hasPerm(p, ".basic.leave"))
						leave(13, kingdom);
				}else 
					if (Validate.hasPerm(p, ".basic.join"))
					join(i, kingdom);
			}
			setCurrentItem(getCurrentItem()+1);
		}
	}

	private void editButton(int i, Kingdom kingdom){
		setItem(i, new ItemStack(Material.BEACON), player -> {
			new EditGUI(player, kingdom, this);
		},"&3Edit " + kingdom.getColor() + kingdom.getName()
		,displayInfo(kingdom));
	}

	private String displayInfo(Kingdom kingdom) {
		
		String str	= "\n&aName: &f" +kingdom.getColor() + kingdom.getName();
		if(Validate.notNull(kingdom.getKing()))
			str += "\n&aKing: &f" + kingdom.getKing().getName();
		else
			str += "\n&aKing: &fNone";
		if(Validate.notNull(kingdom.getMembers()))
			str += "\n&aMembers: &f" + kingdom.getMembers().size();
		else
			str += "\n&aMembers: &fNone";
		str	+= "\n&aLocation:"
				+ "\n- &cX: &f"+ Math.floor(kingdom.getLocation().getX())
				+ "\n- &cY: &f"+ Math.floor(kingdom.getLocation().getY())
				+ "\n- &cZ: &f"+ Math.floor(kingdom.getLocation().getZ())
				+ "\n&aSpawn:"
				+ "\n- &cX: &f"+ Math.floor(kingdom.getSpawn().getX())
				+ "\n- &cY: &f"+ Math.floor(kingdom.getSpawn().getY())
				+ "\n- &cZ: &f"+ Math.floor(kingdom.getSpawn().getZ());
		return str;
	}

	private void createButton(){
		setItem(7, new ItemStack(Material.DIAMOND_PICKAXE), player -> {
			new CreateGUI(player, this);
			setCurrentItem(0);
		}, "&3Create new Kingdom!", ""
				);
	}

	private void join(int slot, Kingdom kingdom){
		setItem(slot, new ItemStack(Material.EMERALD_BLOCK), player -> {
			setCurrentItem(0);
			kingdom.join(player);
			create();
		},"&aJoin Kingdom!"
		, displayInfo(kingdom));
	}

	private void leave(int slot, Kingdom kingdom) {
		setItem(9, new ItemStack(Material.REDSTONE_BLOCK), player -> {
			setCurrentItem(0);
			kingdom.leave(player);
			display();
		},"&cLeave Kingdom!"
		, displayInfo(kingdom));
	}
}