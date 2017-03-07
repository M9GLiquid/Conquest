package eu.kingconquest.conquest.listener;

import org.bukkit.Bukkit;

import eu.kingconquest.conquest.Scoreboard.KingdomBoard;
import eu.kingconquest.conquest.Scoreboard.NeutralBoard;
import eu.kingconquest.conquest.Scoreboard.PlayerBoard;
import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.database.YmlStorage;
import eu.kingconquest.conquest.hook.Vault;
import eu.kingconquest.conquest.util.Validate;

public class ServerRestartListener{

	public static void onServerRestart(){
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

	public static void createNeutralKingdom(){
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
