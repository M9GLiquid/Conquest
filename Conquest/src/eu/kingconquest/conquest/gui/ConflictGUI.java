package eu.kingconquest.conquest.gui;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.Main;
import eu.kingconquest.conquest.core.Objective;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.database.Config;
import eu.kingconquest.conquest.util.Cach;
import eu.kingconquest.conquest.util.ChatManager;
import eu.kingconquest.conquest.util.ChestGui;
import eu.kingconquest.conquest.util.Validate;


// Example 
public class ConflictGUI extends ChestGui{
	private static int taskID;
	private ChestGui previous;
	private Player p;
	private ArrayList<Objective> targets= new ArrayList<Objective>();

	public ConflictGUI(Player p, ChestGui previousGui){
		super();
		this.previous = previousGui;
		this.p = p;

		create();
	}

	@Override
	public void create(){
		targets.clear();
		if (Validate.isNull(PlayerWrapper.getWrapper(p).getKingdom()))
			return;
		Config.getWorlds().forEach(uniqueID->{
			Village.getVillages(Bukkit.getWorld(uniqueID))
			.forEach(village->{
				if (PlayerWrapper.getWrapper(p).getKingdom().equals(village.getOwner()))
					return;
				if (village.hasParent() 
						&& !targets.contains(village.getOwner()))
					targets.add(village.getParent());
				else
					targets.add(village);
			});
		});
		if (targets.size() < 1)
			return;
		createGui(p, "&6Teleport Gui", targets.size());
		display();
	}
	
	@Override
	public void display(){
		clearSlots();

		//Slot 0
		playerInfo(p);
		//Slot 3
		previous(this);
		//Slot 5
		next(this);
		//Slot 8
		backButton(previous);

		//Slot MAIN
		for(int i = 9; i < 54; i++) {
			if (getCurrentItem() > (targets.size() -1) || getItems() < 1)
				break;
			tpButton(i);
		}
	}

	private void tpButton(int i){
		setItem(i, new ItemStack(Material.ENDER_PEARL), player -> {
			teleport(targets.get(getCurrentItem() -1).getLocation().clone());
			targets.clear();
		},"&1Teleport to: &f" + targets.get(getCurrentItem()).getName()
		,"&1-----------------"
				+ "\n&aSpawn Location: "
				+ "\n -&1X: &f" + Math.floor(targets.get(getCurrentItem()).getLocation().getX())
				+ "\n -&1Y: &f" + Math.floor(targets.get(getCurrentItem()).getLocation().getY())
				+ "\n -&1Z: &f" + Math.floor(targets.get(getCurrentItem()).getLocation().getZ())
				);
		setCurrentItem(getCurrentItem()+1);
	}

	private void teleport(Location loc){
		Cach.tpDelay = Config.getLongs("TeleportDelay", loc);
		ChatManager.Chat(p, Config.getChat("startTP"));
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable(){
			@Override
			public void run(){
				p.setInvulnerable(true);
				loc.setY(256);
				p.teleport(loc);
				startFall();
				ChatManager.Chat(p, Config.getChat("Teleported"));
			}
		}, Config.getLongs("TeleportDelay", loc));
	}

	/**
	 * Fall from Y: 256
	 * Involnerable while falling
	 * @return void
	 */
	private void startFall(){
		taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable(){
			@Override
			public void run(){;
			p.setGliding(false);
			if (p.getLocation().subtract(0, 1, 0).getBlock().getType() != Material.AIR)
				stopFall();
			}
		}, 0, 10);
	}

	/**
	 * Stop the Clock of capturing!
	 * @param p - Player instance
	 * @return void
	 */
	private void stopFall(){
		p.setInvulnerable(false);
		p.getServer().getScheduler().cancelTask(taskID);
	}
}
