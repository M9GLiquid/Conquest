package eu.kingconquest.conquest.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.database.Config;
import eu.kingconquest.conquest.hook.TNEApi;
import eu.kingconquest.conquest.hook.Vault;
import eu.kingconquest.conquest.util.SimpleScoreboard;
import eu.kingconquest.conquest.util.Validate;

public class PlayerJoinListener implements Listener{
	private SimpleScoreboard board = null;

	/**
	 * Player Join Event (First Time + Every Time)
	 * @param e - event
	 * @return void
	 */
	@EventHandler 
	public void onPlayerJoin(PlayerJoinEvent e){
		Player player = e.getPlayer();
		if (!Validate.hasPerm(player, "conquest.basic.*"))
			Vault.perms.playerAdd(player, "conquest.basic.*");
		if (!TNEApi.accountExist(player.getUniqueId()))
			TNEApi.createAccount(player.getUniqueId());
		
		Config.getWorlds().forEach(uniqueID->{
			if (player.getWorld().equals(Bukkit.getWorld(uniqueID))){
				PlayerWrapper wrapper = PlayerWrapper.getWrapper(player);
				if (Validate.isNull(wrapper.getScoreboard())){
					board = new SimpleScoreboard("&6------{plugin_prefix}&6------");
					wrapper.setScoreboard(board);
				}else{
					board = wrapper.getScoreboard();
				}
				if (wrapper.isInKingdom(player.getWorld())){
					board.KingdomBoard(player);
				}/*else if (player.hasPlayedBefore()){
					board.PlayerBoard(player);
				}*/else{
					board.NeutralBoard(player);
				}
			}
		});
	}
}
