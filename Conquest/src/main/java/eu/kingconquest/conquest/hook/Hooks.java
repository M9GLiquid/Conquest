package eu.kingconquest.conquest.hook;

import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.MessageType;
import eu.kingconquest.conquest.util.Validate;

import java.util.HashMap;


public class Hooks{
	private static HashMap<String, Boolean> msg = new HashMap<>();
	
	public static void output() {
		if (Validate.isNull(Vault.econ)){
			if (Validate.notNull(EconAPI.econ))
				Hooks.put("&6| --&3 Economy [&6TNE API&3]", true);
			else
				Hooks.put("&6| --&4 No Economy API Available!", false);
		}
		
			if (msg.containsValue(true)) {
				String headerMsg = "&6| - &aHooked:";
				new Message(null, MessageType.CONSOLE, headerMsg);
				msg.forEach((s,b)->{
					if (b) {
						new Message(null, MessageType.CONSOLE, s);
					}
				});
			}
			if (msg.containsValue(false)) {
				String errorMsg = "&6| - &cFailed:";
				new Message(null, MessageType.CONSOLE, errorMsg);
				msg.forEach((s,b)->{
					if (!b)
					new Message(null, MessageType.CONSOLE, s);
				});
			}
			msg.clear();
	}
	public static void put(String str, Boolean b) {
		msg.put(str, b);
	}
}
