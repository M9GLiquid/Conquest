package eu.kingconquest.conquest.core;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import eu.kingconquest.conquest.Main;
import eu.kingconquest.conquest.database.Config;
import eu.kingconquest.conquest.event.CaptureCompleteEvent;
import eu.kingconquest.conquest.event.CaptureNeutralEvent;
import eu.kingconquest.conquest.event.CaptureStartEvent;
import eu.kingconquest.conquest.util.ChatManager;

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
		//Move to Village class

		if (village.getTaskID() > 0)
			return;

		if(village.getProgress() >= 100.0d 
				&& village.getAttackers().size() < 1){
			wrapper.getScoreboard().CaptureBoard(player, village);
			return;
		}

		//If Player is attacking Call Event
		if (village.getAttackers().containsKey(player.getUniqueId()))
			pm.callEvent(new CaptureStartEvent(player, village));
		wrapper.getScoreboard().CaptureBoard(player, village);

		village.setTaskID(Bukkit.getServer().getScheduler()
				.scheduleSyncRepeatingTask(Main.getInstance(), new Runnable(){
					@Override
					public void run(){
						wrapper.getScoreboard().add(5, ChatManager.Format("&e" + village.getProgress() + "%"));	
						wrapper.getScoreboard().send(player);

						System.out.println("Test 1");
						System.out.println(village.getProgress());
						
						// On successfull capture call event
						if (village.getProgress() >= 100.0d
								&& village.getAttackers().size() > 0 
								&& village.getPreOwner().equals(Kingdom.getNeutral(village.getWorld()))){
							pm.callEvent(new CaptureCompleteEvent(player, village));
							return;
						}
						System.out.println("Test 2");
						System.out.println(village.getProgress());

						// Defending
						if (village.getDefenders().containsKey(player.getUniqueId())
								&& wrapper.getKingdom().equals(village.getOwner())
								&& (village.getProgress() < 100.0d)) {
							village.setProgress(village.getProgress() + (Village.getCapSpeed() * village.getDefenders().size()));
						}

						System.out.println("Test 3");
						System.out.println(village.getProgress());
						// Attacking
						if (village.getAttackers().containsKey(player.getUniqueId())
								&& !(wrapper.getKingdom().equals(village.getOwner()))
								&& !(village.getOwner().isNeutral())){
							System.out.println("Test 4");
							System.out.println(village.getProgress());
							village.setProgress(village.getProgress() - (Village.getCapSpeed() * village.getAttackers().size()));
						}

						// If Progress is less than or equal to 0.0 call event
						if ((village.getProgress() <= 0.0d)){

							System.out.println("Test 5");
							System.out.println(village.getProgress());
							
							village.setProgress(0.0d);
							pm.callEvent(new CaptureNeutralEvent(player, village)); 
						}

						// Neutral Attacking
						if (village.getOwner().isNeutral()
								&& village.getAttackers().containsKey(player.getUniqueId())){
							village.setProgress(village.getProgress() + (Village.getCapSpeed() * village.getAttackers().size()));
						}
					}
				}, 0, Config.getLongs("CaptureRate", village.getLocation())));
	}
}
