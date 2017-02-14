package eu.kingconquest.conquest.chatinteract;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

import eu.kingconquest.conquest.core.ChestGui;
import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.Validate;

public class NamePrompt extends StringPrompt{
	private ChestGui gui;
	private String name = "None";

	public NamePrompt(ChestGui gui){
		this.gui = gui;
	}

	@Override
	public Prompt acceptInput(ConversationContext context, String answer){
		context.getForWhom().sendRawMessage(Message.getMessage("{Prefix} &7Name: " + answer));
		
		setName(answer);
		if (Validate.notNull(gui))
			gui.create();
		
		return null;
	}

	@Override
	public String getPromptText(ConversationContext context){
		context.getForWhom().sendRawMessage(Message.getMessage("&6---- [ &dChat Interaction &6] ----"));
		context.getForWhom().sendRawMessage(Message.getMessage("&6Type in the desired name in chat or exit the interaction with &cCancel"));
		context.getForWhom().sendRawMessage(Message.getMessage(" "));
		return "";
	}

	private void setName(String answer){
		System.out.println("Set: " + answer);
		this.name = answer;
	}
	public String getName(){
		System.out.println("Get: " + name);
		return name;
	}
}
