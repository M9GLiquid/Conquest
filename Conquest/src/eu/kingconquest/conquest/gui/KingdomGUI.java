package eu.kingconquest.conquest.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.util.ChestGui;
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
		createGui(p, "&6Kingdom Gui", Kingdom.getKingdoms().size() -1);
		display();
	}
	
	private int slot;
	@Override
	public void display() {
		clearSlots();
		slot = 9;
		//Slot 0
		playerInfo(p);
		//Slot 1
		homeButton();
		//Slot 3
		previous(this);
		//Slot 5
		next(this);
		//Slot 7
		if (Validate.hasPerm(p, ".admin.kingdom.create"))
			createButton();
		//Slot 8
		backButton(previous);
		
		//Slot MAIN
		for(int i = 9; i < 54; i++) {
			if (getCurrentItem() > (Kingdom.getKingdoms().size() -1) || getItems() < 1)
				break;
			Kingdom kingdom = Kingdom.getKingdoms().get(getCurrentItem());
			if (kingdom.isNeutral()){
				i--;
				setCurrentItem(getCurrentItem()+1);
				continue;
			}
			if (Validate.hasPerm(p, ".admin.kingdom.edit")) 
				kingdoms(i, kingdom);
			else{
				if (wrapper.isInKingdom()){
					if (wrapper.getKingdom().equals(kingdom))
						leave(i, kingdom);
				}else 
					join(i, kingdom);
			}
			setCurrentItem(getCurrentItem()+1);
		}
	}

	private void kingdoms(int i, Kingdom kingdom){
		setItem(slot, new ItemStack(Material.BEACON), player -> {
			setCurrentItem(0);
			clearSlots();
			new EditGUI(player, kingdom, this);
		},"&aEdit " + kingdom.getColorSymbol() + kingdom.getName()
		,displayInfo(kingdom));
		slot++;
	}

	private String displayInfo(Kingdom kingdom) {
		String str = "&1-----------------";
		
		str += "\n&aName: &r" + kingdom.getName();
		if (!Validate.isNull(kingdom.getKing()))
			str += "\n&aKing: &r" + kingdom.getKing().getName();
		else
			str += "\n&aKing: &rNot Set";
			
		if (!Validate.isNull(kingdom.getMembers()))
			str += "\n&aMembers: &r" + kingdom.getMembers().size();
		else
			str += "\n&aMembers: &r0";
		str += "\n&aLocation:"
		+ "\n&cX: &r"+ Math.floor(kingdom.getLocation().getX())
		+ "\n&cY: &r"+ Math.floor(kingdom.getLocation().getY())
		+ "\n&cZ: &r"+ Math.floor(kingdom.getLocation().getZ())
		+ "\n&aSpawn:"
		+ "\n&cX: &r"+ Math.floor(kingdom.getSpawn().getX())
		+ "\n&cY: &r"+ Math.floor(kingdom.getSpawn().getY())
		+ "\n&cZ: &r"+ Math.floor(kingdom.getSpawn().getZ());
		return str;
	}

	private void createButton(){
		setItem(7, new ItemStack(Material.DIAMOND_PICKAXE), player -> {
			new CreateGUI(player, this);
			close(player);
		}, "§4Create new Kingdom!","§1-----------------"
				+ "\n§cClick to open the edit manager!");
	}

	private void join(int i, Kingdom kingdom){
		setItem(slot, new ItemStack(Material.EMERALD_BLOCK), player -> {
			kingdom.join(player);
			setCurrentItem(0);
			clearSlots();
			display();
		},"&aJoin Kingdom!"
		, displayInfo(kingdom));
		slot++;
	}

	private void leave(int i, Kingdom kingdom) {
		setItem(slot, new ItemStack(Material.REDSTONE_BLOCK), player -> {
			kingdom.leave(player);
			setCurrentItem(0);
			clearSlots();
			display();
		},"&cLeave Kingdom!"
		, displayInfo(kingdom));
		slot++;
	}
}