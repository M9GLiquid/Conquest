package eu.kingconquest.conquest.chatinteract;

import eu.kingconquest.conquest.core.ChestGui;
import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.Validate;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

import java.util.ArrayList;

public class LorePrompt extends StringPrompt{
	private ChestGui gui;
    private ArrayList<String> lore = new ArrayList<>();

	public LorePrompt(ChestGui gui){
		this.gui = gui;
	}

	@Override
	public Prompt acceptInput(ConversationContext context, String answer){
		context.getForWhom().sendRawMessage(Message.getMessage("{Prefix} &7Lore: "));
        lore.forEach(text -> context.getForWhom().sendRawMessage(Message.getMessage(text)));
		
		if (answer.equalsIgnoreCase("SAVE")){
			if (Validate.notNull(gui)){
				gui.create();
				gui.display();
			}
			return null;
		}else
			lore.add(answer);
		return this;
	}

	@Override
	public String getPromptText(ConversationContext context){
		context.getForWhom().sendRawMessage(Message.getMessage("&6---- [ &dChat Interaction &6] ----"));
		context.getForWhom().sendRawMessage(Message.getMessage("&6Add Lore or exit the interaction with &cCancel&6 or save by &aSave"));
		context.getForWhom().sendRawMessage(Message.getMessage(" "));
		return "";
	}

	public ArrayList<String> get(){
		return lore;
	}

}
