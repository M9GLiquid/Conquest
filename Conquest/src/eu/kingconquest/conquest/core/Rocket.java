package eu.kingconquest.conquest.core;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import eu.kingconquest.conquest.Main;
import eu.kingconquest.conquest.util.ColorManager;
import eu.kingconquest.conquest.util.Validate;

public class Rocket{
	private Firework firework;
	private int lifetime = 0;


	public Rocket(World world, int x, int y, int z, boolean flicker, boolean trail, int power, int lifetime, int color){
		if (lifetime < 1)
			return;
		Location location = new Location(world, x, y, z);
		firework = location.getWorld().spawn(location, Firework.class);
		FireworkMeta fwm = (FireworkMeta) firework.getFireworkMeta();
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
		FireworkMeta fwm = (FireworkMeta) firework.getFireworkMeta();
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
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable(){

			@Override
			public void run(){
				detonate();    
			}
		}, lifetime);
	}
}
