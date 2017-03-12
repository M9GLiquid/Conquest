package eu.kingconquest.conquest.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.kingconquest.conquest.Scoreboard.KingdomBoard;
import eu.kingconquest.conquest.Scoreboard.NeutralBoard;
import eu.kingconquest.conquest.Scoreboard.PlayerBoard;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.database.YmlStorage;

public class PlayerJoinListener implements Listener{
	private static PlayerWrapper wrapper;

	/**
	 * Player Join Event (First Time + Every Time)
	 * @param e - event
	 * @return void
	 */
	@EventHandler 
	public void onPlayerJoin(PlayerJoinEvent e){
		Player player = e.getPlayer();
		YmlStorage.getWorlds().forEach(uniqueID->{
			
			if (player.getWorld().equals(Bukkit.getWorld(uniqueID))){
				wrapper = PlayerWrapper.getWrapper(player);
				if (!wrapper.isInKingdom(Bukkit.getWorld(uniqueID))){
					new NeutralBoard(player);
					return;
				}
				switch(wrapper.getBoardType()){
					case KINGDOMBOARD:
						new KingdomBoard(player);
					break;
					/*case TRAPBOARD:
							new TrapBoard(player);
					break;*/
					case PLAYERBOARD:
						new PlayerBoard(player);
					break;
					default:
						new KingdomBoard(player);
					break;
				}
			}
		});
	}
}
