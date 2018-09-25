package eu.kingconquest.conquest.core;

import eu.kingconquest.conquest.MainClass;
import eu.kingconquest.conquest.Scoreboard.KingdomBoard;
import eu.kingconquest.conquest.Scoreboard.NeutralBoard;
import eu.kingconquest.conquest.event.ObjectiveCreateEvent;
import eu.kingconquest.conquest.event.ObjectiveDeleteEvent;
import eu.kingconquest.conquest.hook.EconAPI;
import eu.kingconquest.conquest.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class Kingdom extends Objective{
	private UUID	king;
	private int		color;

	public Kingdom(String name, String kingID, Location loc, int color){
		this(name, kingID, null, loc, loc, color);
	}

	public Kingdom(String name, String kingID, Location loc, Location spawn){
		this(name, kingID, null, loc, spawn, getKingdoms().size());
	}

	public Kingdom(String name, String kingID, String uniqueID, Location loc, Location spawn, int color){
		super(name, loc, spawn, uniqueID);
		if (Validate.notNull(kingID))
			setKing(UUID.fromString(kingID));
		setColor(color);
		addKingdom(this);
		Marker.update(this);
		Marker.setDescription(this);
	}

	//kingdomitals
	private static ArrayList<Kingdom> kingdoms = new ArrayList<>();
	//Outposts
	private ArrayList<Village> villages = new ArrayList<>();

	//Getters
	public Player getKing() {
		return Bukkit.getPlayer(king);
	}

	/**
	 * Get Kingdom Color by Integer
	 *
	 * @return Integer
	 */
	public Integer getIntColor() {
		return color;
	}

	/**
	 * Get Kingdom Color by Symbol
	 *
	 * @return Integer
	 */
	public String getColor() {
		return ColorManager.intToSymbols(color);
	}

	//Objectives
	private ArrayList<Town> towns = new ArrayList<>();

	//Setters
	//Members
	private ArrayList<UUID> members = new ArrayList<>();

	public static ArrayList<Kingdom> getKingdoms(World world) {
		ArrayList<Kingdom> kingdoms = new ArrayList<>();
		Kingdom.getKingdoms().stream()
				.filter(kingdom -> kingdom.getWorld().equals(world))
				.forEach(kingdoms::add);
		return kingdoms;
	}

	public static void removeKingdom(Kingdom kingdom) {
		kingdoms.remove(kingdom);
	}

	public static void removeKingdoms(ArrayList<Kingdom> kingdom) {
		kingdoms.removeAll(kingdom);
	}

	public Village getOutpost(UUID ID) {

		for (Village op : villages) {
			if (!op.getUUID().equals(ID))
				continue;
			return op;
		}
		return null;
	}

	public ArrayList<Village> getOutposts() {
		return villages;
	}

	/**
	 * Kingdom Join
	 *
	 * @param player
	 *            - Player Instance
	 * @return void
	 */
	public void join(Player player){
		PlayerWrapper wrapper = PlayerWrapper.getWrapper(player);

		//Player joins this Kingdom
		Cach.StaticKingdom = this;
		new Message(player, MessageType.CHAT, "{JoinSuccess}");
		wrapper.setKingdom(getUUID());
		new KingdomBoard(player);
		addMember(player.getUniqueId());
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " parent add " + getName() + " " + MainClass.getInstance().getServer().getServerName());
	}

	/**
	 * Kingdom Leave
	 *
	 * @param player
	 *            - Player Instance
	 * @return void
	 */
	public void leave(Player player){
		PlayerWrapper wrapper = PlayerWrapper.getWrapper(player);
		if (!wrapper.getKingdom(player.getWorld()).equals(this))
			return;
		if (!getMembers().contains(player.getUniqueId()))
			return;

		Cach.StaticKingdom = this;
		new Message(player, MessageType.CHAT, "{LeaveSuccess}");
		wrapper.setKingdom(null);
		new NeutralBoard(player);
		removeMember(player.getUniqueId());
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " parent remove " + getName() + " " + MainClass.getInstance().getServer().getServerName());
	}

	/**
	 * Get Total Map Capture Percentage
	 *
	 * @return Double
	 */
	public Double getCapturePercent(){
		for (Village op : Village.getVillages()){
			if (op.getParent() == null){
				villages.add(op);
			}
		}

		return Math.ceil(((towns.size() + villages.size() / Town.getTowns().size() * 100)));
	}

	/**
	 * Set Kingdom King
	 *
	 * @param uuid UUID
	 *            - String
	 * @return void
	 */
	public void setKing(UUID uuid){
		this.king = uuid;
	}

	/**
	 * Set Kingdom ID
	 *
	 * @param color Color
	 *            - Integer
	 * @return void
	 */
	public void setColor(Integer color){
		this.color = color;
	}

	public Town getObjective(UUID ID){
		for (Town obj : towns){
			if (!obj.getUUID().equals(ID))
				continue;
			return obj;
		}
		return null;

	}

	public ArrayList<Town> getObjectives(){
		return towns;

	}

	/**
	 * Add Objective to Kingdom Objectives
	 * 
	 * @param obj
	 *            - Objective
	 * @return void
	 */
	public void addObj(Town obj){
		this.towns.add(obj);
	}

	public void addObjs(ArrayList<Town> objs){
		this.towns.addAll(objs);
	}

	/**
	 * Remove Objective from Kingdom Objectives
	 * 
	 * @param obj
	 *            - Objective
	 * @return void
	 */
	public void removeObjective(Town obj){
		this.towns.remove(obj);
	}

	public void removeObjectives(ArrayList<Town> objs){
		this.towns.removeAll(objs);
	}

	/**
	 * Set Kingdom Location/Spawn
	 *
	 * @return void
	 */

	//Booleans Checks
	public boolean isNeutral() {
		return getName().equals("Neutral");
	}

	public boolean addOutpost(Village op) {
		return villages.add(op);
	}

	public Player getMember(UUID uniqueID){
		return Bukkit.getPlayer(uniqueID);
	}

	public boolean hasMember(UUID uniqueID){
		for (UUID id : getMembers())
			if (id.equals(uniqueID))
				return true;
		return false;
	}

	public ArrayList<UUID> getMembers() {
		return members;
	}

	public boolean addOutposts(ArrayList<Village> ops){
		return villages.addAll(ops);
	}

	public void addMember(UUID member) {
		members.add(member);
	}

	public boolean removeOutpost(Village op) {
		return villages.remove(op);
	}

	public boolean removeOutposts(ArrayList<Village> ops) {
		return villages.removeAll(ops);

	}

	public void clearMembers() {
		members.clear();
	}

	public boolean hasMembers(){
		return members.size() != 0;
	}

	public static ArrayList<Kingdom> getKingdoms() {
		return kingdoms;
	}

	public void addMembers(ArrayList<UUID> members){
		this.members.addAll(members);
	}

	public static Kingdom getKingdom(UUID ID, World world){
		for (Kingdom kingdom : getKingdoms())
			if (kingdom.getUUID().equals(ID)
					&& kingdom.getWorld().equals(world))
				return kingdom;
		return null;
	}

	public static Kingdom getKingdom(String name, World world){
		for (Kingdom kingdom : getKingdoms(world))
			if (kingdom.getName().equals(name)
					&& kingdom.getWorld().equals(world))
				return kingdom;
		return null;
	}

	public static Kingdom getNeutral(World world){
		return getKingdom("Neutral", world);
	}

	public static void addKingdoms(ArrayList<Kingdom> kingdom){
		kingdoms.addAll(kingdom);
	}

	public static void addKingdom(Kingdom kingdom) {
		kingdoms.add(kingdom);
	}

	public void removeMember(UUID member) {
		this.members.remove(member);
	}

	public boolean removeMembers(ArrayList<UUID> members) {
		return this.members.removeAll(members);
	}

	public static void clear(){
		Kingdom.getKingdoms().forEach(kingdom ->{
			kingdom.members.clear();
			kingdom.towns.clear();
			kingdom.villages.clear();
		});
		kingdoms.clear();
	}

	@Override
	public boolean create(Player player){
		Bukkit.getPluginManager().callEvent(new ObjectiveCreateEvent(player, this));
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "permissions creategroup " + getName());
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "permissions group " + getName() + " meta addsuffix  100 " + "\"&6{" + getColor() + getName() + "}&r &7\"");
		EconAPI.createAccount(getUUID());
		return Marker.update(this);
	}

	@Override
	public boolean delete(Player player){
		Cach.StaticKingdom = this;
		new Message(player, MessageType.CHAT, "{KingdomDeleted}");
		Bukkit.getPluginManager().callEvent(new ObjectiveDeleteEvent(player, this));
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp deletegroup " + getName());
		removeKingdom(this);
		return Marker.remove(this);
	}

	@Override
	public void updateGlass(){
	}
}
