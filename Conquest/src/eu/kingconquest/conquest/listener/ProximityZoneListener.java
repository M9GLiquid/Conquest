package eu.kingconquest.conquest.listener;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import eu.kingconquest.conquest.Scoreboard.PlayerBoard;
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
		if (!player.getGameMode().equals(GameMode.SURVIVAL))
			return;
		wrapper = PlayerWrapper.getWrapper(player);
		// If Player already is Capturing
		if (village.isCapturing(player))
			return;
		
		// If Defenders  Kingdom is owner of Objective
		if (village.getOwner().equals(wrapper.getKingdom(player.getWorld()))){
			village.addCapturing(player);
			village.removeAttacker(player);
			village.addDefender(player);
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

		village.removeCapturing(player);
		village.removeAttacker(player);
		village.removeDefender(player);
		new PlayerBoard(player);
		
		if (village.getAttackers().size() < 1 
				&& village.getDefenders().size() < 1)
			village.stop();
	}
}
