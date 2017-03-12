package eu.kingconquest.conquest.listener;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import eu.kingconquest.conquest.Scoreboard.KingdomBoard;
import eu.kingconquest.conquest.Scoreboard.NeutralBoard;
import eu.kingconquest.conquest.Scoreboard.PlayerBoard;
import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.database.YmlStorage;
import eu.kingconquest.conquest.util.Validate;

public class ServerRestartListener{ // Not a real Listener since it can't listend for a server restart :(
	private static PlayerWrapper wrapper;

	public static void onServerRestart(Collection<? extends Player> collection){
		createNeutralKingdom();
		collection.forEach(player->{ 
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
		});
	}

	private static void createNeutralKingdom(){
		YmlStorage.getWorlds().forEach(uniqueID->{
			if (Validate.isNull(Kingdom.getKingdom("Neutral", Bukkit.getWorld(uniqueID))))
				new Kingdom(
						"Neutral", 
						null, 
						Bukkit.getWorld(uniqueID).getSpawnLocation(), 
						-1);
		});
	}
}
