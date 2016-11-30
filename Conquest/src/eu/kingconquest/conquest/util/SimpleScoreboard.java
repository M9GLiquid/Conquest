package eu.kingconquest.conquest.util;
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
    private Objective objective;
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
    	text = ChatManager.Format(text);
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
        if (updated.isEmpty()) {
            return;
        }

        if (objective == null) {
            objective = scoreboard.registerNewObjective((title.length() > 16 ? title.substring(0, 15) : title), "dummy");
            objective.setDisplayName((title.length() > 32 ? title.substring(0, 31) : title));
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        removed.stream().forEach((remove) -> {
            for (String s : scoreboard.getEntries()) {
                Score score = objective.getScore(s);

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

            objective.getScore(team.getValue()).setScore(score);
            index -= 1;
        }

        updated.clear();
    }

    public void setTitle(String title) {
    	title = ChatManager.Format(title);
        this.title = (title.length() > 32 ? title.substring(0, 31) : title);

        if(objective != null)
            objective.setDisplayName(this.title);
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

    private int x;
	public void KingdomBoard(Player p){
		x = 0;
		setTitle("&6[&eKingdom Information&6]");
		Kingdom kingdom = PlayerWrapper.getWrapper(p).getKingdom();
		Village.getVillages().stream().filter(village->village.getOwner().equals(kingdom)).forEach(village->{
			if (!village.hasParent())
				x++;
		});
		Town.getTowns().stream().filter(town->town.getOwner().equals(kingdom)).forEach(town->{
			x++;
		});
		add(12, "&6&lKingdom: &r&7" + kingdom.getColorSymbol() + kingdom.getName());
		add(11, "&6&lKing:" /*+kingdom.getKingName()*/);	
		add(10, "&7*Coming Soon*");	
		add(9, "&6&lMoney: &r&7");	
		add(8, "&7*Coming Soon* " /*+ TNEApi.getBalance(kingdom.getUUID())*/);	
		add(7, "&a&lKingdom Specific");	
		add(6, "&6Objectives Captured:");	
		add(5, "&7" + x);	
		add(4, "&6Traps Deployed:");	
		add(3, "&7*Coming Soon*  ");		
		add(2, " ");		
		add(1, "  ");		
		add(0, "   ");		
		send(p);
	}

	public void CaptureBoard(Player player, Village village){
		setTitle("&6[&eCapture Information&6]");
		add(12, "&a&lName: ");	
		add(11, village.getOwner().getColorSymbol() + village.getName());	
		add(10, "       ");	
		add(9, "&a&lOwner:");
		add(8, village.getOwner().getColorSymbol() + village.getOwner().getName());	
		add(7, "  ");	
		add(6, "&a&lCapture Progress:");	
		add(5, "&e" + village.getProgress() + "%");	
		add(4, "        ");	
		add(3, "         ");	
		add(2, "          ");		
		add(1, "           ");
		add(0, "            ");
		send(player);
	}
	
	public void PlayerBoard(Player player){
		PlayerWrapper wrapper = PlayerWrapper.getWrapper(player);
		Kingdom kingdom = (wrapper.isInKingdom() ? wrapper.getKingdom() : Kingdom.getNeutral(player.getWorld()));
		setTitle("&6[&ePlayer Information&6]");
		add(12, "&6&lKingdom: &r&7" + kingdom.getColorSymbol() + kingdom.getName());
		add(11, "&6&lMoney:");	
		add(10, "&7*Coming Soon*");	
		add(9, "&6&lFriends Online:");
		add(8, "&7*Coming Soon* ");
		add(7, "&6&lRank:");
		add(6, "&7*Coming Soon* ");
		add(5, "&6Level:");
		add(4, "&7*Coming Soon* ");
		add(3, "&6&lxp for next lvl:");
		add(2, "&7*Coming Soon* ");
		add(1, "");
		add(0, "&7*Coming Soon* ");
		send(player);
	}
	
	public void NeutralBoard(Player player){
		add(12, "&1Welcome: &r" + player.getName());
		add(11, "           ");
		add(10, "&6&lNetwork: &rKingConquest");	
		add(9, "&6&lServer: &rConquest");	
		add(8, "   ");	
		add(7, "&1&lTo Join a capital:");	
		add(6, "&6Steps:");	
		add(5, "&61: &f/kc");	
		add(4, "&62: &eInteract! ");		
		add(3, "&61For Help: ");	
		add(2, "&61: &f/kc ");	
		add(1, "&62: &eInteract!");		
		add(0, "");
		send(player);
		
	}
}