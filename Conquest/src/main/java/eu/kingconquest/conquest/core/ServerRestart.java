package eu.kingconquest.conquest.core;

import eu.kingconquest.conquest.Scoreboard.KingdomBoard;
import eu.kingconquest.conquest.Scoreboard.NeutralBoard;
import eu.kingconquest.conquest.Scoreboard.PlayerBoard;
import eu.kingconquest.conquest.database.core.YmlStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ServerRestart {
	private static PlayerWrapper wrapper;

    public ServerRestart(Collection<? extends Player> onlinePlayers) {
        onlinePlayers.forEach(player ->
                YmlStorage.getWorlds().forEach(uniqueID -> {
                    if (player.getWorld().equals(Bukkit.getWorld(uniqueID))) {

                        wrapper = PlayerWrapper.getWrapper(player);
                        if (!wrapper.isInKingdom(ActiveWorld.getActiveWorld(Bukkit.getWorld(uniqueID)))) {
                            new NeutralBoard(player);
                            return;
                        }
                        switch (wrapper.getBoardType()) {
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
                }));
	}
}
