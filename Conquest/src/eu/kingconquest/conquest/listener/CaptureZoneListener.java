package eu.kingconquest.conquest.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import eu.kingconquest.conquest.Main;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.event.CaptureCompleteEvent;
import eu.kingconquest.conquest.event.CaptureSemiEvent;
import eu.kingconquest.conquest.event.CaptureStartEvent;
import eu.kingconquest.conquest.event.CaptureZoneEnterEvent;
import eu.kingconquest.conquest.event.CaptureZoneExitEvent;
import eu.kingconquest.conquest.util.Config;

public class CaptureZoneListener implements Listener{
	private PluginManager pm = Bukkit.getServer().getPluginManager();
	private PlayerWrapper wrapper;
	private Player p;

	@EventHandler
	public void onZoneEnter(CaptureZoneEnterEvent e){
		Village village = (Village) e.getObjective();
		this.p = e.getPlayer();
		wrapper = PlayerWrapper.getWrapper(p);
		// If Player already is Capturing
		if (village.isCapturing(p))
			return;
		/* If Defendings Players Kingdom is owner of Objective*/
		if (village.getOwner().equals(wrapper.getKingdom())){
			village.addCapturing(p);
			village.removeAttacker(p);
			village.addDefender(p);

			/** Incase of equal defenders and attackers or if players kingdom is already owner and capture progress is over or equal to 100*/
			if (!(village.getAttackers().size() == village.getDefenders().size()) 
					|| (wrapper.getKingdom().equals(village.getOwner()) 
							&& village.getProgress() >= 100.0d))
				return;
		}else{
			village.addCapturing(p);
			village.addAttacker(p);
		}
		start(village);
	}

	@EventHandler
	public void onZoneExit(CaptureZoneExitEvent e){
		Village village = (Village) e.getObjective();
		p = e.getPlayer();
		village.removeCapturing(p);
		village.removeAttacker(p);
		village.removeDefender(p);
		if (village.getAttackers().size() < 1)
			village.stop();
	}

	/**
	 * Start capturing!
	 * 
	 * @param p - Player instance
	 * @return void
	 */
	private void start(Village village){
		if (village.getTaskID() > 0)
			return;
		
		if(village.getProgress() >= 100.0d 
				&& village.getAttackers().size() < 1){
			wrapper.getScoreboard().captureBoard(p, village);
			return;
		}
		
		//If Player is attacking Call Event
		if (village.getAttackers().containsKey(p.getUniqueId()))
			pm.callEvent(new CaptureStartEvent(p, village));

		village.setTaskID(p.getServer().getScheduler()
				.scheduleSyncRepeatingTask(Main.getInstance(), new Runnable(){
					@Override
					public void run(){
						wrapper.getScoreboard().captureBoard(p, village);
						
						// On successfull capture Call the Event
						if (village.getProgress() >= 100.0d
								&& village.getAttackers().size() > 0 
								&& village.getPreOwner().equals(wrapper.getKingdom())){
							pm.callEvent(new CaptureCompleteEvent(p, village));
							return;
						}

						// Defending
						if (village.getDefenders().containsKey(p.getUniqueId())
								&& wrapper.getKingdom().equals(village.getOwner())
								&& (village.getProgress() >= 100.0d)) {
							village.setProgress(village.getProgress() + (Village.getCapSpeed() * village.getDefenders().size()));
						}
						// Attacking
						if (village.getAttackers().containsKey(p.getUniqueId())
								&& !(wrapper.getKingdom().equals(village.getOwner()))
								&& !(village.getOwner().isNeutral())){
							village.setProgress(village.getProgress() - (Village.getCapSpeed() * village.getAttackers().size()));
						}

						// If Progress is less than 0.0 call the Event
						if (village.getProgress() <= 0.0d){
							village.setProgress(0.0d);

							// Set to Neutral kingdomital
							pm.callEvent(new CaptureSemiEvent(p, village));
						}

						// Neutral Attacking
						if (village.getOwner().isNeutral()
								&& village.getAttackers().containsKey(p.getUniqueId()))
							if (village.getPreOwner().isNeutral()){
								village.setProgress(village.getProgress() - (Village.getCapSpeed() * village.getAttackers().size()));
							}else{
								village.setProgress(village.getProgress() + (Village.getCapSpeed() * village.getAttackers().size()));
							}
					}
				}, 0, Config.CaptureRate.get(p.getWorld())));
	}
}
