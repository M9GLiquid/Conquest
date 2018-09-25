package eu.kingconquest.conquest.core;

import eu.kingconquest.conquest.MainClass;
import eu.kingconquest.conquest.util.ColorManager;
import eu.kingconquest.conquest.util.Validate;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class Rocket{
	private Firework firework;


	public Rocket(World world, int x, int y, int z, boolean flicker, boolean trail, int power, int lifetime, int color){
		if (lifetime < 1)
			return;
		Location location = new Location(world, x, y, z);
		firework = location.getWorld().spawn(location, Firework.class);
		FireworkMeta fwm = firework.getFireworkMeta();
		fwm.addEffect(FireworkEffect.builder()
                .flicker(flicker)
                .trail(trail)
                .with(FireworkEffect.Type.STAR)
                .with(FireworkEffect.Type.BALL_LARGE)
                .with(FireworkEffect.Type.BURST)
                .withColor(ColorManager.int2Color(color))
                .build());
		fwm.setPower(power);
		firework.setFireworkMeta(fwm);
		spawn();
	}
	public Rocket(Location location, boolean flicker, boolean trail, int power, int lifetime, int color){
		if (lifetime < 1)
			return;
		firework = location.getWorld().spawn(location, Firework.class);
		FireworkMeta fwm = firework.getFireworkMeta();
		fwm.addEffect(FireworkEffect.builder()
                .flicker(flicker)
                .trail(trail)
                .with(FireworkEffect.Type.STAR)
                .with(FireworkEffect.Type.BALL_LARGE)
                .with(FireworkEffect.Type.BURST)
                .withColor(ColorManager.int2Color(color))
                .build());
		fwm.setPower(power);
		firework.setFireworkMeta(fwm);
		spawn();
	}
	public void detonate(){
		if(Validate.notNull(firework)){
			firework.detonate();
		}
	}

	public void spawn(){
		int lifetime = 0;
		Bukkit.getScheduler().runTaskLater(MainClass.getInstance(), () -> detonate(), lifetime);
	}
}
