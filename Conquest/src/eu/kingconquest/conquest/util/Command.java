package eu.kingconquest.conquest.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import eu.kingconquest.conquest.Main;


public class Command {

	public Command(CommandType type, String command){
		this(null, type, command);
	}
	public Command(Player target, CommandType type, String command){
		switch(type){
		case PLAYERCMD:
			if (Validate.notNull(target))
				target.performCommand(Message.getMessage(command));
			else
				new Message(null, MessageType.ERROR, "Tried to send a command without a player instance");
			break;
		case EVERYONECMD:
			Bukkit.getOnlinePlayers().forEach(player->{
				player.performCommand(Message.getMessage(command));
			});
			break;
		case CONSOLECMD:
		default:
			Main.getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), Message.getMessage(command));
			break;
		}
	}
}
