package eu.kingconquest.conquest.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.event.NeutralCaptureTrapEvent;
import eu.kingconquest.conquest.util.Validate;
import net.minecraft.server.v1_11_R1.Material;

public class TrapListener implements Listener{

	@EventHandler
	public void onZombieSpawnTrap(NeutralCaptureTrapEvent e){
		Location location = e.getLocation();
		Bukkit.getOnlinePlayers().stream()
		.filter(player->Validate.isWithinArea(location, player.getLocation(), e.getTrapDistance(), 20.0d, 10.0d))// If Player within area
			.filter(player->PlayerWrapper.getWrapper(player).isInKingdom(player.getWorld()))
				.filter(player->Validate.notNull(Kingdom.getKingdom(e.getOwner(), e.getWorld())))
					.filter(player ->!PlayerWrapper.getWrapper(player).getKingdom(player.getWorld()).equals(Kingdom.getKingdom(e.getOwner(), e.getWorld())))// If player don't belong to the trap's owner
						.forEach(player->{
							spawnDefensiveEntity(EntityType.ZOMBIE, location, e.getTrapDistance(), 2);
							spawnDefensiveEntity(EntityType.SKELETON, location, e.getTrapDistance(), 1);
		});
	}

	private static void spawnDefensiveEntity(EntityType entity, Location location, double radius, int rounds){
		int i = 0, tries = 0;
		Location loc1 = location.clone();
		Location loc2 = location.clone();
		while(true){
			double x = Math.cos(Math.random()*Math.PI*2)*radius;
			double z = Math.sin(Math.random()*Math.PI*2)*radius;
			loc1.add(x, 1, z);
			loc2.add(x, 2, z);
			if ((!loc1.getBlock().getType().isSolid() ||  loc1.getBlock().getType().equals(Material.AIR)) 
					&& !loc2.getBlock().getType().isSolid() || loc2.getBlock().getType().equals(Material.AIR)){
				location.getWorld().spawnEntity(loc2, entity);
				if (i >= rounds) // If spawned enteties is more or equal to rounds
					break;
				i++;
			}
			if (tries >= 100) // For Safety
				break;
			tries++;
			loc1 = location.clone();
			loc2 = location.clone();
		}
	}
	@SuppressWarnings("unused")
	private static void spawnDefensiveEntity(LivingEntity entity, Location location, double radius, int rounds){
		int i = 0, tries = 0;
		Location loc1 = location.clone();
		Location loc2 = location.clone();
		while(true){
			double x = Math.cos(Math.random()*Math.PI*2)*radius;
			double z = Math.sin(Math.random()*Math.PI*2)*radius;
			loc1.add(x, 1, z);
			loc2.add(x, 2, z);
			if ((!loc1.getBlock().getType().isSolid() ||  loc1.getBlock().getType().equals(Material.AIR)) 
						&& !loc2.getBlock().getType().isSolid() || loc2.getBlock().getType().equals(Material.AIR)){
				location.getWorld().spawn(loc2, entity.getClass());
				if (i >= rounds) // If spawned enteties is more or equal to rounds
					break;
				i++;
			}
			if (tries >= 100) // For Safety
				break;
			tries++;
			loc1 = location.clone();
			loc2 = location.clone();
		}
	}
}
