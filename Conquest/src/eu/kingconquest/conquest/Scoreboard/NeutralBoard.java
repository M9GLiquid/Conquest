package eu.kingconquest.conquest.Scoreboard;

import org.bukkit.entity.Player;

public class NeutralBoard extends Board{
	
	public NeutralBoard(Player player){
		setName("Neutral");
		SimpleScoreboard board = getBoard(player);
		int i = 13;
		board.setTitle("&6-----{Prefix}&6-----");
		board.add(i--, "&6Welcome: &3" + player.getName());
		board.add(i--, "  ");	
		board.add(i--, "&6&lGuide/Help");	
		board.add(i--, " &6&l# &r&7Run command: &4/kc");	
		board.add(i--, " &6&l# &r&7Interact with Menu");	
		board.add(i--, " &6&l# &r&7Join the Fun!");
		board.add(i--, " ");	
		board.add(i--, " ");	
		board.add(i--, " ");	
		board.add(i--, " ");	
		board.add(i--, " ");	
		board.add(i--, " ");	
		board.send(player); // Build Scoreboard then send it to player
	}
}
