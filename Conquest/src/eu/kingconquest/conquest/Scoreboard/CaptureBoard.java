package eu.kingconquest.conquest.Scoreboard;

import org.bukkit.entity.Player;

import eu.kingconquest.conquest.core.Village;

public class CaptureBoard extends Board{

	public CaptureBoard(Player player, Village village){
		setName("Capture");
		SimpleScoreboard board = getBoard(player);
		
		int i = 13;
		board.setTitle("&6[&eCapture Information&6]");
		board.add(i--, "&a&lName: ");	
		board.add(i--, village.getOwner().getColorSymbol() + village.getName());	
		board.add(i--, "   ");	
		board.add(i--, "&a&lOwner:");
		board.add(i--, village.getOwner().getColorSymbol() + village.getOwner().getName());	
		board.add(i--, "&a&lParent:");	
		board.add(i--, "&e" + (village.hasParent() ? village.getParent().getName() : "&fNone"));	
		board.add(i--, "  ");	
		board.add(i--, "&a&lCapture Progress:");	
		board.add(i--, "&e" + village.getProgress() + "%");	
		board.add(i--, " ");		
		board.add(i--, " ");
		board.add(i--, " ");
		board.send(player);
	}
}
