package eu.kingconquest.conquest.hook;

import java.util.HashMap;

import eu.kingconquest.conquest.core.util.ChatManager;

public class Hooks{
	private static String headerMsg = 	"&6| - &aHooked:";
	private static String errorMsg = 	"&6| - &cFailed:";
	private static HashMap<String, Boolean> msg = new HashMap<String, Boolean>();
	
	public static void output() {
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
