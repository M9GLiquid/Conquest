package eu.kingconquest.conquest.core;

import eu.kingconquest.conquest.Scoreboard.*;
import eu.kingconquest.conquest.util.HierarchyRank;
import eu.kingconquest.conquest.util.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PlayerWrapper{

    private static HashMap<UUID, PlayerWrapper> wrapper = new HashMap<>();
    private ArrayList<UUID> friends = new ArrayList<>();
    private HashMap<UUID, LocalDateTime> cooldowns = new HashMap<>();

    private SimpleScoreboard scoreboard;
    private HierarchyRank hierarchy = HierarchyRank.DEFAULT;
    private BoardType boardType = BoardType.NEUTRALBOARD;

    private UUID kingdom;

	public PlayerWrapper(UUID uuid){
		wrapper.put(uuid, this);
	}

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
	public ArrayList<UUID> getFriends(){
		return friends;
	}

	public boolean isRewardReady(UUID reward){
		if (Validate.isNull(cooldowns.get(reward)))
			return true;
		
		LocalDateTime date = LocalDateTime.now();
		if (date.isAfter(cooldowns.get(reward)) 
				|| date.equals(cooldowns.get(reward))){
			cooldowns.remove(reward);
			return true;
		}else
			return false;
	}
	public void setRewardCooldown(Reward reward){
		LocalDateTime now = LocalDateTime.now();
		now = now.plusMinutes(reward.getCooldown());
		cooldowns.put(reward.getUUID(), now);
	}

    public boolean isRewardReady(Reward reward) {
        if (Validate.isNull(cooldowns.get(reward.getUUID())))
            return true;

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(cooldowns.get(reward.getUUID()))
                || now.equals(cooldowns.get(reward.getUUID()))) {
            cooldowns.remove(reward.getUUID());
            return true;
        } else
            return false;
    }

	public void setRewardCooldown(UUID uuid, Long cooldown){
		LocalDateTime date = LocalDateTime.now();
        cooldowns.put(uuid, date.plusMinutes(cooldown));
	}
	public Long getRewardCooldown(Reward reward){
		try{
			Duration duration = Duration.between(LocalDateTime.now(), cooldowns.get(reward.getUUID()));
			return duration.getSeconds();
		}catch(Exception e){
			return null;
		}
	}

	public void setKingdom(UUID uuid){
		this.kingdom = uuid;
	}

    public Kingdom getKingdom(ActiveWorld world) {
		return Kingdom.getKingdom(kingdom, world);
	}

    public HashMap<UUID, Long> getRewardCooldowns() {
        HashMap<UUID, Long> c = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        cooldowns.forEach((uuid, date) -> {
            Duration duration = Duration.between(date, now);
            c.put(uuid, duration.getSeconds() / 60); //Minutes
        });

        return c;
    }

	
	public Player getPlayer(UUID uuid){
		return Bukkit.getPlayer(uuid);
	}


    public BoardType getBoardType() {
		return boardType;
	}

    public boolean isInKingdom(ActiveWorld world) {
        return Validate.notNull(Kingdom.getKingdom(kingdom, world));
    }

    public void setBoardType(BoardType board) {
        this.boardType = board;
    }

    public void setBoardType(String board) {
        for (BoardType type : BoardType.values())
            if (type.getName().equals(board.toLowerCase()))
                this.boardType = type;
    }


    public void setScoreboard(SimpleScoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public SimpleScoreboard getScoreboard() {
        return scoreboard;
    }

    public void callBoardType(Player player) {
        switch (boardType){
            case KINGDOMBOARD:
                new KingdomBoard(player);
                return;
				/*case TRAPBOARD:
			return new TrapBoard(player);
			break;*/
            case PLAYERBOARD:
                new PlayerBoard(player);
                return;
            case NEUTRALBOARD:
            default:
                new NeutralBoard(player);
        }
    }
	public static PlayerWrapper getWrapper(Player player){
		if (Validate.notNull(wrapper.get(player.getUniqueId()))){
			return wrapper.get(player.getUniqueId());
		}
		return new PlayerWrapper(player.getUniqueId());
	}
	public static PlayerWrapper getWrapper(UUID uuid){
		if (Validate.notNull(wrapper.get(uuid))){
			return wrapper.get(uuid);
		}
		return new PlayerWrapper(uuid);
	}
	public static HashMap<UUID, PlayerWrapper> Wrappers(){
		return wrapper;
	}
	public static void clear(){
		wrapper.clear();
	}

    public HierarchyRank getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(HierarchyRank hierarchy) {
        this.hierarchy = hierarchy;
    }
}
