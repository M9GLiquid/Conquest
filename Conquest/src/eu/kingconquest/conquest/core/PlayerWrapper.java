package eu.kingconquest.conquest.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import eu.kingconquest.conquest.util.SimpleScoreboard;
import eu.kingconquest.conquest.util.Validate;

public class PlayerWrapper{
	private static HashMap<UUID, PlayerWrapper> map = new HashMap<UUID, PlayerWrapper>();

	public PlayerWrapper(Player player){
		map.put(player.getUniqueId(), this);
	}

	private ArrayList<UUID> friends = new ArrayList<UUID>();
	public void addFriend(Player friend){
		friends.add(friend.getUniqueId());
	}
	public void removeFriend(Player friend){
		friends.remove(friend.getUniqueId());
	}
	public void removeFriends(ArrayList<UUID> friends){
		this.friends.removeAll(friends);
	}
	public void addFriends(ArrayList<UUID> friends){
		this.friends.addAll(friends);
	}
	
	private Kingdom kingdom;
	public void setKingdom(Kingdom kingdom){
		this.kingdom= kingdom;
	}
	public Kingdom getKingdom(){
		return kingdom;
	}
	public boolean isInKingdom(){
		if (Validate.isNull(kingdom))
			return false;
		return true;
	}
	
	private SimpleScoreboard scoreboard;
	public void setScoreboard(SimpleScoreboard scoreboard){
		this.scoreboard= scoreboard;
	}
	public SimpleScoreboard getScoreboard(){
		return scoreboard;
	}

	public static PlayerWrapper getWrapper(Player player){
		if (!Validate.isNull(map.get(player.getUniqueId()))){
			return map.get(player.getUniqueId());
		}
		return new PlayerWrapper(player);
	}
}
