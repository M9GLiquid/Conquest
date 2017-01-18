package eu.kingconquest.conquest.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.Objective;
import eu.kingconquest.conquest.core.Town;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.database.Config;
import eu.kingconquest.conquest.util.Cach;
import eu.kingconquest.conquest.util.ChatManager;
import eu.kingconquest.conquest.util.ChestGui;
import eu.kingconquest.conquest.util.Validate;

public class EditGUI extends ChestGui{
	private final Player p;
	private ChestGui previous;
	private Objective objective;
	private Town parent;
	
	public EditGUI(Player player, Object objective, Object previousGui){
		super();
		p = player;
		this.previous = (ChestGui) previousGui;
		this.objective = (Objective) objective;
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
		if (objective instanceof Kingdom){
			if (Validate.hasPerm(p, ".admin.edit.kingdom")){
				if (Validate.hasPerm(p, ".admin.edit.kingdom.name"))
					nameButton();
				if (Validate.hasPerm(p, ".admin.edit.kingdom.player"))
					playerButton();
					//childrenButton();
				if (Validate.hasPerm(p, ".admin.edit.kingdom.spawn"))
					spawnButton();
				if (Validate.hasPerm(p, ".admin.edit.kingdom.location"))
					locationButton();
				if (Validate.hasPerm(p, ".admin.remove.kingdom"))
					removeButton();
			}
		}if (objective instanceof Town) {
			if (Validate.hasPerm(p, ".admin.edit.town")){
				if (Validate.hasPerm(p, ".admin.edit.town.name"))
					nameButton();
				if (Validate.hasPerm(p, ".admin.edit.town.owner"))
					ownerButton();
				//childrenButton();
				if (Validate.hasPerm(p, ".admin.edit.town.spawn"))
					spawnButton();
				if (Validate.hasPerm(p, ".admin.edit.town.location"))
					locationButton();
				if (Validate.hasPerm(p, ".admin.remove.town"))
					removeButton();
			}
		}if (objective instanceof Village) {
			if (Validate.hasPerm(p, ".admin.edit.village")){
				if (Validate.hasPerm(p, ".admin.edit.village.name"))
					nameButton();
				if (Validate.hasPerm(p, ".admin.edit.village.owner"))
					ownerButton();
				if (Validate.hasPerm(p, ".admin.edit.village.parent"))
					parentButton();
				if (Validate.hasPerm(p, ".admin.edit.village.spawn"))
					spawnButton();
				if (Validate.hasPerm(p, ".admin.edit.village.location"))
					locationButton();
				if (Validate.hasPerm(p, ".admin.remove.village"))
					removeButton();
			}
		}
		teleportButton();
	}

	private void teleportButton(){
		setItem(slot, new ItemStack(Material.ENDER_PEARL), player -> {
			player.teleport(objective.getLocation());
		}, "&2Teleport", 
				"&6Teleport to target");
		slot++;
	}

	private void init(){
		setName();
		setOwner();
		setParent();
	}

	private void playerButton(){
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta skull = (SkullMeta) head.getItemMeta();
		skull.setOwner(p.getName());
		head.setItemMeta(skull);
        setSkullItem(slot, head, player ->{
        	new PlayerGUI(player ,this, objective);
        }, "&4Manage Members",
        		"");
		slot++;
	}
	
	private void removeButton(){
			setItem(slot, new ItemStack(Material.BARRIER), player ->{
				objective.delete(player);
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
			}, "&4Remove!",  
					"&4Click to remove!");
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
			str	+= "\n&aName: &f" + ((Kingdom)objective).getColorSymbol() + objective.getName();
			if(Validate.notNull(((Kingdom)objective).getKing()))
				str += "\n&aKing: &f" + ((Kingdom)objective).getKing().getName();
			else
				str += "\n&aKing: &fNone";
			if(Validate.notNull(((Kingdom)objective).getMembers()))
				str += "\n&aMembers: &f" + ((Kingdom)objective).getMembers().size();
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
			str += "\n&aName: &f" + objective.getName();
			str += "\n&aOwner: &f" + objective.getOwner().getName();
			if (Validate.notNull(((Town)objective).getChildren()))
				str += "\n&aChildren: &f" + ((Town)objective).getChildren().size();
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
			str += "\n&aName: &f" + objective.getOwner().getColorSymbol() + objective.getName();
			str += "\n&aOwner: &f" + objective.getOwner().getName();
			if (Validate.notNull(((Village)objective).getParent()))
				str += "\n&aParent: &f" + ((Village)objective).getParent().getName();
			else
				str += "\n&aParent: &fNone";			
			str += "\n&aPre-Owner: &f" + ((Village)objective).getPreOwner().getName()
					+ "\n&aLocation:"
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
	
	AlphabetGUI alphabetGUI;
	private void nameButton(){
		setItem(slot, new ItemStack(Material.BOOK), player -> {
			alphabetGUI = new AlphabetGUI(player, this, objective.getName());
			slot = 9;
		}, "&4Edit Name!",  
				"&cClick to edit!");
		slot++;
	}
	protected void setName(){
		if (Validate.notNull(alphabetGUI)){
			objective.setName(alphabetGUI.get());
			alphabetGUI.close(p);
		}
	}
	
	private void spawnButton(){
		setItem(slot, new ItemStack(Material.BED), player -> {
			setSpawn(player);
			display();
		}, "&4Edit Spawn Location!",
				"&cClick to edit!");
		slot++;
	}
	private void setSpawn(Player player){
		objective.setSpawn(player.getLocation());
		if (objective instanceof Kingdom) {
			Cach.StaticKingdom = (Kingdom) objective;
			ChatManager.Chat(player, Config.getStr("editKingdomSpawn"));
		}if (objective instanceof Town) {
			Cach.StaticTown = (Town) objective;
			ChatManager.Chat(player, Config.getStr("editTownSpawn"));
		}if (objective instanceof Village) {
			Cach.StaticVillage = (Village) objective;
			ChatManager.Chat(player, Config.getStr("editVillageSpawn"));
		}
	}

	private void locationButton(){
		setItem(slot, new ItemStack(Material.BANNER), player -> {
			setLocation(player);
			display();
		}, "&4Edit Dynmap Location!",
				"&cClick to edit!");
		slot++;
	}
	private void setLocation(Player player){
		objective.setSpawn(player.getLocation());
		if (objective instanceof Kingdom) {
			Cach.StaticKingdom = (Kingdom) objective;
			ChatManager.Chat(player, Config.getStr("editKingdomLocation"));
		}if (objective instanceof Town) {
			Cach.StaticTown = (Town) objective;
			ChatManager.Chat(player, Config.getStr("editTownLocation"));
		}if (objective instanceof Village) {
			Cach.StaticVillage = (Village) objective;
			ChatManager.Chat(player, Config.getStr("editVillageLocation"));
		}
	}
	
	private OwnerGUI ownerGui;
	private void ownerButton(){
		setItem(slot, new ItemStack(Material.BEACON), player -> {
			ownerGui = new OwnerGUI(p, this);
		}, "&4Edit Owner!",
				"&cClick to edit!");
		slot++;
	}
	private void setOwner(){
		if (Validate.notNull(ownerGui) 
				&& !(objective instanceof Kingdom)){
				objective.setOwner(ownerGui.get());
				objective.updateGlass();
			ownerGui.close(p);
			ownerGui = null;
		}
	}

	private ParentGUI parentGui;
	private void parentButton(){
		setItem(slot, new ItemStack(Material.BEACON), player -> {
			parentGui = new ParentGUI(p, objective, this);
		}, "&4Edit Parent!",
				"&cClick to edit!");
		slot++;
	}
	private void setParent(){
		if (Validate.notNull(parentGui)){
			if (parentGui.get() instanceof Town)
			parent = (Town) parentGui.get();
			((Village) objective).setParent(parent);
			parent.addChild((Village) objective);
			Cach.StaticVillage = (Village) objective;
			Cach.StaticTown = parent;
			ChatManager.Chat(p, Config.getStr("editVillageParent"));
			parentGui.close(p);
			parentGui = null;
		}
	}
}
