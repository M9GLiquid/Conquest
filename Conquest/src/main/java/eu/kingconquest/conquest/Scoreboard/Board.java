package eu.kingconquest.conquest.Scoreboard;

import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.util.Validate;
import org.bukkit.entity.Player;

public class Board{
	
	public String getName(){
		return type.getName();
	}
	
	private BoardType type;
	public void setType(BoardType type){
		this.type = type;
	}
	public BoardType getType(){
		return type;
	}
	
	public SimpleScoreboard getBoard(Player player){
		PlayerWrapper wrapper = getWrapper(player);
		if (Validate.isNull(wrapper.getScoreboard())){
			wrapper.setScoreboard(new SimpleScoreboard());
			wrapper.setBoardType(BoardType.NEUTRALBOARD);
		}
		return wrapper.getScoreboard();
	}
	
	public PlayerWrapper getWrapper(Player player){
		return PlayerWrapper.getWrapper(player);
	}
}
