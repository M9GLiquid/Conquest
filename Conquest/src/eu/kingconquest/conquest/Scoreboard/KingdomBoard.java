package eu.kingconquest.conquest.Scoreboard;

import org.bukkit.entity.Player;

import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.Town;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.hook.EconAPI;
import eu.kingconquest.conquest.util.Validate;

public class KingdomBoard extends Board{
	
	public KingdomBoard(Player player){
		setType(BoardType.KINGDOMBOARD);
		SimpleScoreboard board = new SimpleScoreboard();
		
		int x = 0;
		Kingdom kingdom = getWrapper(player).getKingdom(player.getWorld());
		for (Village village : Village.getVillages(player.getWorld())){
			if (village.getOwner().equals(kingdom))
				if (!village.hasParent())
					x++;
		}
		for (Town town : Town.getTowns(player.getWorld())){
			if (town.getOwner().equals(kingdom))
				x++;
		}
		int i = 13;
		board.setTitle("&6[&cKingdom &9Information&6]");
		board.add(i--, "&c&lKingdom: " + kingdom.getColor() + kingdom.getName());
		board.add(i--, "&c&lKing: &7" + (Validate.notNull(kingdom.getKing()) ? kingdom.getKing().getDisplayName() : "None"));	
		board.add(i--, "&c&lBalance: &r&6"+ EconAPI.getBalance(kingdom.getUUID()) + "$");		
		board.add(i--, "  ");	
		board.add(i--, "&6&lKingdom Specific");	
		board.add(i--, "&c&lPoints Captured:");	
		board.add(i--, "&7" + x);	
		board.add(i--, "&c&lTraps Deployed:");	
		board.add(i--, "&7*Coming Soon*  ");
		board.send(player); // Build Scoreboard then send it to player
	}
}
