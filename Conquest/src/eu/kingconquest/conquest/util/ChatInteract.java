package eu.kingconquest.conquest.util;

import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import eu.kingconquest.conquest.Main;

public class ChatInteract{
	private ConversationFactory cf = new ConversationFactory(Main.getInstance());

	public ChatInteract(Player player, Prompt promptClass, String escapeChar){
				cf
				.withFirstPrompt(promptClass)
				.withLocalEcho(false)
				.withTimeout(60)
				.withEscapeSequence(escapeChar)
				.addConversationAbandonedListener(new ChatExit())
				.buildConversation(player)
				.begin();
	}
	
	private class ChatExit implements ConversationAbandonedListener{

		@Override
		public void conversationAbandoned(ConversationAbandonedEvent event){
			event.getContext().getForWhom().sendRawMessage(Message.getMessage("{Prefix} &cExited Chat Interactions!"));
		}
		
	}
}
