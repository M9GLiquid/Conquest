package eu.kingconquest.conquest.core.listener;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.core.event.CaptureCompleteEvent;
import eu.kingconquest.conquest.core.event.CaptureSemiEvent;
import eu.kingconquest.conquest.core.util.Cach;
import eu.kingconquest.conquest.core.util.ChatManager;
import eu.kingconquest.conquest.core.util.Config;
import eu.kingconquest.conquest.core.util.Marker;

public class CaptureProgressListener implements Listener{
	private Player p;

	@EventHandler
	public void onCompleteControle(CaptureCompleteEvent e){
		Village village = (Village) e.getObjective();
		p = e.getPlayer();
		PlayerWrapper wrapper = PlayerWrapper.getWrapper(p);
		
		village.removeCapturing(p);
		village.setProgress(100.0d);
		village.setOwner(wrapper.getKingdom());
		Cach.StaticKingdom = village.getOwner();
		Cach.StaticVillage = village;
		ChatManager.Broadcast(Config.getChat("CapComplete"));
		ChatManager.Chat(p, Config.getChat("CapureVillageSuccess"));
		Marker.update(village);
		village.removeAttacker(p);
		village.addDefender(p);
		village.updateGlass();
		wrapper.getScoreboard().captureBoard(p, village);

		boolean FullCapture = true;
		if (village.getParent() != null){
			for (Village v : village.getParent().getChildren()){
				if (v.getOwner() == null
						|| !v.getOwner().equals(v.getOwner())){
					FullCapture = false;
					break;
				}
			}
			if (FullCapture){
				Cach.StaticTown = village.getParent();
				ChatManager.Chat(p, Config.getChat("CapureTownSuccess"));
				village.getParent().setOwner(village.getOwner());
				village.getParent().updateGlass();
			}
		}
		stop(village);
	}
	
	/**
	 * Stop capturing!
	 * 
	 * @param p - Player instance
	 * @return void
	 */
	public void stop(Village village){
		village.removeAttacker(p);
		village.removeDefender(p);
		if (village.getAttackers().size() != 0 || village.getDefenders().size() != 0)
			return;
		Bukkit.getServer().getScheduler().cancelTask(village.getTaskID());
		village.setTaskID(0);
	}


	@EventHandler
	public void onSemiControle(CaptureSemiEvent e){
		Village village = (Village) e.getObjective();
		p = e.getPlayer();
			
		village.setPreOwner(PlayerWrapper.getWrapper(p).getKingdom());
		village.setNeutral();
		if (village.hasParent())
			village.getParent().setNeutral();

		Cach.StaticVillage = village;
		ChatManager.Broadcast(Config.getChat("Distress"));
	}
}
