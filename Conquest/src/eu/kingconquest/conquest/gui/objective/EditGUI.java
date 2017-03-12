package eu.kingconquest.conquest.gui.objective;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import eu.kingconquest.conquest.chatinteract.NamePrompt;
import eu.kingconquest.conquest.core.ChestGui;
import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.Objective;
import eu.kingconquest.conquest.core.Town;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.gui.PlayerGUI;
import eu.kingconquest.conquest.util.Cach;
import eu.kingconquest.conquest.util.ChatInteract;
import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.MessageType;
import eu.kingconquest.conquest.util.Validate;

public class EditGUI extends ChestGui{
	private final Player player;
	private ChestGui previous;
	private Objective objective;
	private Town parent;
	
	public EditGUI(Player player, Objective objective, Object previousGui){
		super();
		this.player = player;
		this.previous = (ChestGui) previousGui;
		this.objective = objective;
		create();
	}
	
	@Override
	public void create(){
		createGui(player, "&6Edit Gui", 54);
		display();
	}
	
	@Override
	public void display(){
		clearSlots();
		
		playerInfo(player);
		homeButton();
		previous(this);
		infoIcon(4);
		next(this);
		backButton(previous);
		
		//Slot MAIN
		if (objective instanceof Kingdom){
			if (Validate.hasPerm(player, ".admin.edit.kingdom")){
				if (Validate.hasPerm(player, ".admin.edit.kingdom.name"))
					nameButton(13);
				if (Validate.hasPerm(player, ".admin.edit.kingdom.player"))
					playerButton(22);
				//childrenButton();
				if (Validate.hasPerm(player, ".admin.edit.kingdom.spawn")){
					spawnButton(30);
					if (Validate.hasPerm(player, ".admin.edit.kingdom.location"))
						locationIcon(31);
				}
				if (Validate.hasPerm(player, ".admin.edit.kingdom.location"))
					locationButton(32);
				if (Validate.hasPerm(player, ".admin.remove.kingdom"))
					removeButton(40);
			}
		}else if (objective instanceof Town) {
			if (Validate.hasPerm(player, ".admin.edit.town")){
				if (Validate.hasPerm(player, ".admin.edit.town.name"))
					nameButton(13);
				if (Validate.hasPerm(player, ".admin.edit.town.owner"))
					ownerButton(22);
				//childrenButton();
				if (Validate.hasPerm(player, ".admin.edit.town.spawn")){
					spawnButton(30);
					if (Validate.hasPerm(player, ".admin.edit.town.location"))
						locationIcon(31);
				}
				if (Validate.hasPerm(player, ".admin.edit.town.location"))
					locationButton(32);
				if (Validate.hasPerm(player, ".admin.remove.town"))
					removeButton(40);
			}
		}else if (objective instanceof Village) {
			if (Validate.hasPerm(player, ".admin.edit.village")){
				if (Validate.hasPerm(player, ".admin.edit.village.name"))
					nameButton(13);
				if (Validate.hasPerm(player, ".admin.edit.village.parent"))
					parentButton(22);
				if (Validate.hasPerm(player, ".admin.edit.village.owner")){
					ownerButton(30);
					ownerIcon(31);
					preOwnerButton(32);
				}
				if (Validate.hasPerm(player, ".admin.edit.village.spawn")){
					spawnButton(39);
					if (Validate.hasPerm(player, ".admin.edit.village.location"))
						locationIcon(40);
				}
				if (Validate.hasPerm(player, ".admin.edit.village.location"))
					locationButton(41);
				if (Validate.hasPerm(player, ".admin.remove.village"))
					removeButton(49);
			}
		}
		teleportButton(2);
	}
	
	private void ownerIcon(int slot){
		setItem(slot, new ItemStack(Material.PAPER), player -> {
		}, "&r&7<< &5&lEdit Owners &7>>", "");
	}
	private void locationIcon(int slot){
		setItem(slot, new ItemStack(Material.PAPER), player -> {
		}, "&r&7<< &5&lEdit Locations &7>>", "");
	}
	
	private void teleportButton(int slot){
		setItem(slot, new ItemStack(Material.ENDER_PEARL), player -> {
			player.teleport(objective.getLocation());
		}, "&2Teleport", 
				"&6Teleport to target");
	}
	
	private void playerButton(int slot){
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta skull = (SkullMeta) head.getItemMeta();
		skull.setOwner(player.getName());
		head.setItemMeta(skull);
		setSkullItem(slot, head, player ->{
			new PlayerGUI(player ,this, objective);
		}, "&4Manage Members",
				"");
	}
	
	private void removeButton(int slot){
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
	}
	
	public void infoIcon(int slot){
		setItem(slot, new ItemStack(Material.PAPER), player -> {
			display();
		},"&3Information"
				,  displayInfo());
	}
	private String displayInfo() {
		String str = "";
		
		if (objective instanceof Kingdom) {
			str	= "\n&aName: &f" + ((Kingdom)objective).getColorSymbol() + objective.getName();
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
			str = "\n&aName: &f" + objective.getName();
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
			str = "\n&aName: &f" + objective.getOwner().getColorSymbol() + objective.getName();
			str += "\n&aOwner: &f" + objective.getOwner().getName()
					+ "\n&aPre-Owner: &f" + ((Village)objective).getPreOwner().getName();
			if (Validate.notNull(((Village)objective).getParent()))
				str += "\n&aParent: &f" + ((Village)objective).getParent().getName();
			else
				str += "\n&aParent: &fNone";			
			str +=  "\n&aLocation:"
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
	
	private NamePrompt namePrompt;
	private void nameButton(int slot){
		if (Validate.notNull(namePrompt)){
			objective.setName(namePrompt.get());
			namePrompt = null;
			display();
		}
		
		setItem(slot, new ItemStack(Material.BOOK), player -> {
			namePrompt = new NamePrompt(this);
			new ChatInteract(player, namePrompt, "Cancel");
			player.closeInventory();
		}, "&5Edit Name!",  
				"&bClick to edit!");
	}
	
	private void spawnButton(int slot){
		setItem(slot, new ItemStack(Material.BED), player -> {
			objective.setSpawn(player.getLocation());
			if (objective instanceof Kingdom) {
				Cach.StaticKingdom = (Kingdom) objective;
				new Message(player, MessageType.CHAT, "{AdminEditKingdomSpawn}");
			}if (objective instanceof Town) {
				Cach.StaticTown = (Town) objective;
				new Message(player, MessageType.CHAT, "{AdminEditTownSpawn}");
			}if (objective instanceof Village) {
				Cach.StaticVillage = (Village) objective;
				new Message(player, MessageType.CHAT, "{AdminEditVillageSpawn}");
			}
			display();
		}, "&5Edit Spawn Location!",
				"&bClick to edit!");
	}
	
	private void locationButton(int slot){
		setItem(slot, new ItemStack(Material.BANNER), player -> {
			objective.setSpawn(player.getLocation());
			if (objective instanceof Kingdom) {
				Cach.StaticKingdom = (Kingdom) objective;
				new Message(player, MessageType.CHAT, "{AdminEditKingdomLocation}");
			}if (objective instanceof Town) {
				Cach.StaticTown = (Town) objective;
				new Message(player, MessageType.CHAT, "{AdminEditTownLocation}");
			}if (objective instanceof Village) {
				Cach.StaticVillage = (Village) objective;
				new Message(player, MessageType.CHAT, "{AdminEditVillageLocation}");
			}
			display();
		}, "&5Edit Dynmap Location!",
				"&bClick to edit!");
	}
	
	private OwnerGUI ownerGui;
	private void ownerButton(int slot){
		setItem(slot, new ItemStack(Material.BEACON), player -> {
			ownerGui = new OwnerGUI(player, this);
		}, "&5Edit Owner!",
				"&bClick to edit!");
		
		if (Validate.notNull(ownerGui) 
				&& !(objective instanceof Kingdom)){
			objective.setOwner(ownerGui.get());
			objective.updateGlass();
			ownerGui.close(player);
			ownerGui = null;
		}
	}
	
	private OwnerGUI preOwnerGui;
	private void preOwnerButton(int slot){
		setItem(slot, new ItemStack(Material.BEACON), player -> {
			preOwnerGui = new OwnerGUI(player, this);
		}, "&5Edit Pre-Owner!",
				"&bClick to edit!");
		
		if (Validate.notNull(preOwnerGui) 
				&& !(objective instanceof Kingdom)){
			((Village)objective).setPreOwner(preOwnerGui.get());
			objective.updateGlass();
			preOwnerGui.close(player);
			
			Cach.StaticVillage = (Village) objective;
			Cach.StaticTown = parent;
			new Message(player, MessageType.CHAT, "{AdminEditVillagePreParent}");
			preOwnerGui = null;
		}
	}
	
	private ParentGUI parentGui;
	private void parentButton(int slot){
		setItem(slot, new ItemStack(Material.BEACON), player -> {
			parentGui = new ParentGUI(player, objective, this);
		}, "&5Edit Parent!",
				"&bClick to edit!");
		
		if (Validate.notNull(parentGui)){
			if (parentGui.get() instanceof Town)
				parent = (Town) parentGui.get();
			
			
			if (Validate.notNull(objective)){
				parent.addChild((Village) objective);
				((Village) objective).setParent(parent);
			}else {
				parent.removeChild((Village) objective);
				((Village) objective).removeParent();
			}
			
			Cach.StaticVillage = (Village) objective;
			Cach.StaticTown = parent;
			new Message(player, MessageType.CHAT, "{AdminEditVillageParent}");
			
			parentGui.close(player);
			parentGui = null;
		}
	}
}
