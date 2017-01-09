package eu.kingconquest.conquest.listener;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.core.Rocket;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.database.Config;
import eu.kingconquest.conquest.event.CaptureCompleteEvent;
import eu.kingconquest.conquest.event.CaptureNeutralEvent;
import eu.kingconquest.conquest.event.CaptureStartEvent;
import eu.kingconquest.conquest.hook.TNEApi;
import eu.kingconquest.conquest.util.Cach;
import eu.kingconquest.conquest.util.ChatManager;
import eu.kingconquest.conquest.util.Marker;

public class CaptureProgressListener implements Listener{
	private Player player;

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onCaptureStart(CaptureStartEvent e){
		Village village = (Village) e.getObjective();
		Cach.StaticVillage = village;
		Cach.StaticKingdom = village.getOwner();
		ChatManager.Broadcast(Config.getChat("WarnDistress"));
		
		//Run Mob Spawns as defence if objective owner isn't Neutral
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onCaptureSuccess(CaptureCompleteEvent e){
		Village village = (Village) e.getObjective();
		player = e.getPlayer();
		PlayerWrapper wrapper = PlayerWrapper.getWrapper(player);

		wrapper.getScoreboard().KingdomBoard(player);
		village.removeCapturing(player);
		village.setOwner(wrapper.getKingdom(player.getWorld()));
		village.setPreOwner(wrapper.getKingdom(player.getWorld()));
		village.removeAttacker(player);
		village.removeDefender(player);
		Cach.StaticKingdom = village.getOwner();
		Cach.StaticVillage = village;
		Marker.update(village);
		village.updateGlass();

		boolean FullCapture = true;
		if (village.hasParent()){
			for (Village v : village.getParent().getChildren()){
				if (!v.getOwner().equals(village.getOwner())){ 
					FullCapture = false;
					break;
				}
			}
			if (FullCapture){ // Towns children all got the same Owner
			Cach.StaticTown = village.getParent();
			village.getParent().setOwner(village.getOwner());
			village.getParent().updateGlass();
			new Rocket(village.getParent().getLocation(), false, true, 4, 45, village.getOwner().getColor()); // Rocket on Success
			village.getParent().getChildren().forEach(child->{
				new Rocket(child.getLocation(), false, true, 1, 35, village.getOwner().getColor()); // Rocket on Success
				TNEApi.addFunds(village.getOwner().getUUID(), Config.getDouble("CapCash", village.getLocation())); // Add Funds for each 
			});
			ChatManager.Chat(player, Config.getChat("TownCaptured"));
			ChatManager.Chat(player, Config.getChat("CaptureTownSuccess"));
			TNEApi.addFunds(player, Config.getDouble("CapCash", village.getLocation()));
			}
		}else{ //If Child without Parent
			ChatManager.Chat(player, Config.getChat("CaptureVillageSuccess"));
			TNEApi.addFunds(village.getOwner().getUUID(), Config.getDouble("CapCash", village.getLocation()));
			TNEApi.addFunds(player, Config.getDouble("CapCash", village.getLocation()));
			new Rocket(village.getLocation(), false, true, 1, 35, village.getOwner().getColor()); // Rocket on Success
		}
		ChatManager.Broadcast(Config.getChat("Captured"));
	if (village.getAttackers().size() < 1)
		village.stop();
	village.addDefender(player);
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onCaptureNeutral(CaptureNeutralEvent e){
		Village village = (Village) e.getObjective();
		player = e.getPlayer();
		if (!village.getPreOwner().isNeutral()){
			village.setPreOwner(Kingdom.getKingdom("Neutral", player.getWorld()));
		}
		village.setNeutral();
		if (village.hasParent())
			village.getParent().setNeutral();

		Cach.StaticVillage = village;
		Cach.StaticKingdom = village.getPreOwner();
		ChatManager.Broadcast(Config.getChat("WarnNeutral"));
		//Config.saveVillages(village.getWorld());
		//Bukkit.getServer().getPluginManager().callEvent(new NeutralCaptureTrapEvent(village.getPreOwner().getUUID(), "ZombieTrap", village.getLocation(), true, 20));
		//Run Traps bought by the kingdom as defence if objective owner isn't Neutral
	}
}
