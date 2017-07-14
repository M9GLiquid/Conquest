package eu.kingconquest.conquest.util;

public enum CommandType {
	PLAYERCMD("PlayerCmd"),
	EVERYONECMD("EveryoneCmd"),
	CONSOLECMD("ConsoleCmd");

	private String name;

	CommandType(String name){
		this.name = name;	
	}

	public String getName(){
		return name;
	}
}
