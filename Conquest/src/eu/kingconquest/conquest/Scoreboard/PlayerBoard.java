package eu.kingconquest.conquest.Scoreboard;

import org.bukkit.entity.Player;

import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.hook.Vault;

public class PlayerBoard extends Board{
	
	
	public PlayerBoard(Player player){
		setName("Player");
		SimpleScoreboard board = getBoard(player);
		PlayerWrapper wrapper = getWrapper(player);
		Kingdom kingdom = 
				(wrapper.isInKingdom(player.getWorld()) 
				? wrapper.getKingdom(player.getWorld()) 
						: Kingdom.getNeutral(player.getWorld()));

		int i = 13;
		board.setTitle("&6[&ePlayer Information&6]");
		board.add(i--, "&c&lKingdom: &r&7" + kingdom.getColorSymbol() + kingdom.getName());
		board.add(i--, "&c&lBalance: &6" + Vault.econ.getBalance(player) + "$");	
		board.add(i--, "&7*Coming Soon*");	
		board.add(i--, "&c&lFriends: &3" + wrapper.getOnlineFriends() + "/" + wrapper.getNumberOfFriends() + " &aOnline");
		board.add(i--, " ");	
		board.add(i--, " ");	
		board.add(i--, " ");	
		board.add(i--, " ");	
		board.add(i--, " ");	
		board.add(i--, " ");	
		board.add(i--, " ");	
		board.add(i--, " ");	
		board.send(player); // Build Scoreboard then send it to player
	}
}
