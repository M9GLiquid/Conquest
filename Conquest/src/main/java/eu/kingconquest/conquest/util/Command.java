package eu.kingconquest.conquest.util;

import eu.kingconquest.conquest.Conquest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class Command {

	public Command(CommandType type, String command){
		this(null, type, command);
	}

    public Command(Player target, CommandType commandType, String command) {
        switch (commandType) {
            case PLAYER:
			if (Validate.notNull(target))
				target.performCommand(Message.getMessage(command));
			else
                new Message(MessageType.ERROR, "Tried to send a command without a player instance");
			break;
            case EVERYONE:
            Bukkit.getOnlinePlayers().forEach(player ->
                    player.performCommand(Message.getMessage(command)));
			break;
            case CONSOLE:
		default:
            Conquest.getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), Message.getMessage(command));
			break;
		}
	}
}
