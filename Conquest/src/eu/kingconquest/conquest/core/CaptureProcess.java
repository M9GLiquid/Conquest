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

	/**
	 * Start capturing!
	 * 
	 * @param player - Player instance
	 * @return void
	 */
	public CaptureProcess(Player player, Village village){
		PlayerWrapper wrapper = PlayerWrapper.getWrapper(player);
		
		// If player is in another GameMode than Survival
		if (!player.getGameMode().equals(GameMode.SURVIVAL)){
			village.removeAttacker(player);
			village.removeDefender(player);
			return;
		}
			
		// If Task already running
		if (village.getTaskID() > 0)
			return;

		// If already captured
		if(village.getProgress() >= 100.0d 
				&& village.getAttackers().size() < 1){ 
			new CaptureBoard(player, village);
			return;
		}

		//If Player is attacking Call Event
		if (village.getAttackers().containsKey(player.getUniqueId()))
			pm.callEvent(new CaptureStartEvent(player, village));
		 	new CaptureBoard(player, village);

		village.setTaskID(Bukkit.getServer().getScheduler()
				.scheduleSyncRepeatingTask(Main.getInstance(), new Runnable(){
					@Override
					public void run(){
						new CaptureBoard(player, village);	
						
						// On successfull capture call event
						if (village.getProgress() >= 100.0d
								&& village.getAttackers().size() > 0 
								&& village.getPreOwner().equals(Kingdom.getNeutral(village.getWorld()))){
							for (int i = 0; i < 10; i++){
								try{
									callEvent(new CaptureCompleteEvent(player, village));
									return;
								}catch(IllegalStateException e){}
							}
						}

						// Defending
						if (village.getDefenders().containsKey(player.getUniqueId())
								&& wrapper.getKingdom(player.getWorld()).equals(village.getOwner())
								&& (village.getProgress() < 100.0d)) {
							village.setProgress(village.getProgress() + (Village.getCapSpeed() * village.getDefenders().size()));
						}

						// Attacking
						if (village.getAttackers().containsKey(player.getUniqueId())
								&& !(wrapper.getKingdom(player.getWorld()).equals(village.getOwner()))
								&& !(village.getOwner().isNeutral())){
							village.setProgress(village.getProgress() - (Village.getCapSpeed() * village.getAttackers().size()));
						}

						// If Progress is less than or equal to 0.0 call event
						if ((village.getProgress() <= 0.0d)){
							
							village.setProgress(0.0d);
							pm.callEvent(new CaptureNeutralEvent(player, village));
						}

						// Neutral Attacking
						if (village.getOwner().isNeutral()
								&& village.getAttackers().containsKey(player.getUniqueId())){
							village.setProgress(village.getProgress() + (Village.getCapSpeed() * village.getAttackers().size()));
						}
					}
				}, 0, YmlStorage.getLong("CaptureRate", village.getLocation())));
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
