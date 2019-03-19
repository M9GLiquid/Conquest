package eu.kingconquest.conquest.Scoreboard;

import eu.kingconquest.conquest.core.ActiveWorld;
import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.hook.Vault;
import org.bukkit.entity.Player;

public class PlayerBoard extends Board{

	@SuppressWarnings("all")
	public PlayerBoard(Player player){
		setType(BoardType.PLAYERBOARD);
		SimpleScoreboard board = new SimpleScoreboard();
		PlayerWrapper wrapper = getWrapper(player);
        ActiveWorld activeWorld = ActiveWorld.getActiveWorld(player.getWorld());
        Kingdom kingdom =
                (wrapper.isInKingdom(activeWorld)
                        ? wrapper.getKingdom(activeWorld)
                        : Kingdom.getNeutral(activeWorld));

		int i = 13;
		board.setTitle("&6[&ePlayer Information&6]");
		board.add(i--, "&c&lKingdom: &r&7" + kingdom.getColor() + kingdom.getName());
		board.add(i--, "&c&lBalance: &6" + Vault.econ.getBalance(player) + "$");	
		board.add(i--, "  ");	
		board.add(i--, "&c&lFriends: &3" + wrapper.getOnlineFriends() + "/" + wrapper.getNumberOfFriends() + " &aOnline");
		board.add(i--, "&c&lTraps Deployed:");	
		board.add(i--, "&7*Coming Soon*  ");
		board.add(i--, " ");	
		board.send(player); // Build Scoreboard then send it to player
	}
}
