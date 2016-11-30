package eu.kingconquest.conquest.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import eu.kingconquest.conquest.core.CaptureProcess;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.event.CaptureZoneEnterEvent;
import eu.kingconquest.conquest.event.CaptureZoneExitEvent;

public class ProximityZoneListener implements Listener{
	private PlayerWrapper wrapper;
	private Player player;

	@EventHandler
	public void onZoneEnter(CaptureZoneEnterEvent e){
		Village village = (Village) e.getObjective();
		this.player = e.getPlayer();
		wrapper = PlayerWrapper.getWrapper(player);
		// If Player already is Capturing
		if (village.isCapturing(player))
			return;
		/* If Defendings Players Kingdom is owner of Objective*/
		if (village.getOwner().equals(wrapper.getKingdom())){
			village.addCapturing(player);
			village.removeAttacker(player);
			village.addDefender(player);

			/** Incase of equal defenders and attackers or if players kingdom is already owner and capture progress is over or equal to 100*/
			if (!(village.getAttackers().size() == village.getDefenders().size()) 
					|| (wrapper.getKingdom().equals(village.getOwner()) 
							&& village.getProgress() >= 100.0d))
				return;
		}else{
			village.addCapturing(player);
			village.addAttacker(player);
		}
		new CaptureProcess(player, village);
	}

	@EventHandler
	public void onZoneExit(CaptureZoneExitEvent e){
		Village village = (Village) e.getObjective();
		player = e.getPlayer();
		PlayerWrapper wrapper = PlayerWrapper.getWrapper(player);
		
		village.removeCapturing(player);
		village.removeAttacker(player);
		village.removeDefender(player);
		if (village.getAttackers().size() < 1)
			village.stop();
		
		if (wrapper.isInKingdom())
			wrapper.getScoreboard().KingdomBoard(player);
		else
			wrapper.getScoreboard().NeutralBoard(player);
	}
}
