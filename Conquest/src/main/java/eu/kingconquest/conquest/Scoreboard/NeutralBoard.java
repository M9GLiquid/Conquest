package eu.kingconquest.conquest.Scoreboard;

import org.bukkit.entity.Player;

public class NeutralBoard extends Board{

	@SuppressWarnings("all")
	public NeutralBoard(Player player){
		setType(BoardType.NEUTRALBOARD);
		SimpleScoreboard board = new SimpleScoreboard();
		int i = 13;
		board.setTitle("&6-----{Prefix}&6-----");
		board.add(i--, "&6Welcome: &3" + player.getName());
		board.add(i--, "  ");	
		board.add(i--, "&6Guide/Help");	
		board.add(i--, " &6&l# &r&7Run command: &4/c");	
		board.add(i--, " &6&l# &r&7Interact with Menu");	
		board.add(i--, " &6&l# &r&7Join the Fun!");
		board.send(player); // Build Scoreboard then send it to player
	}
}
