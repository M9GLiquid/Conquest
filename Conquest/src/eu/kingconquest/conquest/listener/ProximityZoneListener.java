package eu.kingconquest.conquest.listener;

import org.bukkit.Bukkit;
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
import eu.kingconquest.conquest.util.Validate;

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
			village.removeAttacker(player);
			village.addDefender(player);
		}else{
			village.addAttacker(player);
		}
		new CaptureProcess(player, village);
	}

	@EventHandler
	public void onZoneExit(CaptureZoneExitEvent e){
		Village village = (Village) e.getObjective();
		if (Validate.isNull(e.getPlayer())){
			village.getAttackers().forEach((uuid, kuuid)->{
				if (kuuid.equals(village.getOwner().getUUID()))
					village.addDefender(Bukkit.getPlayer(uuid)); // add defender of the winning kingdom
			});
			village.clearAttackers();
		}else{
			player = e.getPlayer();

			village.removeAttacker(player);
			village.removeDefender(player);
		}
		new PlayerBoard(player);
		
		if (village.getAttackers().size() < 1 
				&& village.getDefenders().size() < 1)
			village.stop();
	}
}
