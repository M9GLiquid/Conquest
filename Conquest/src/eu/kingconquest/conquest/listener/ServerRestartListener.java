package eu.kingconquest.conquest.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import eu.kingconquest.conquest.Scoreboard.KingdomBoard;
import eu.kingconquest.conquest.Scoreboard.NeutralBoard;
import eu.kingconquest.conquest.Scoreboard.PlayerBoard;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.database.YmlStorage;
import eu.kingconquest.conquest.event.ServerRestartEvent;
import eu.kingconquest.conquest.hook.Vault;
import eu.kingconquest.conquest.util.Validate;

public class ServerRestartListener implements Listener{

	@EventHandler
	public void onServerRestart(ServerRestartEvent event){
		Bukkit.getServer().getOnlinePlayers().forEach(player->{ 
			if (!Validate.hasPerm(player, "conquest.basic.*"))
				Vault.perms.playerAdd(player, "conquest.basic.*");
			PlayerWrapper wrapper = PlayerWrapper.getWrapper(player);
			YmlStorage.getWorlds().forEach(uniqueID->{
				if (player.getWorld().equals(Bukkit.getWorld(uniqueID))){
					
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
		});
	}
}
