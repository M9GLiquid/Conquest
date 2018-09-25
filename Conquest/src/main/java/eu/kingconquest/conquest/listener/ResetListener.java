package eu.kingconquest.conquest.listener;

import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.Town;
import eu.kingconquest.conquest.core.Village;
import eu.kingconquest.conquest.database.YmlStorage;
import eu.kingconquest.conquest.event.ServerResetEvent;
import eu.kingconquest.conquest.event.WorldResetEvent;
import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class ResetListener implements Listener{
	
	public void onServerReset(ServerResetEvent e){
		Player player = e.getPlayer();
		new Message(player, MessageType.CHAT, "{Prefix} &aServer Reset Started!");
		new Message(null, MessageType.CONSOLE, "{Prefix} &aServer Reset Started!");
		
		YmlStorage.getWorlds().forEach(uniqueID->{
			if (e.getKingdomSave())
				Kingdom.removeKingdoms(Kingdom.getKingdoms());
			if (e.getMemberSave())
				players(Bukkit.getWorld(uniqueID));
			villages(Bukkit.getWorld(uniqueID));
			towns(Bukkit.getWorld(uniqueID));
		});
	}

	public void onWorldReset(WorldResetEvent e){
		Player player = e.getPlayer();
		World world = e.getWorld();
		new Message(player, MessageType.CHAT, "{Prefix} " + world.getName() + " &aReset Started!");
		new Message(null, MessageType.CONSOLE, "{Prefix} &aReset for World: " + world.getName() + " Started!");
		
		if (e.getKingdomSave())
			Kingdom.removeKingdoms(Kingdom.getKingdoms(world));
		if (e.getMemberSave())
			players(world);
		villages(world);
		towns(world);
	}

	/**
	 * Reset Players of Kingdoms of {world}
	 * @param world World
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
		Town.getTowns().stream().filter(town -> town.getLocation().getWorld().equals(world)).forEach(Town::setNeutral);
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
