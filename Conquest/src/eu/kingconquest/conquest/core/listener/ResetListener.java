package eu.kingconquest.conquest.core.listener;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.Town;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.core.event.ServerResetEvent;
import eu.kingconquest.conquest.core.event.WorldResetEvent;
import eu.kingconquest.conquest.core.util.ChatManager;
import eu.kingconquest.conquest.core.util.Config;

public class ResetListener implements Listener{
	
	public void onServerReset(ServerResetEvent e){
		Player player = e.getPlayer();
		ChatManager.Chat(player, "{plugin_prefix} &aServer Reset Started!");
		ChatManager.Console("{plugin_prefix}&4 &aServer Reset Started!");
		
		Config.getWorlds().forEach(world->{
			if (e.getKingdomSave())
				Kingdom.clearKingdoms(world);
			if (e.getMemberSave())
				players(world);
			villages(world);
			towns(world);
		});
	}

	public void onWorldReset(WorldResetEvent e){
		Player player = e.getPlayer();
		World world = e.getWorld();
		ChatManager.Chat(player, "{plugin_prefix} " + world.getName() + " &aReset Started!");
		ChatManager.Console("{plugin_prefix}&4 Reset for World: " + world.getName() + " &aStarted!");
		
		if (e.getKingdomSave())
			Kingdom.clearKingdoms(world);
		if (e.getMemberSave())
			players(world);
		villages(world);
		towns(world);
	}

	/**
	 * Reset Players of Kingdoms of {world}
	 * @param world
	 */
	private void players(World world){
		Kingdom.getKingdoms(world).forEach(kingdom->{
			kingdom.clearMembers();
			kingdom.setKing(null);
		});
	}

	/**
	 * Reset Objectives of World
	 * @param world - World
	 * @return void
	 */
	private void towns(World world){
		Town.getTowns().stream().filter(town->town.getLocation().getWorld().equals(world)).forEach(town->{
				town.setNeutral();
		});
	}
	
	/**
	 * Reset Outposts of World
	 * @param world - World
	 * @return void
	 */
	private void villages(World world){
		for (Village village : Village.getVillages()){
			if (!village.getLocation().getWorld().equals(world))
				continue;
			village.setNeutral();
		}
	}
}
