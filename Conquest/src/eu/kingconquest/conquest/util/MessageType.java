package eu.kingconquest.conquest.util;

public enum MessageType{
	CHAT("Chat"),
	ERROR("Error"),
	DEBUG("Debug"),
	CONSOLE("Console"),
	BROADCAST("Broadcast");
	
	private String name;
	
	MessageType(String name){
	this.name = name;	
	}
	
	public String getName(){
		return name;
	}
}
