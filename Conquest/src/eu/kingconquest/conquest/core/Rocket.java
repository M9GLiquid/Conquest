package eu.kingconquest.conquest.core;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import eu.kingconquest.conquest.Main;
import eu.kingconquest.conquest.util.ColorManager;
import eu.kingconquest.conquest.util.Validate;

public class Rocket{
	private FireworkEffect.Builder builder = FireworkEffect.builder();
	private Location location;
	private FireworkMeta fwm;
	private Firework firework;
	private int lifetime = 0;
	private int power = 0;


	public Rocket(Location location, boolean flicker, boolean trail, int power, int lifetime, int color){
		if (lifetime < 1)
			return;
		this.location = location;
		setFlicker(flicker);
		setTrail(trail);
		this.lifetime = lifetime;
		this.power = power;
		setColor(ColorManager.int2Color(color));
		spawn();
	}

	public void setColor(Color color){
		builder.withColor(color);
	}

	public void setFlicker(boolean flicker){
		builder.flicker(flicker);
	}

	public void setTrail(boolean trail){
		builder.trail(trail);
	}

	public void detonate(){
		if(Validate.notNull(firework)){
			firework.detonate();
		}
	}

	public void spawn(){
		firework = (Firework)location.getWorld().spawnEntity(location, EntityType.FIREWORK);
		fwm = (FireworkMeta)firework.getFireworkMeta();
		
		fwm.addEffect(builder.build());
		fwm.setPower(power);
		firework.setFireworkMeta(fwm);

		Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable(){

			@Override
			public void run(){
				detonate();    
			}
		}, lifetime);
	}
}
