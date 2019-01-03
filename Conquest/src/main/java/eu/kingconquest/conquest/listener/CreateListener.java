package eu.kingconquest.conquest.listener;

import eu.kingconquest.conquest.core.Arena;
import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.Objective;
import eu.kingconquest.conquest.event.ObjectiveCreateEvent;
import eu.kingconquest.conquest.hook.EconAPI;
import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CreateListener implements Listener{
	
	@EventHandler
	public void onCreate(ObjectiveCreateEvent event){
		// Code to add Balance/Bank to Kingdom on creation
		Objective objective = event.getObjective();
        if (objective instanceof Kingdom){
            Kingdom kingdom = (Kingdom) event.getObjective();
            EconAPI.createAccount(kingdom.getUUID());
            EconAPI.createBank(kingdom.getUUID(), kingdom.getLocation().getWorld());

            // Create Group & Set the suffix | Add it via Vault? Is it possible?
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/lp creategroup " + kingdom.getName());
            Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    "/lp group " + kingdom.getName() + " permission set \"suffix.100.§6{" + kingdom.getColor() + kingdom.getName() + "§6}§r\"");
            new Message(event.getPlayer(), MessageType.CONSOLE, "{KingdomCreated}");
        } else if (objective instanceof Arena) {
            new Message(event.getPlayer(), MessageType.BROADCAST, "{ArenaCreated}"); //TODO Language file addition
        }
		/*
		 * Code to add to Objective on Creation
		 * TNEApi.econ.createAccount(event.getObjective().getUUID().toString());
		 * TNEApi.econ.createBank(event.getObjective().getUUID().toString(), event.getObjective().getLocation().getWorld().getName());
		 */
	}
}
