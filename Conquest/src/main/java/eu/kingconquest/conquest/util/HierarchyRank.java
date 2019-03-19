package eu.kingconquest.conquest.util;

public enum HierarchyRank {
    DEFAULT("Default"),
    GENTLEMAN("Gentleman"),
    SQUIRE("Squire"),
    BARON("Baron"),
    COUNT("Count"),
    KING("King");

    private String name;

    HierarchyRank(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
