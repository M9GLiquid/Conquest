package eu.kingconquest.conquest.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.database.Config;
import eu.kingconquest.conquest.hook.TNEApi;
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
		if (!TNEApi.accountExist(player.getUniqueId()))
			TNEApi.createAccount(player.getUniqueId());
		Config.getWorlds().forEach(uniqueID->{
			if (player.getWorld().equals(Bukkit.getWorld(uniqueID))){
				PlayerWrapper wrapper = new PlayerWrapper(player);
				SimpleScoreboard board = new SimpleScoreboard("&6===={plugin_prefix}&6====");
				wrapper.setScoreboard(board);
				if (!player.hasPlayedBefore() || Validate.isNull(wrapper.getKingdom())){
					board.NeutralBoard(player);
				}else{
					board.KingdomBoard(player);
				}
			}
		});
	}
}
