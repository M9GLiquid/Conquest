package eu.kingconquest.conquest.Scoreboard;

import org.bukkit.entity.Player;

import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.util.Validate;

public class Board{
	private String name = "";
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public SimpleScoreboard getBoard(Player player){
		PlayerWrapper wrapper = getWrapper(player);
		if (Validate.isNull(wrapper.getScoreboard())){
			wrapper.setScoreboard(new SimpleScoreboard());
			wrapper.setBoardType(new NeutralBoard(player));
		}
		return wrapper.getScoreboard();
	}
	
	public PlayerWrapper getWrapper(Player player){
		return PlayerWrapper.getWrapper(player);
	}
}
