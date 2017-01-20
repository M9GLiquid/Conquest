package eu.kingconquest.conquest.hook;

import java.util.HashMap;

import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.MessageType;
import eu.kingconquest.conquest.util.Validate;


public class Hooks{
	private static String headerMsg = 	"&6| - &aHooked:";
	private static String errorMsg = 	"&6| - &cFailed:";
	private static HashMap<String, Boolean> msg = new HashMap<String, Boolean>();
	
	public static void output() {
		if (Validate.isNull(Vault.econ)){
			if (Validate.notNull(TNEApi.econ))
				Hooks.put("&6| --&3 Economy [&6TNE API&3]", true);
			else
				Hooks.put("&6| --&4 No Economy API Available!", false);
		}
		
			if (msg.containsValue(true)) {
				new Message(null, MessageType.CONSOLE, headerMsg);
				msg.forEach((s,b)->{
					if (b) {
						new Message(null, MessageType.CONSOLE, s);
					}
				});
			}
			if (msg.containsValue(false)) {
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
