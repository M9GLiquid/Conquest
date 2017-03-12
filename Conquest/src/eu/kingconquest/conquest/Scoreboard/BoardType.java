package eu.kingconquest.conquest.Scoreboard;

public enum BoardType{
	NEUTRALBOARD("neutralboard"),
	PLAYERBOARD("playerboard"),
	KINGDOMBOARD("Kkingdomboard"),
	CAPTUREBOARD("captureboard"),
	TRAPBOARD("trapboard");

	private String name;
	BoardType(String name){
		this.name = name;	
	}

	public String getName(){
		return name;
	}
	
}
