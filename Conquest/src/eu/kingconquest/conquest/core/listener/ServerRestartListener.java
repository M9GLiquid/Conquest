package eu.kingconquest.conquest.core.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.core.event.ServerRestartEvent;
import eu.kingconquest.conquest.core.util.SimpleScoreboard;
import eu.kingconquest.conquest.core.util.Validate;

public class ServerRestartListener implements Listener{
	
	@EventHandler
	public void onServerRestart(ServerRestartEvent event){
		if (Bukkit.getServer().getOnlinePlayers().size() > 0){
			Bukkit.getServer().getOnlinePlayers().forEach(player->{ 
				try{
						PlayerWrapper wrapper = PlayerWrapper.getWrapper(player);
						SimpleScoreboard score = new SimpleScoreboard("&6------{plugin_prefix}&6------");
						wrapper.setScoreboard(score);
						if (wrapper.isInKingdom() && !Validate.isNull(wrapper.getKingdom())){
							score.kingdomBoard(player);
							return;
						}
						score.neutralBoard(player);
				}catch(Exception e){
					e.printStackTrace();
				}
			});
		}
	}
}
