package eu.kingconquest.conquest.listener;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.core.Rocket;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.database.YmlStorage;
import eu.kingconquest.conquest.event.CaptureCompleteEvent;
import eu.kingconquest.conquest.event.CaptureNeutralEvent;
import eu.kingconquest.conquest.event.CaptureStartEvent;
import eu.kingconquest.conquest.event.NeutralCaptureTrapEvent;
import eu.kingconquest.conquest.hook.EconAPI;
import eu.kingconquest.conquest.util.Cach;
import eu.kingconquest.conquest.util.Marker;
import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.MessageType;

public class CaptureProgressListener implements Listener{
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onCaptureStart(CaptureStartEvent e){
		Village village = (Village) e.getObjective();
		PlayerWrapper wrapper = PlayerWrapper.getWrapper(e.getPlayer());
		
		Cach.StaticVillage = village;
		Cach.StaticKingdom = village.getOwner();
		
		//Run Mob Spawns as defence if objective owner isn't Neutral
		if (village.getPreOwner().equals(wrapper.getKingdom(village.getWorld()))){
			Bukkit.getServer().getPluginManager().callEvent(new NeutralCaptureTrapEvent(village.getPreOwner().getUUID(), "ZombieTrap", village.getLocation(), true, 20));
			Bukkit.getServer().getPluginManager().callEvent(new NeutralCaptureTrapEvent(village.getPreOwner().getUUID(), "ZombieTrap", village.getLocation(), true, 30));
			new Message(MessageType.BROADCAST, "{WarnDistress}");
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onCaptureSuccess(CaptureCompleteEvent e){
		Village village = (Village) e.getObjective();
		Player player = e.getPlayer();
		PlayerWrapper wrapper = PlayerWrapper.getWrapper(player);
		
		wrapper.getBoardType(player);
		
		village.removeCapturing(player);
		village.setOwner(wrapper.getKingdom(player.getWorld()));
		village.setPreOwner(wrapper.getKingdom(player.getWorld()));
		village.removeAttacker(player);
		village.removeDefender(player);
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
				new Message(player, MessageType.CHAT, "{CaptureTownSuccess}");
				new Message(player, MessageType.CHAT, "{TownCaptured}");
				EconAPI.addFunds(player, YmlStorage.getDouble("CapCash", village.getLocation()));
				village.getParent().setOwner(village.getOwner());
				village.getParent().updateGlass();
			}
		}else{ //If Child without Parent
			new Message(player, MessageType.CHAT, "{CaptureVillageSuccess}");
			EconAPI.addFunds(village.getOwner().getUUID(), YmlStorage.getDouble("CapCash", village.getLocation()));
			EconAPI.addFunds(player, YmlStorage.getDouble("CapCash", village.getLocation()));
			new Rocket(village.getLocation(), false, true, 1, 35, village.getOwner().getIntColor()); // Rocket on Success
		}
		new Message(MessageType.BROADCAST, "{Captured}");
		if (village.getAttackers().size() < 1)
			village.stop();
		village.addDefender(player);
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onCaptureNeutral(CaptureNeutralEvent e){
		Village village = (Village) e.getObjective();
		
		if (!village.getPreOwner().isNeutral()){
			village.setPreOwner(Kingdom.getKingdom("Neutral", village.getWorld()));
		}
		village.setNeutral();
		if (village.hasParent())
			village.getParent().setNeutral();
		
		Cach.StaticVillage = village;
		Cach.StaticKingdom = village.getPreOwner();
		if (village.isNeutral() && village.getProgress() <= 10.0){
			Bukkit.getServer().getPluginManager().callEvent(new NeutralCaptureTrapEvent(village.getPreOwner().getUUID(), "ZombieTrap", village.getLocation(), true, 20));
			//Run Traps bought by the kingdom as defence if objective owner isn't Neutral
		}
	}
}