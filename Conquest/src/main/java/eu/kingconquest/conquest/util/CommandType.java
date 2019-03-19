package eu.kingconquest.conquest.util;

public enum CommandType {
    PLAYER("Player Command"),
    EVERYONE("Everyone Command"),
    CONSOLE("Console Command");

	private String name;

	CommandType(String name){
		this.name = name;	
	}

	public String getName(){
		return name;
	}
}
