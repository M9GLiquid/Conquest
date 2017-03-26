package eu.kingconquest.conquest.listener;


import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.core.Rocket;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.database.YmlStorage;
import eu.kingconquest.conquest.event.CaptureCompleteEvent;
import eu.kingconquest.conquest.event.CaptureNeutralEvent;
import eu.kingconquest.conquest.event.CaptureStartEvent;
import eu.kingconquest.conquest.event.CaptureZoneExitEvent;
import eu.kingconquest.conquest.event.NeutralCaptureTrapEvent;
import eu.kingconquest.conquest.hook.EconAPI;
import eu.kingconquest.conquest.util.Cach;
import eu.kingconquest.conquest.util.Marker;
import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.MessageType;
import eu.kingconquest.conquest.util.Validate;

public class CaptureProgressListener implements Listener{
	private WeakHashMap<UUID, Integer> compare = new WeakHashMap<UUID, Integer>();
	private static PluginManager pm = Bukkit.getServer().getPluginManager();
	private Player player;
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onCaptureStart(CaptureStartEvent e){
		Village village = (Village) e.getObjective();
		PlayerWrapper wrapper = PlayerWrapper.getWrapper(e.getPlayer());
		
		Cach.StaticVillage = village;
		Cach.StaticKingdom = village.getOwner();
		new Message(MessageType.BROADCAST, "{WarnDistress}");
		
		//Run Mob Spawns as defence if objective owner isn't Neutral
		if (village.getPreOwner().equals(wrapper.getKingdom(village.getWorld()))){
			pm.callEvent(new NeutralCaptureTrapEvent(village.getPreOwner().getUUID(), "ZombieTrap", village.getLocation(), true, 20));
			pm.callEvent(new NeutralCaptureTrapEvent(village.getPreOwner().getUUID(), "ZombieTrap", village.getLocation(), true, 30));
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onCaptureSuccess(CaptureCompleteEvent e){
		Village village = (Village) e.getObjective();
		village.getAttackers().values().forEach(kuuid->{
			compare.put(kuuid, ((Validate.notNull(compare.get(kuuid)) ? compare.get(kuuid) : 1) + 1));
		});
		UUID key = Collections.max(compare.entrySet(), Map.Entry.comparingByValue()).getKey();
		village.getAttackers().forEach((uuid, kuuid)->{
			System.out.println("Users: " + Bukkit.getPlayer(uuid).getName());
			System.out.println("Users Kingdom: " + Kingdom.getKingdom(kuuid, village.getWorld()).getName());
			System.out.println("Key: " + Kingdom.getKingdom(key, village.getWorld()).getName());
			if (kuuid.equals(key)){
				player = Bukkit.getPlayer(uuid);
				return;
			}
		});
		PlayerWrapper wrapper = PlayerWrapper.getWrapper(player);
		
		wrapper.getBoardType(player);
		
		village.setOwner(wrapper.getKingdom(player.getWorld()));
		village.setPreOwner(wrapper.getKingdom(player.getWorld()));
		Marker.update(village);
		village.updateGlass();
		
		boolean FullCapture = true;
		Cach.StaticVillage = village;
		Cach.StaticKingdom = village.getOwner();
		if (village.hasParent()){ //If Child with Parent
			for (Village v : village.getParent().getChildren()){
				if (!v.getOwner().equals(village.getOwner())){
					FullCapture = false;
					break;
				}
			}
			Cach.StaticTown = village.getParent();
			if (FullCapture){ // Towns children all got the same Owner
				new Rocket(village.getLocation(), true, true, 2, 45, village.getOwner().getIntColor()); // Rocket on Success
				new Rocket(village.getParent().getLocation(), false, true, 4, 45, village.getOwner().getIntColor()); // Rocket on Success
				village.getParent().getChildren().forEach(child->{
					new Rocket(child.getLocation(), false, true, 1, 35, village.getOwner().getIntColor()); // Rocket on Success
					EconAPI.addFunds(village.getOwner().getUUID(), YmlStorage.getDouble("CapCash", village.getLocation())); // Add Funds for each villageS
				});
				village.getAttackers().forEach((uuid, kuuid)->{
					new Message(Bukkit.getPlayer(uuid), MessageType.CHAT, "{CaptureTownSuccess}");
					new Message(Bukkit.getPlayer(uuid), MessageType.CHAT, "{TownCaptured}");
					EconAPI.addFunds(Bukkit.getPlayer(uuid), YmlStorage.getDouble("CapCash", village.getLocation()));
				});
				village.getParent().setOwner(village.getOwner());
				village.getParent().updateGlass();
				Marker.update(village.getParent());
			}
		}else{ //If Child without Parent
			village.getAttackers().forEach((uuid, kuuid)->{
				new Message(player, MessageType.CHAT, "{CaptureVillageSuccess}");
				EconAPI.addFunds(Bukkit.getPlayer(uuid), YmlStorage.getDouble("CapCash", village.getLocation()));
			});
			EconAPI.addFunds(village.getOwner().getUUID(), YmlStorage.getDouble("CapCash", village.getLocation()));
			new Rocket(village.getLocation(), false, true, 1, 35, village.getOwner().getIntColor()); // Rocket on Success
		}
		new Message(MessageType.BROADCAST, "{Captured}");
		
		pm.callEvent(new CaptureZoneExitEvent(null, village));
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onCaptureNeutral(CaptureNeutralEvent e){
		Village village = (Village) e.getObjective();
		
		if (!village.getPreOwner().isNeutral())
			village.setPreOwner(Kingdom.getKingdom("Neutral", village.getWorld()));
		
		village.setNeutral();
		if (village.hasParent())
			village.getParent().setNeutral();

		Cach.StaticVillage = village;
		Cach.StaticKingdom = village.getOwner();
		new Message(MessageType.BROADCAST, "{WarnNeutral}");
		pm.callEvent(new NeutralCaptureTrapEvent(village.getPreOwner().getUUID(), "ZombieTrap", village.getLocation(), true, 20));
		
		//Run Traps bought by the kingdom as defence if objective owner isn't Neutral
	}
}