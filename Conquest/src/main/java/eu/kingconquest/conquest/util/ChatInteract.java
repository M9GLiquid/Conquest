package eu.kingconquest.conquest.util;

import eu.kingconquest.conquest.Conquest;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

public class ChatInteract{

	public ChatInteract(Player player, Prompt promptClass, String escapeChar){
        ConversationFactory cf = new ConversationFactory(Conquest.getInstance());
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
