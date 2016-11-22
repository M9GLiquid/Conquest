package eu.kingconquest.conquest.core.util;
import java.lang.reflect.Constructor;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import eu.kingconquest.conquest.core.Kingdom;
import eu.kingconquest.conquest.core.PlayerWrapper;
import eu.kingconquest.conquest.core.Town;
import eu.kingconquest.conquest.core.Village;

//Not of own design merly updated/Changed to fit my need
public class SimpleScoreboard {

    private static Map<String, OfflinePlayer> cache = new HashMap<>();

    private Scoreboard scoreboard;
    private String title;
    private Map<String, Integer> scores;
    private Objective obj;
    private List<Team> teams;
    private List<Integer> removed;
    private Set<String> updated;

    public SimpleScoreboard(String title) {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        setTitle(title);
        this.scores = new ConcurrentHashMap<>();
        this.teams = Collections.synchronizedList(Lists.newArrayList());
        this.removed = Lists.newArrayList();
        this.updated = Collections.synchronizedSet(new HashSet<>());
    }

    public void add(Integer score, String text) {

        text = ChatColor.translateAlternateColorCodes('&', text);
        if (text.length() > 30) 
            text = text.substring(0, 29); // cut off suffix, done if text is over 30 characters

        if (remove(score, text, false) || !scores.containsValue(score)) {
            updated.add(text);
        }

        scores.put(text, score);
    }

    public boolean remove(Integer score, String text) {
        return remove(score, text, true);
    }

    public boolean remove(Integer score, String n, boolean b) {
        String toRemove = get(score, n);

        if (toRemove == null)
            return false;

        scores.remove(toRemove);

        if(b)
            removed.add(score);

        return true;
    }

    public String get(int score, String n) {
        String str = null;

        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            if (entry.getValue().equals(score) &&
                    !entry.getKey().equals(n)) {
                str = entry.getKey();
            }
        }

        return str;
    }

    private Map.Entry<Team, OfflinePlayer> createTeam(String text, int pos) {
        Team team;
        ChatColor color = ChatColor.values()[pos];
        OfflinePlayer result;

        if (!cache.containsKey(color.toString()))
            cache.put(color.toString(), getOfflinePlayerSkipLookup(color.toString()));

        result = cache.get(color.toString());

        try {
            team = scoreboard.registerNewTeam("text-" + (teams.size() + 1));
        } catch (IllegalArgumentException e) {
            team = scoreboard.getTeam("text-" + (teams.size()));
        }

        applyText(team, text, result);

        teams.add(team);
        return new AbstractMap.SimpleEntry<>(team, result);
    }

    @SuppressWarnings("deprecation")
	private void applyText(Team team, String text, OfflinePlayer result) {
        Iterator<String> iterator = Splitter.fixedLength(16).split(text).iterator();
        String prefix = iterator.next();

        team.setPrefix(prefix);

        if(!team.hasPlayer(result))
            team.addPlayer(result);

        if (text.length() > 16) {
            String prefixColor = ChatColor.getLastColors(prefix);
            String suffix = iterator.next();

            if (prefix.endsWith(String.valueOf(ChatColor.COLOR_CHAR))) {
                prefix = prefix.substring(0, prefix.length() - 1);
                team.setPrefix(prefix);
                prefixColor = ChatColor.getByChar(suffix.charAt(0)).toString();
                suffix = suffix.substring(1);
            }

            if (prefixColor == null)
                prefixColor = "";

            if (suffix.length() > 16) {
                suffix = suffix.substring(0, (13 - prefixColor.length())); // cut off suffix, done if text is over 30 characters
            }

            team.setSuffix((prefixColor.equals("") ? ChatColor.RESET : prefixColor) + suffix);
        }
    }

    @SuppressWarnings("deprecation")
	public void update() {
    	ChatManager.Format(title);
        if (updated.isEmpty()) {
            return;
        }

        if (obj == null) {
            obj = scoreboard.registerNewObjective((title.length() > 16 ? title.substring(0, 15) : title), "dummy");
            obj.setDisplayName((title.length() > 32 ? title.substring(0, 31) : title));
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        removed.stream().forEach((remove) -> {
            for (String s : scoreboard.getEntries()) {
                Score score = obj.getScore(s);

                if (score == null)
                    continue;

                if (score.getScore() != remove)
                    continue;

                scoreboard.resetScores(s);
            }
        });

        removed.clear();

        int index = scores.size();

        for (Map.Entry<String, Integer> text : scores.entrySet()) {
            Team t = scoreboard.getTeam(ChatColor.values()[text.getValue()].toString());
            Map.Entry<Team, OfflinePlayer> team;

            if(!updated.contains(text.getKey())) {
                continue;
            }

            if(t != null) {
                String color = ChatColor.values()[text.getValue()].toString();

                if (!cache.containsKey(color)) {
                    cache.put(color, getOfflinePlayerSkipLookup(color));
                }

                team = new AbstractMap.SimpleEntry<>(t, cache.get(color));
                applyText(team.getKey(), text.getKey(), team.getValue());
                index -= 1;

                continue;
            } else {
                team = createTeam(text.getKey(), text.getValue());
            }

            Integer score = text.getValue() != null ? text.getValue() : index;

            obj.getScore(team.getValue()).setScore(score);
            index -= 1;
        }

        updated.clear();
    }

    public void setTitle(String title) {
    	title = ChatManager.Format(title);
        this.title = (title.length() > 32 ? title.substring(0, 31) : title);

        if(obj != null)
            obj.setDisplayName(this.title);
    }

    public void reset() {
        for (Team t : teams)
            t.unregister();
        teams.clear();
        scores.clear();
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void send(Player... players) {
		update();
        for (Player p : players)
            p.setScoreboard(scoreboard);
    }

    private final UUID invalidUserUUID = UUID.nameUUIDFromBytes("InvalidUsername".getBytes(Charsets.UTF_8));
    private Class<?> gameProfileClass;
    private Constructor<?> gameProfileConstructor;
    private Constructor<?> craftOfflinePlayerConstructor;

    @SuppressWarnings("deprecation")
    private OfflinePlayer getOfflinePlayerSkipLookup(String name) {
        try {
            if (gameProfileConstructor == null) {
                try { // 1.7
                    gameProfileClass = Class.forName("net.minecraft.util.com.mojang.authlib.GameProfile");
                } catch (ClassNotFoundException e) { // 1.8
                    gameProfileClass = Class.forName("com.mojang.authlib.GameProfile");
                }
                gameProfileConstructor = gameProfileClass.getDeclaredConstructor(UUID.class, String.class);
                gameProfileConstructor.setAccessible(true);
            }
            if (craftOfflinePlayerConstructor == null) {
                Class<?> serverClass = Bukkit.getServer().getClass();
                Class<?> craftOfflinePlayerClass = Class.forName(serverClass.getName()
                        .replace("CraftServer", "CraftOfflinePlayer"));
                craftOfflinePlayerConstructor = craftOfflinePlayerClass.getDeclaredConstructor(
                        serverClass, gameProfileClass
                );
                craftOfflinePlayerConstructor.setAccessible(true);
            }
            Object gameProfile = gameProfileConstructor.newInstance(invalidUserUUID, name);
            Object craftOfflinePlayer = craftOfflinePlayerConstructor.newInstance(Bukkit.getServer(), gameProfile);
            return (OfflinePlayer) craftOfflinePlayer;
        } catch (Throwable t) { // Fallback if fail
            return Bukkit.getOfflinePlayer(name);
        }
    }

    private int i;
    private int x;
	public void kingdomBoard(Player p){
		setTitle("&6[&eKingdom Information&6]");
		Kingdom kingdom = PlayerWrapper.getWrapper(p).getKingdom();
		Village.getVillages().stream().filter(village->village.getOwner().equals(kingdom)).forEach(village->{
			i++;
		});
		Town.getTowns().stream().filter(town->town.getOwner().equals(kingdom)).forEach(town->{
			x++;
		});
		add(12, ChatManager.Format("&6&lKingdom: &r&7" + kingdom.getColorSymbol() + kingdom.getName()));
		add(11, ChatManager.Format("&6&lKing: &r&7*Coming Soon*" /*+kingdom.getKingName()*/));	
		add(10, ChatManager.Format("&6&lRank: &r&7*Coming Soon*"));	
		add(9, ChatManager.Format("&6&lMoney: &r&7" /*+ Vault.econ.getBalance(p)*/));	
		add(8, ChatManager.Format("       "));	
		add(7, ChatManager.Format("&a&lKingdom Specific"));	
		add(6, ChatManager.Format("&cWorld Domination: &e"));	
		add(5, ChatManager.Format("&6Villages Captured: &e" + i));	
		add(4, ChatManager.Format("&6Towns Captured: &e" + x));	
		add(3, ChatManager.Format("                    "));	
		add(2, ChatManager.Format("          "));		
		add(1, ChatManager.Format("           "));
		send(p);
	}

	public void captureBoard(Player p, Village op){
		add(12, ChatManager.Format("&a&lName: "));	
		add(11, ChatManager.Format(op.getOwner().getColorSymbol() + op.getName()));	
		add(10, ChatManager.Format("       "));	
		add(9, ChatManager.Format("&a&lOwner:"));
		add(8, ChatManager.Format(op.getOwner().getColorSymbol() + op.getOwner().getName()));	
		add(7, ChatManager.Format("  "));	
		add(6, ChatManager.Format("&a&lCapture Process:"));	
		add(5, ChatManager.Format("&e" + op.getProgress() + "%"));	
		add(4, ChatManager.Format("        "));	
		add(3, ChatManager.Format("         "));	
		add(2, ChatManager.Format("          "));		
		add(1, ChatManager.Format("           "));
		send(p);
	}
	
	public void neutralBoard(Player p){
		add(12, ChatManager.Format("&1Welcome: &r" + p.getName()));
		add(11, ChatManager.Format("           "));
		add(10, ChatManager.Format("&6&lNetwork: &rKingConquest"));	
		add(9, ChatManager.Format("&6&lServer: &rConquest"));	
		add(8, ChatManager.Format("   "));	
		add(7, ChatManager.Format("&1&lTo Join a capital:"));	
		add(6, ChatManager.Format("&6Steps:"));	
		add(5, ChatManager.Format("&61: &f/kc"));	
		add(4, ChatManager.Format("&62: &eInteract! "));		
		add(3, ChatManager.Format("&1For Help:"));	
		add(2, ChatManager.Format("&61: /kc"));	
		add(1, ChatManager.Format("&62: &eInteract!"));		
		send(p);
		
	}
}