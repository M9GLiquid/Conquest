package eu.kingconquest.conquest.core;

import eu.kingconquest.conquest.Scoreboard.NeutralBoard;
import eu.kingconquest.conquest.event.ObjectiveCreateEvent;
import eu.kingconquest.conquest.event.ObjectiveDeleteEvent;
import eu.kingconquest.conquest.hook.EconAPI;
import eu.kingconquest.conquest.util.Cach;
import eu.kingconquest.conquest.util.Marker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class Arena extends Objective {
    /**
     * Arenas
     */
    private static ArrayList<Arena> arenas = new ArrayList<>();
    private String name;
    private Location perimiterA, perimiterB;
    private Location spawn;
    /**
     * Members
     */
    private ArrayList<UUID> players = new ArrayList<>();

    public Arena(String name, Location perimiterA, Location perimiterB, Location spawn) {
        super(name, spawn, spawn, null);
        this.perimiterA = perimiterA;
        this.perimiterB = perimiterB;
        this.name = name;
        this.spawn = spawn;
    }

    public static void removeArenas(ArrayList<Arena> arena) {
        arenas.removeAll(arena);
    }

    public static void removeArena(Arena arena) {
        arenas.remove(arena);
    }

    public static ArrayList<Arena> getArenas() {
        return arenas;
    }

    public static ArrayList<Arena> getArenas(World world) {
        ArrayList<Arena> arenas = new ArrayList<>();
        Arena.getArenas().stream()
                .filter(arena -> arena.getWorld().equals(world))
                .forEach(arenas::add);
        return arenas;
    }

    public static Arena getArena(UUID ID, World world) {
        for (Arena arena : getArenas())
            if (arena.getUUID().equals(ID)
                    && arena.getWorld().equals(world))
                return arena;
        return null;
    }

    public static Arena getArena(String name, World world) {
        for (Arena arena : getArenas(world))
            if (arena.getName().equals(name)
                    && arena.getWorld().equals(world))
                return arena;
        return null;
    }

    public static void addArenas(ArrayList<Arena> arena) {
        arenas.addAll(arena);
    }

    public static void addArena(Arena arena) {
        arenas.add(arena);
    }

    /**
     * Arena Join
     *
     * @param player - Player Instance
     * @return void
     */
    public void join(Player player) {
        PlayerWrapper wrapper = PlayerWrapper.getWrapper(player);

        //Player joins this Arena
        Cach.StaticArena = this;
        //new Message(player, MessageType.CHAT, "{JoinArenaSuccess}"); //TODO Add to Language.yml
        wrapper.setArena(getUUID());
        // new ArenaBoard(player);  // TODO ArenaBoard
        addPlayer(player.getUniqueId());
    }

    /**
     * Arena Leave
     *
     * @param player - Player Instance
     * @return void
     */
    public void leave(Player player) {
        PlayerWrapper wrapper = PlayerWrapper.getWrapper(player);
        if (!wrapper.getArena(player.getWorld()).equals(this))
            return;
        if (!getAllPlayers().contains(player.getUniqueId()))
            return;

        //Player leaves this Arena
        Cach.StaticArena = this;
        //new Message(player, MessageType.CHAT, "{LeaveArenaSuccess}");  //TODO Add to Language.yml
        wrapper.setArena(null);
        new NeutralBoard(player);
        removePlayer(player.getUniqueId());
    }

    @Override
    public boolean create(Player player) {
        Bukkit.getPluginManager().callEvent(new ObjectiveCreateEvent(player, this));
        EconAPI.createAccount(getUUID());
        return Marker.update(this);
    }

    @Override
    public boolean delete(Player player) {
        Bukkit.getPluginManager().callEvent(new ObjectiveDeleteEvent(player, this));
        removeArena(this);
        return Marker.remove(this);
    }

    public Location getPerimiterA() {
        return perimiterA;
    }

    public void setPerimiterA(Location perimiterA) {
        this.perimiterA = perimiterA;
    }

    public Location getPerimiterB() {
        return perimiterB;
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void B(Location perimiterB) {
        this.perimiterB = perimiterB;
    }

    public boolean hasPlayers(UUID uniqueID) {
        for (UUID id : getAllPlayers())
            if (id.equals(uniqueID))
                return true;
        return false;
    }

    public Player getPlayer(UUID uniqueID) {
        return Bukkit.getPlayer(uniqueID);
    }

    public ArrayList<UUID> getAllPlayers() {
        return players;
    }

    public boolean hasPlayers() {
        return players.size() > 0;
    }

    public void addPlayer(UUID member) {
        players.add(member);
    }

    public void addPlayers(ArrayList<UUID> members) {
        this.players.addAll(members);
    }

    public void removePlayer(UUID member) {
        this.players.remove(member);
    }

    public boolean removePlayers(ArrayList<UUID> members) {
        return this.players.removeAll(members);
    }

    public void clearPlayers() {
        players.clear();
    }

    @Override
    public void updateGlass() {

    }
}
