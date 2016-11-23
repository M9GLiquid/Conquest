package eu.kingconquest.conquest.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.event.ServerRestartEvent;
import eu.kingconquest.conquest.util.Config;
import eu.kingconquest.conquest.util.SimpleScoreboard;
import eu.kingconquest.conquest.util.Validate;

public class ServerRestartListener implements Listener{
	
	@EventHandler
	public void onServerRestart(ServerRestartEvent event){
		if (Bukkit.getServer().getOnlinePlayers().size() > 0){
			Bukkit.getServer().getOnlinePlayers().forEach(player->{ 
				Config.getWorlds().forEach(world->{
					if (player.getWorld().equals(world)){
						PlayerWrapper wrapper = PlayerWrapper.getWrapper(player);
						SimpleScoreboard score = new SimpleScoreboard("&6------{plugin_prefix}&6------");
						wrapper.setScoreboard(score);
						if (wrapper.isInKingdom() && !Validate.isNull(wrapper.getKingdom())){
							score.kingdomBoard(player);
							return;
						}
						score.neutralBoard(player);
					}
				});
			});
		}
	}
}
