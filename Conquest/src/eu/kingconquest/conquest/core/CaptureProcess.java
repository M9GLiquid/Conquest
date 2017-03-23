package eu.kingconquest.conquest.core;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;

import eu.kingconquest.conquest.Main;
import eu.kingconquest.conquest.Scoreboard.CaptureBoard;
import eu.kingconquest.conquest.database.YmlStorage;
import eu.kingconquest.conquest.event.CaptureCompleteEvent;
import eu.kingconquest.conquest.event.CaptureNeutralEvent;
import eu.kingconquest.conquest.event.CaptureStartEvent;
import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.MessageType;

public class CaptureProcess{
	private PluginManager pm = Bukkit.getServer().getPluginManager();
	private double capRate;
	private boolean warnNeutral = false;
	private boolean warnDistress = false;
	
	/**
	 * Start capturing!
	 * 
	 * @param player - Player instance
	 * @return void
	 */
	public CaptureProcess(Player player, Village village){
		// If player is in another GameMode than Survival
		if (!player.getGameMode().equals(GameMode.SURVIVAL)){
			village.removeAttacker(player);
			village.removeDefender(player);
			return;
		}
		new CaptureBoard(village);	
		// If Task already running or somebody already Attacking
		if (village.getTaskID() > 0 || 
				(village.getProgress() >= 100.0d 
				&& village.getAttackers().size() < 1))
			return;
		
		village.setTaskID(Bukkit.getServer().getScheduler()
				.scheduleSyncRepeatingTask(Main.getInstance(), new Runnable(){
					@Override
					public void run(){
						
						capRate = YmlStorage.getDouble("CaptureRate", village.getLocation());
						new CaptureBoard(village);
						
						// On successfull capture call event
						if (village.getProgress() >= 100.0d
								&& village.getAttackers().size() > 0 
								&& village.getPreOwner().equals(Kingdom.getNeutral(village.getWorld()))){
							callEvent(new CaptureCompleteEvent(player, village));
							return;
						}
						
						if (village.getDefenders().size() > village.getAttackers().size()) // Defending
							if  (village.getProgress() < 100.0d) //Defending Kingdom (If It's under attack)
								village.setProgress(village.getProgress() + ((capRate * village.getAttackers().size()) * 0.1) - ((capRate * village.getDefenders().size()) * 0.1));
							else if (village.getDefenders().size() < village.getAttackers().size()){ // Attacking
								System.out.println(" Attacking ");
								if  (!village.getOwner().isNeutral() && village.getPreOwner().isNeutral()){ // Attacking Kingdom
									System.out.println(" Attacking Kingdom ");
									if (!warnDistress){ //Send Event once
										//If Objective is under attack Call Event
										pm.callEvent(new CaptureStartEvent(player, village));
										warnDistress = !warnDistress;
									}
									village.setProgress(village.getProgress() - ((capRate * village.getAttackers().size()) * 0.1) + ((capRate * village.getDefenders().size()) * 0.1));
								}else{ // Attacking Neutral
									System.out.println(" Attacking Neutral ");
									if (!warnNeutral){ //Send Event once
										if ((village.getProgress() <= 0.0d))
											//If Objective goes neutral Call Event
											pm.callEvent(new CaptureNeutralEvent(player, village));
										warnNeutral = !warnNeutral;
									}
									village.setProgress(village.getProgress() + ((capRate * village.getAttackers().size()) * 0.1) - ((capRate * village.getDefenders().size()) * 0.1));
								}
							}else{// Same amount attacking as defending
								village.getAttackers().forEach((playerID, kingdomID)->{
									new Message(Bukkit.getPlayer(playerID), MessageType.CHAT, "{CannotCapture}");
								});
								village.getDefenders().forEach((playerID, kingdomID)->{
									new Message(Bukkit.getPlayer(playerID), MessageType.CHAT, "{CaptureHaulted}");
								});
							}
						
						/*
						
						// Defending
						if (village.getDefenders().containsKey(player.getUniqueId())
								&& wrapper.getKingdom(player.getWorld()).equals(village.getOwner())
								&& (village.getProgress() < 100.0d)) {
							village.setProgress(village.getProgress() + ((capRate * village.getAttackers().size()) * 0.1) - ((capRate * village.getDefenders().size()) * 0.1));
						}
						
						// Attacking
						if (village.getAttackers().containsKey(player.getUniqueId())
								&& !(wrapper.getKingdom(player.getWorld()).equals(village.getOwner()))
								&& !(village.getOwner().isNeutral())){
							village.setProgress(village.getProgress() - ((capRate * village.getAttackers().size()) * 0.1) + ((capRate * village.getDefenders().size()) * 0.1));
						}
						
						// If Progress is less than or equal to 0.0 call event
						if ((village.getProgress() <= 0.0d)){
							
							village.setProgress(0.0d);
							pm.callEvent(new CaptureNeutralEvent(player, village));
						}
						// Neutral Attacking
						if (village.getOwner().isNeutral()
								&& village.getAttackers().containsKey(player.getUniqueId())){
							village.setProgress(village.getProgress() + ((capRate * village.getAttackers().size()) * 0.1) - ((capRate * village.getDefenders().size()) * 0.1));
						}*/
					}
				}, 0, 2));
	}
	private void callEvent(Event event) throws IllegalStateException{
		for (int i = 0; i <= 10; i++){
			try{
				pm.callEvent(event);
				return;
			}catch(IllegalStateException e){
				new Message(null, MessageType.CONSOLE, "Tried too Call Event: " + event.getEventName());
			}
		}
	}
}
