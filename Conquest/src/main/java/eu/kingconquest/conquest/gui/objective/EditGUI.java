package eu.kingconquest.conquest.gui.objective;

import eu.kingconquest.conquest.chatinteract.NamePrompt;
import eu.kingconquest.conquest.core.*;
import eu.kingconquest.conquest.gui.PlayerGUI;
import eu.kingconquest.conquest.util.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

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

    @SuppressWarnings("all")
	private void teleportButton(int slot){
		setItem(slot, new ItemStack(Material.ENDER_PEARL), player -> {
			player.teleport(objective.getLocation());
		}, "&2Teleport", 
				"&6Teleport to target");
	}

    @SuppressWarnings("all")
	private void playerButton(int slot){
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta skull = (SkullMeta) head.getItemMeta();
        skull.setOwningPlayer(player);
		head.setItemMeta(skull);
        setSkullItem(slot, head, player ->
                        new PlayerGUI(player, this, objective), "&4Manage Members",
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
        setItem(slot, new ItemStack(Material.PAPER), player ->
                        display(), "&3Information"
				,  displayInfo());
	}
	private String displayInfo() {
		String str = "";
		
		if (objective instanceof Kingdom) {
			Kingdom kingdom = (Kingdom) objective;
			str	= "\n&aName: &f" +kingdom.getColor() + kingdom.getName();
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
		}else if(objective instanceof Town) {
			Town town = (Town) objective;
			str = "\n&aName: &f" + town.getOwner().getColor() + town.getName();
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
		}else if(objective instanceof Village) {
			Village village = (Village) objective;
			str = "\n&aName: &f" + village.getOwner().getColor() + village.getName();
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
		}
		return str;
	}
	
	private NamePrompt namePrompt;

    @SuppressWarnings("all")
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
        setItem(slot, new ItemStack(Material.RED_BED), player -> {
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
	private void locationIcon(int slot){
		setItem(slot, new ItemStack(Material.PAPER), player -> {
		}, "&r&7<< &5&lEdit Locations &7>>", "");
	}
	private void locationButton(int slot){
        setItem(slot, new ItemStack(Material.BLACK_BANNER), player -> {
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
        setItem(slot, new ItemStack(Material.BEACON), player ->
                        ownerGui = new OwnerGUI(player, this), "&5Edit Owner!",
				"&bClick to edit!");
		
		if (Validate.notNull(ownerGui) 
				&& !(objective instanceof Kingdom)){
			objective.setOwner(ownerGui.get());
			objective.updateGlass();
			ownerGui.close(player);
			Cach.StaticKingdom = objective.getOwner();
			if (objective instanceof Village){
				Cach.StaticVillage = (Village) objective;
				new Message(player, MessageType.CHAT, "{AdminEditVillageOwner}");
			}else if (objective instanceof Town){
				Cach.StaticTown = (Town) objective;
				new Message(player, MessageType.CHAT, "{AdminEditTownOwner}");
			}
			ownerGui = null;
		}
	}

    @SuppressWarnings("all")
    private void ownerIcon(int slot){
        setItem(slot, new ItemStack(Material.PAPER), player -> {
        }, "&r&7<< &5&lEdit Owners &7>>", "");
    }
	private OwnerGUI preOwnerGui;

    @SuppressWarnings("all")
    private void preOwnerButton(int slot){
        setItem(slot, new ItemStack(Material.BEACON), player ->
                        preOwnerGui = new OwnerGUI(player, this), "&5Edit Pre-Owner!",
                "&bClick to edit!");

        if (Validate.notNull(preOwnerGui)
                && !(objective instanceof Kingdom)){
            objective.updateGlass();
            Cach.StaticKingdom = objective.getOwner();
            if (objective instanceof Village){
                ((Village)objective).setPreOwner(preOwnerGui.get());
                preOwnerGui.close(player);
                Cach.StaticVillage = (Village) objective;
                new Message(player, MessageType.CHAT, "{AdminEditVillagePreOwner}");
            }
            preOwnerGui = null;
        }
    }
	
	private ParentGUI parentGui;

    @SuppressWarnings("all")
    private void parentButton(int slot){
        setItem(slot, new ItemStack(Material.BEACON), player ->
                        parentGui = new ParentGUI(player, objective, this), "&5Edit Parent!",
                "&bClick to edit!");

        if (Validate.notNull(parentGui)){
            if (parentGui.get() instanceof Town)
                parent = (Town) parentGui.get();
            if (Validate.notNull(parent)){
                parent.addChild((Village) objective);
                ((Village) objective).setParent(parent);
                Cach.StaticKingdom = objective.getOwner();
                Cach.StaticVillage = (Village) objective;
                Cach.StaticTown = parent;
                new Message(player, MessageType.CHAT, "{AdminEditVillageParent}");
            }else{
                ((Village) objective).getParent().removeChild((Village) objective);
                ((Village) objective).removeParent();

                new Message(player, MessageType.CHAT, "{AdminRemoveVillageParent}");
            }


            parentGui.close(player);
            parentGui = null;
        }
    }
}
