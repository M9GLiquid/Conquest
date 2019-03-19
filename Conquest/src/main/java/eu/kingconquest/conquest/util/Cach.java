package eu.kingconquest.conquest.util;

import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.Reward;
import eu.kingconquest.conquest.core.Town;
import eu.kingconquest.conquest.core.Village;
import org.bukkit.entity.Player;

public class Cach{
	public static Kingdom StaticKingdom = null;
	public static Reward StaticReward = null;
	public static Village StaticVillage = null;
	public static Player StaticPlayer = null;
	public static Integer[] StaticCooldownLeft;
	public static Town StaticTown = null;
	public static Long tpDelay;
	
	public static void nullify(){
		StaticPlayer = null;
		StaticReward = null;
		StaticVillage = null;
		StaticTown = null;
		StaticKingdom = null;
	}
}
