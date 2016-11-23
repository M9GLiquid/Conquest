package eu.kingconquest.conquest.listener;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.core.Rocket;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.event.CaptureCompleteEvent;
import eu.kingconquest.conquest.event.CaptureSemiEvent;
import eu.kingconquest.conquest.event.CaptureStartEvent;
import eu.kingconquest.conquest.util.Cach;
import eu.kingconquest.conquest.util.ChatManager;
import eu.kingconquest.conquest.util.Config;
import eu.kingconquest.conquest.util.Marker;

public class CaptureProgressListener implements Listener{
	private Player p;
	private Rocket rocket;

	@EventHandler
	public void onCaptureStart(CaptureStartEvent e){
		Village village = (Village) e.getObjective();
		Cach.StaticVillage = village;
		ChatManager.Broadcast(Config.getChat("WarnDistress"));
	}

	@EventHandler
	public void onCaptureSuccess(CaptureCompleteEvent e){
		Village village = (Village) e.getObjective();
		p = e.getPlayer();
		PlayerWrapper wrapper = PlayerWrapper.getWrapper(p);

		village.removeCapturing(p);
		village.setPreOwner(village.getOwner());
		village.setOwner(wrapper.getKingdom());
		village.removeAttacker(p);
		village.addDefender(p);
		Cach.StaticKingdom = village.getOwner();
		Cach.StaticVillage = village;
		Marker.update(village);
		village.updateGlass();

		boolean FullCapture = true;
		if (village.hasParent()){
			System.out.println(" 10 " + village.getParent().getChildren().size());
			for (Village v : village.getParent().getChildren()){
				System.out.println(" 20 ");
				System.out.println(!v.getOwner().equals(village.getOwner()));
				System.out.println(v.getOwner().getName());
				System.out.println(village.getOwner().getName());
				if (!v.getOwner().equals(village.getOwner())){ //If any child is not owned by parents kingdom
					FullCapture = false;
					break;
				}
			}
			if (FullCapture){
			Cach.StaticTown = village.getParent();
			ChatManager.Chat(p, Config.getChat("CaptureTownSuccess"));
			village.getParent().setOwner(village.getOwner());
			village.getParent().updateGlass();
			rocket = new Rocket(village.getParent().getLocation().clone(), false, true, 4, 30, village.getOwner().getColor()); //Rocket on Success
			rocket.spawn();
			village.getParent().getChildren().forEach(child->{
				rocket = new Rocket(child.getLocation().clone(), false, true, 1, 20, village.getOwner().getColor()); //Rocket on Success
				rocket.spawn();
			});
			}
		}else{ //If Child without Parent
			ChatManager.Broadcast(Config.getChat("Captured"));
			Rocket rocket = new Rocket(village.getLocation().clone(), false, true, 1, 20, village.getOwner().getColor()); //Rocket on Success
			rocket.spawn();
		}
		Config.saveVillages(village.getLocation().getWorld());
		village.removeAttacker(p);
		village.removeDefender(p);
		if (village.getAttackers().size() < 1)
			village.stop();
	}


	@EventHandler
	public void onCaptureNeutral(CaptureSemiEvent e){
		Village village = (Village) e.getObjective();
		p = e.getPlayer();

		village.setPreOwner(PlayerWrapper.getWrapper(p).getKingdom());
		village.setNeutral();
		if (village.hasParent())
			village.getParent().setNeutral();

		Cach.StaticVillage = village;
		ChatManager.Broadcast(Config.getChat("WarnNeutral"));
		Config.saveVillages(village.getLocation().getWorld());
	}
}
