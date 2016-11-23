package eu.kingconquest.conquest.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.util.Config;
import eu.kingconquest.conquest.util.SimpleScoreboard;
import eu.kingconquest.conquest.util.Validate;

public class PlayerJoinListener implements Listener{

	/**
	 * Player Join Event (First Time + Every Time)
	 * @param e - event
	 * @return void
	 */
	@EventHandler 
	public void onPlayerJoin(PlayerJoinEvent e){
		Player player = e.getPlayer();
		Config.getWorlds().forEach(world->{
			if (player.getWorld().equals(world)){
				PlayerWrapper wrapper = new PlayerWrapper(player);
				SimpleScoreboard board = new SimpleScoreboard("&6===={plugin_prefix}&6====");
				wrapper.setScoreboard(board);
				if (!player.hasPlayedBefore() || Validate.isNull(wrapper.getKingdom())){
					board.neutralBoard(player);
				}else{
					board.kingdomBoard(player);
				}
			}
		});
	}
}
