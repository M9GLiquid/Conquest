package eu.kingconquest.conquest.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.Objective;
import eu.kingconquest.conquest.event.ObjectiveCreateEvent;
import eu.kingconquest.conquest.hook.TNEApi;

public class CreateListener implements Listener{
	
	@EventHandler
	public void onCreate(ObjectiveCreateEvent event){
		// Code to add Balance/Bank to Kingdom on creation
		Objective objective = event.getObjective();
		if (objective instanceof Kingdom){
			Kingdom kingdom = (Kingdom) event.getObjective();
			TNEApi.createAccount(kingdom.getUUID());
			TNEApi.createBank(kingdom.getUUID(), kingdom.getLocation().getWorld());
		}
		/**
		 * Code to add to Objective on Creation
		 * TNEApi.econ.createAccount(event.getObjective().getUUID().toString());
		 * TNEApi.econ.createBank(event.getObjective().getUUID().toString(), event.getObjective().getLocation().getWorld().getName());
		 */
	}
}
