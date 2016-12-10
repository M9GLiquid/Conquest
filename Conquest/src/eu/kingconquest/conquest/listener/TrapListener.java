package eu.kingconquest.conquest.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.event.NeutralCaptureTrapEvent;
import eu.kingconquest.conquest.util.Validate;

public class TrapListener implements Listener{

	@EventHandler
	public void onZombieSpawnTrap(NeutralCaptureTrapEvent e){
		Location location = e.getLocation();
		Bukkit.getOnlinePlayers().stream()
		.filter(player->Validate.isWithinArea(location, player.getLocation(), e.getTrapDistance(), 20, 10))// If Player within area
		.filter(player->PlayerWrapper.getWrapper(player).isInKingdom(player.getWorld()))
		.filter(player->Validate.notNull(Kingdom.getKingdom(e.getOwner(), e.getWorld())))
		.filter(player ->!PlayerWrapper.getWrapper(player).getKingdom(player.getWorld()).equals(Kingdom.getKingdom(e.getOwner(), e.getWorld())))// If player don't belong to the trapp's owner
		.forEach(player->{
			location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
			location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
			location.getWorld().spawnEntity(location, EntityType.SKELETON);
		});
	}
}
