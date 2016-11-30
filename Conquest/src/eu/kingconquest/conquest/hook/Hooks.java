package eu.kingconquest.conquest.hook;

import java.util.HashMap;

import eu.kingconquest.conquest.util.ChatManager;
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
				ChatManager.Console(headerMsg);
				msg.forEach((s,b)->{
					if (b) {
						ChatManager.Console(s);
					}
				});
			}
			if (msg.containsValue(false)) {
				ChatManager.Console(errorMsg);
				msg.forEach((s,b)->{
					if (!b)
						ChatManager.Console(s);
				});
			}
			msg.clear();
	}
	public static void put(String str, Boolean b) {
		msg.put(str, b);
	}
}
