package eu.kingconquest.conquest.core.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.Objective;
import eu.kingconquest.conquest.core.Town;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.core.util.Cach;
import eu.kingconquest.conquest.core.util.ChatManager;
import eu.kingconquest.conquest.core.util.ChestGui;
import eu.kingconquest.conquest.core.util.Config;
import eu.kingconquest.conquest.core.util.Validate;

public class EditGUI extends ChestGui{
	private final Player p;
	private ChestGui previous;
	private Objective objective;
	
	public EditGUI(Player player, Object object, Object previousGui){
		super();
		p = player;
		this.previous = (ChestGui) previousGui;
		this.objective = (Objective) object;
		create();
	}

	@Override
	public void create(){
		createGui(p, "&6Edit Gui", 9);
		display();
	}
	
	private int slot = 9;
	@Override
	public void display(){
		clearSlots();
		init();
		slot = 9;
		
		//Slot 0
		playerInfo(p);
		//Slot 1
		homeButton();
		//Slot 3
		previous(this);
		//Slot 4
		infoIcon();
		//Slot 5
		next(this);
		//Slot 8
		backButton(previous);
		
		//Slot MAIN
		nameButton();
		if (objective instanceof Kingdom){
			playerButton();
			//childrenButton();
		}if (objective instanceof Town) {
			ownerButton();
			//childrenButton();
		}if (objective instanceof Village) {
			ownerButton();
			parentButton();
		}
		spawnButton();
		locationButton();
		teleportButton();
		removeButton();
	}

	private void teleportButton(){
		setItem(slot, new ItemStack(Material.ENDER_PEARL), player -> {
			player.teleport(objective.getLocation());
		}, "&2Teleport", "&1-----------------"
				+ "\n&6Teleport to target"
				+ "\n");
		slot++;
	}

	private void init(){
		setName();
		setOwner();
	}

	private void playerButton(){
		
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta skull = (SkullMeta) head.getItemMeta();
		skull.setOwner(p.getName());
		head.setItemMeta(skull);
        setSkullItem(slot, head, player ->{
        	new PlayerGUI(player ,this, objective);
        }, "§4Manage Members","§1-----------------"
        		+ "\n"
        		);
		slot++;
	}
	
	private void removeButton(){
			setItem(slot, new ItemStack(Material.BARRIER), player ->{
				objective.delete();
				if (objective instanceof Kingdom){
					KingdomGUI kingdomGUI = new KingdomGUI(player, this);
					kingdomGUI.create();
				}else if  (objective instanceof Town){
					TownGUI townGUI = new TownGUI(player, this);
					townGUI.create();
				}else if (objective instanceof Village){
					VillageGUI villageGUI = new VillageGUI(player, this);
					villageGUI.create();
				}
				close(player);
			}, "§4Remove!",  "&1-----------------"
					+ "\n§4Click to remove!");
			slot++;
	}

	public void infoIcon(){
		setItem(4, new ItemStack(Material.PAPER), player -> {
			},"&3Information"
			,  displayInfo());
	}
	private String displayInfo() {
		String str = "&1-----------------\n ";
		
		if (objective instanceof Kingdom) {
			Kingdom kingdom = (Kingdom) objective;
			str	+= "\n&aName: &f" + kingdom.getColorSymbol() + objective.getName();
			if(!Validate.isNull(kingdom.getKing()))
				str += "\n&aKing: &f" + kingdom.getKing().getName();
			else
				str += "\n&aKing: &fNone";
			if(!Validate.isNull(kingdom.getMembers()))
				str += "\n&aMembers: &f" + kingdom.getMembers().size();
			else
				str += "\n&aMembers: &fNone";
			str	+= "\n&aLocation:"
					+ "\n- &cX: &f"+ Math.floor(objective.getLocation().getX())
					+ "\n- &cY: &f"+ Math.floor(objective.getLocation().getY())
					+ "\n- &cZ: &f"+ Math.floor(objective.getLocation().getZ())
					+ "\n&aSpawn:"
					+ "\n- &cX: &f"+ Math.floor(objective.getSpawn().getX())
					+ "\n- &cY: &f"+ Math.floor(objective.getSpawn().getY())
					+ "\n- &cZ: &f"+ Math.floor(objective.getSpawn().getZ());
		}else if(objective instanceof Town) {
			Town town = (Town) objective;
			str += "\n&aName: &f" + objective.getName();
			str += "\n&aOwner: &f" + objective.getOwner().getName();
			if (!Validate.isNull(town.getChildren()))
				str += "\n&aChildren: &f" + town.getChildren().size();
			else
				str += "\n&aChildren: &fNone";			
			str += "\n&aLocation:"
					+ "\n- &cX: &f"+ Math.floor(objective.getLocation().getX())
					+ "\n- &cY: &f"+ Math.floor(objective.getLocation().getY())
					+ "\n- &cZ: &f"+ Math.floor(objective.getLocation().getZ())
					+ "\n&aSpawn:"
					+ "\n- &cX: &f"+ Math.floor(objective.getSpawn().getX())
					+ "\n- &cY: &f"+ Math.floor(objective.getSpawn().getY())
					+ "\n- &cZ: &f"+ Math.floor(objective.getSpawn().getZ());
		}else if(objective instanceof Village) {
			Village village = (Village) objective;
			str += "\n&aName: &f" + objective.getOwner().getColorSymbol() + objective.getName();
			str += "\n&aOwner: &f" + objective.getOwner().getName();
			if (!Validate.isNull(village.getParent()))
				str += "\n&aParent: &f" + village.getParent().getName();
			else
				str += "\n&aParent: &fNone";			
			str += "\n&aLocation:"
					+ "\n- &cX: &f"+ Math.floor(objective.getLocation().getX())
					+ "\n- &cY: &f"+ Math.floor(objective.getLocation().getY())
					+ "\n- &cZ: &f"+ Math.floor(objective.getLocation().getZ())
					+ "\n&aSpawn:"
					+ "\n- &cX: &f"+ Math.floor(objective.getSpawn().getX())
					+ "\n- &cY: &f"+ Math.floor(objective.getSpawn().getY())
					+ "\n- &cZ: &f"+ Math.floor(objective.getSpawn().getZ());
		}
		return str;
	}
	
	AlphabetGUI alphaGUI;
	private void nameButton(){
		setItem(slot, new ItemStack(Material.BOOK), player -> {
				alphaGUI = new AlphabetGUI(player, this, objective.getName());
			alphaGUI.create();
			slot = 9;
		}, "§4Edit Name!",  "&1-----------------"
				+ "\n§cClick to edit!");
		slot++;
	}
	protected void setName(){
		if (!Validate.isNull(alphaGUI)){
			objective.setName(alphaGUI.getWord());
			alphaGUI.close(p);
		}
	}
	
	private void spawnButton(){
		setItem(slot, new ItemStack(Material.BED), player -> {
			setSpawn(player);
			display();
		}, "§4Edit Spawn Location!","§1-----------------"
				+ "\n§cClick to edit!");
		slot++;
	}
	private void setSpawn(Player player){
		objective.setSpawn(player.getLocation());
		if (objective instanceof Kingdom) {
			Cach.StaticKingdom = (Kingdom) objective;
			ChatManager.Chat(player, Config.getChat("editKingdomSpawn"));
		}if (objective instanceof Town) {
			Cach.StaticTown = (Town) objective;
			ChatManager.Chat(player, Config.getChat("editTownSpawn"));
		}if (objective instanceof Village) {
			Cach.StaticVillage = (Village) objective;
			ChatManager.Chat(player, Config.getChat("editVillageSpawn"));
		}
	}

	private void locationButton(){
		setItem(slot, new ItemStack(Material.BANNER), player -> {
			setLocation(player);
			display();
		}, "§4Edit Dynmap Location!","§1-----------------"
				+ "\n§cClick to edit!");
		slot++;
	}
	private void setLocation(Player player){
		objective.setSpawn(player.getLocation());
		if (objective instanceof Kingdom) {
			Cach.StaticKingdom = (Kingdom) objective;
			ChatManager.Chat(player, Config.getChat("editKingdomLocation"));
		}if (objective instanceof Town) {
			Cach.StaticTown = (Town) objective;
			ChatManager.Chat(player, Config.getChat("editTownLocation"));
		}if (objective instanceof Village) {
			Cach.StaticVillage = (Village) objective;
			ChatManager.Chat(player, Config.getChat("editVillageLocation"));
		}
	}
	
	private OwnerGUI ownershipGui;
	private void ownerButton(){
		setItem(slot, new ItemStack(Material.BEACON), player -> {
			ownershipGui = new OwnerGUI(p, this);
		}, "§4Edit Owner!","§1-----------------"
				+ "\n§cClick to edit!");
		slot++;
	}
	private void setOwner(){
		if (!Validate.isNull(ownershipGui) 
				&& !(objective instanceof Kingdom)){
				objective.setOwner(ownershipGui.get());
			ownershipGui.close(p);
		}
	}

	private void parentButton(){
		setItem(slot, new ItemStack(Material.BEACON), player -> {
			new ParentGUI(p, objective, this);
		}, "§4Edit Parent!","§1-----------------"
				+ "\n§cClick to edit!");
		slot++;
	}
}
