package eu.kingconquest.conquest.Scoreboard;

import org.bukkit.Bukkit;

import eu.kingconquest.conquest.core.Village;

public class CaptureBoard extends Board{
	public CaptureBoard(Village village){
		setType(BoardType.CAPTUREBOARD);
		SimpleScoreboard board = new SimpleScoreboard();

		int i = 13;
		board.setTitle("&6[&eCapture Information&6]");
		board.add(i--, "&a&lName: ");
		board.add(i--, village.getOwner().getColor() + village.getName());
		board.add(i--, "   ");
		board.add(i--, "&a&lOwner:");
		board.add(i--, village.getOwner().getColor() + village.getOwner().getName());
		board.add(i--, "&a&lParent:");
		board.add(i--, "&e" + (village.hasParent() ? village.getParent().getName() : "&fNone"));
		board.add(i--, "  ");
		board.add(i--, "&a&lCapture Progress:");
		board.add(i--, "&e" + village.getProgress() + "%");
		//Send new Captureboard to all Attacking
		village.getAttackers().forEach((playerID, kingdomID) ->{
			board.send(Bukkit.getPlayer(playerID));
		});
		//Send new Captureboard to all Defending
		village.getDefenders().forEach((playerID, kingdomID) ->{
			board.send(Bukkit.getPlayer(playerID));
		});
	}
}
