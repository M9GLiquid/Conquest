package eu.kingconquest.conquest.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.database.Config;
import eu.kingconquest.conquest.event.ServerRestartEvent;
import eu.kingconquest.conquest.util.SimpleScoreboard;
import eu.kingconquest.conquest.util.Validate;

public class ServerRestartListener implements Listener{
	private SimpleScoreboard score = null;

	@EventHandler
	public void onServerRestart(ServerRestartEvent event){
		Bukkit.getServer().getOnlinePlayers().forEach(player->{ 
			Config.getWorlds().forEach(uniqueID->{

				if (player.getWorld().equals(Bukkit.getWorld(uniqueID))){
					PlayerWrapper wrapper = PlayerWrapper.getWrapper(player);

					if (Validate.isNull(wrapper.getScoreboard())){
						score = new SimpleScoreboard("&6------{plugin_prefix}&6------");
						wrapper.setScoreboard(score);
					}else
						score = wrapper.getScoreboard();

					if (wrapper.isInKingdom())
						score.KingdomBoard(player);
					else
						score.NeutralBoard(player);
				}
			});
		});
	}
}
