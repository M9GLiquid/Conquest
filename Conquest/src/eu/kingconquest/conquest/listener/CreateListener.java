package eu.kingconquest.conquest.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.Objective;
import eu.kingconquest.conquest.event.ObjectiveCreateEvent;
import eu.kingconquest.conquest.hook.EconAPI;
import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.MessageType;

public class CreateListener implements Listener{
	
	@EventHandler
	public void onCreate(ObjectiveCreateEvent event){
		// Code to add Balance/Bank to Kingdom on creation
		Objective objective = event.getObjective();
		if (objective instanceof Kingdom){
			Kingdom kingdom = (Kingdom) event.getObjective();
			EconAPI.createAccount(kingdom.getUUID());
			EconAPI.createBank(kingdom.getUUID(), kingdom.getLocation().getWorld());
			new Message(null, MessageType.CONSOLE, "{KingdomCreated}");
			
			// Add it via Vault? Is it possible?
			// Create Group & Set the suffix
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/lp creategroup " + kingdom.getName());
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/lp group " + kingdom.getName() + " permission set \"suffix.100.&6{&2 + " + kingdom.getName() + "&6}&r &7\"");
		}
		/**
		 * Code to add to Objective on Creation
		 * TNEApi.econ.createAccount(event.getObjective().getUUID().toString());
		 * TNEApi.econ.createBank(event.getObjective().getUUID().toString(), event.getObjective().getLocation().getWorld().getName());
		 */
	}
}
