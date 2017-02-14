package eu.kingconquest.conquest.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import eu.kingconquest.conquest.Scoreboard.Board;
import eu.kingconquest.conquest.Scoreboard.SimpleScoreboard;
import eu.kingconquest.conquest.util.Validate;

public class PlayerWrapper{
	private static HashMap<UUID, PlayerWrapper> wrapper = new HashMap<UUID, PlayerWrapper>();
	private Board boardType;

	public PlayerWrapper(UUID uuid){
		wrapper.put(uuid, this);
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
	public int getOnlineFriends(){
		int i = 0;
		for (Player player : Bukkit.getOnlinePlayers())
			if (friends.contains(player.getUniqueId()))
				i++;
		return i;
	}
	public int getNumberOfFriends(){
		return friends.size();
	}
	
	private UUID kingdom;
	public void setKingdom(UUID uuid){
		this.kingdom = uuid;
	}
	public Kingdom getKingdom(World world){
		return Kingdom.getKingdom(kingdom, world);
	}
	public boolean isInKingdom(World world){
		if (Validate.notNull(Kingdom.getKingdom(kingdom, world)))
			return true;
		return false;
	}
	
	public Player getPlayer(UUID uuid){
		return Bukkit.getPlayer(uuid);
	}
	
	public Board getBoardType(){
		return boardType;
	}
	public void setBoardType(Board board){
		this.boardType = board;
	}
	
	private SimpleScoreboard scoreboard;
	public void setScoreboard(SimpleScoreboard scoreboard){
		this.scoreboard= scoreboard;
	}
	public SimpleScoreboard getScoreboard(){
		return scoreboard;
	}

	public static PlayerWrapper getWrapper(Player player){
		if (Validate.notNull(wrapper.get(player.getUniqueId()))){
			return wrapper.get(player.getUniqueId());
		}
		return new PlayerWrapper(player.getUniqueId());
	}
}
