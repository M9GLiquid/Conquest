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
	private boolean standStill = false;
	
	/**
	 * Start capturing!
	 * 
	 * @param player - Player instance
	 * @return void
	 */
	public CaptureProcess(Player player, Village village){
		// If player dies and enters in another GameMode than Survival
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
						
						
						if (village.getDefenders().size() > village.getAttackers().size()) // Defending
							onDefend(player, village);
						else if (village.getDefenders().size() < village.getAttackers().size()){ // Attacking
							if  (!village.getOwner().isNeutral() && !village.getPreOwner().isNeutral()) // Attacking Kingdom
								onAttackKingdom(player, village);
							else // Attacking Neutral
								onAttackNeutral(player, village);
						}else// Same amount attacking as defending
							onStandStill(player, village);
					}
				}, 0, 10));
	}
	
	private void onAttackNeutral(Player player, Village village){
		standStill = false;
		
		village.setProgress(village.getProgress() + (((capRate * village.getAttackers().size()) * 0.33) - ((capRate * village.getDefenders().size()) * 0.11)));
		
		// On successfull capture call event
		if (village.getProgress() >= 100.0d 
				&& village.getAttackers().size() > 0  
				&& village.getPreOwner().equals(Kingdom.getNeutral(village.getWorld())))
			callEvent(new CaptureCompleteEvent(player, village));
	}
	
	private void onAttackKingdom(Player player, Village village){
		if (!warnDistress){ //Send Event once
			//If Objective is under attack Call Event
			pm.callEvent(new CaptureStartEvent(player, village));
			warnDistress = !warnDistress;
		}
		standStill = false;
		village.getAttackers().forEach((uuid, kuuid)->{
			if (Kingdom.getKingdom(kuuid, village.getWorld()).equals(village.getOwner()) 
					&& village.getProgress() >= 100.0d)
				village.addDefender(Bukkit.getPlayer(uuid));;
		});
		village.setProgress(village.getProgress() - (((capRate * village.getAttackers().size()) * 0.33) + ((capRate * village.getDefenders().size()) * 0.11)));
		
		if(village.getProgress() <= 0.0d)
			if (!warnNeutral){ //Send Event once
				//If Objective goes neutral Call Event
				pm.callEvent(new CaptureNeutralEvent(player, village));
				warnNeutral = !warnNeutral;
			}
	}
	
	private void onDefend(Player player, Village village){
		if  (village.getProgress() < 100.0d) //Defending Kingdom (If It's under attack)
			village.setProgress(village.getProgress() + (((capRate * village.getAttackers().size()) * 0.33) - ((capRate * village.getDefenders().size()) * 0.11)));
	}
	
	private void onStandStill(Player player, Village village){
		if (!standStill){
			village.getAttackers().forEach((playerID, kingdomID)->{
				new Message(Bukkit.getPlayer(playerID), MessageType.CHAT, "{CannotCapture}");
			});
			village.getDefenders().forEach((playerID, kingdomID)->{
				new Message(Bukkit.getPlayer(playerID), MessageType.CHAT, "{CaptureHaulted}");
			});
			standStill = !standStill;
		}
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
