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
import eu.kingconquest.conquest.hook.TNEApi;
import eu.kingconquest.conquest.hook.Vault;
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

		YmlStorage.getWorlds().forEach(uniqueID->{
			if (player.getWorld().equals(Bukkit.getWorld(uniqueID))){
				if (!Validate.hasPerm(player, "conquest.basic.*"))
					Vault.perms.playerAdd(player, "conquest.basic.*");
				PlayerWrapper wrapper = PlayerWrapper.getWrapper(player);
				if (!TNEApi.accountExist(player.getUniqueId()))
					TNEApi.createAccount(player.getUniqueId());
				if (wrapper.isInKingdom(player.getWorld())){
					if(wrapper.getBoardType() instanceof KingdomBoard
							|| Validate.isNull(wrapper.getBoardType()))
						new KingdomBoard(player);
					if(wrapper.getBoardType() instanceof PlayerBoard)
						new PlayerBoard(player);
				}else{
					new NeutralBoard(player);
				}
			}
		});
	}
}
