package eu.kingconquest.conquest.core.gui;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.kingconquest.conquest.Main;
import eu.kingconquest.conquest.core.Objective;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.core.Town;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.core.util.Cach;
import eu.kingconquest.conquest.core.util.ChatManager;
import eu.kingconquest.conquest.core.util.ChestGui;
import eu.kingconquest.conquest.core.util.Config;
import eu.kingconquest.conquest.core.util.Validate;


// Example 
public class TeleportGUI extends ChestGui{
	private static int taskID;
	private ChestGui previous;
	private Player p;
	private ArrayList<Objective> targets= new ArrayList<Objective>();

	public TeleportGUI(Player p, ChestGui previousGui){
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
		Town.getTowns().forEach(town->{
			if (PlayerWrapper.getWrapper(p).getKingdom().equals(town.getOwner()))
				addTarget(town);
		});
		Config.getWorlds().forEach(world->{
			Village.getVillages(world).stream()
			.filter(village->!village.hasParent())
			.forEach(village->{
				addTarget(village);
			});
		});
		if (getTargets().size() == 0)
			return;
		createGui(p, "&6Teleport Gui", targets.size() >  1 ? targets.size() -1 : 0);
		display();
	}
	@Override
	public void display(){
		PlayerWrapper wrapper = PlayerWrapper.getWrapper(p);
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
		/**
		 * IF objective's owner is same as players kingdom,
		 * If Orphane village OR
		 * Villages Parents Owner is same as players Kingdom
		 */
		for(int i = 9; i < 54; i++) 
			if (getCurrentItem() < getItems() || getItems() != 0 
			&& targets.get(getCurrentItem()).getOwner().equals(wrapper.getKingdom())){
					if (targets.get(getCurrentItem()) instanceof Village && !((Village)targets.get(getCurrentItem())).hasParent() 
							|| ((Village)targets.get(getCurrentItem())).getParent().getOwner().equals(wrapper.getKingdom())){
						tpButton(i);
					}
				tpButton(i);
			}
	}

	private void tpButton(int i){
		setItem(i, new ItemStack(Material.ENDER_PEARL), player -> {
			teleport(targets.get(getCurrentItem() -1).getLocation().clone());
		},"&1Teleport to: &f" + targets.get(getCurrentItem()).getName()
		,"&1-----------------"
				+ "\n&aSpawn Location: "
				+ "\n -&1X: &f" + Math.floor(targets.get(getCurrentItem()).getLocation().getX())
				+ "\n -&1Y: &f" + Math.floor(targets.get(getCurrentItem()).getLocation().getY())
				+ "\n -&1Z: &f" + Math.floor(targets.get(getCurrentItem()).getLocation().getZ())
				);
		setCurrentItem(getCurrentItem()+1);
	}

	public ArrayList<Objective> getTargets(){
		return targets;
	}
	public void addTarget(Objective objective){
		targets.add(objective);
	}


	private void teleport(Location loc){
		Cach.tpDelay = Config.TeleportDelay.get(p.getWorld())/20;
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
		}, Config.TeleportDelay.get(p.getWorld()));
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
