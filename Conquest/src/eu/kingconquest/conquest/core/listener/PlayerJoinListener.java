package eu.kingconquest.conquest.core.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.core.util.SimpleScoreboard;
import eu.kingconquest.conquest.core.util.Validate;

public class PlayerJoinListener implements Listener{

	/**
	 * Player Join Event (First Time + Every Time)
	 * @param e - event
	 * @return void
	 */
	@EventHandler 
	public void onPlayerJoin(PlayerJoinEvent e){
		Player p = e.getPlayer();
		PlayerWrapper wrapper = new PlayerWrapper(p);
		SimpleScoreboard board = new SimpleScoreboard("&6===={plugin_prefix}&6====");
		wrapper.setScoreboard(board);
		if (!p.hasPlayedBefore() || Validate.isNull(wrapper.getKingdom())){
			board.neutralBoard(p);
		}else{
			board.kingdomBoard(p);
		}
	}
}
