package eu.kingconquest.conquest.core;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import eu.kingconquest.conquest.util.Cach;
import eu.kingconquest.conquest.util.ChatManager;
import eu.kingconquest.conquest.util.ColorManager;
import eu.kingconquest.conquest.util.Config;
import eu.kingconquest.conquest.util.Marker;
import eu.kingconquest.conquest.util.Validate;


public class Kingdom extends Objective{
	private UUID king;
	private int color;

	public Kingdom(String name, String king, int color, Location location) {
		super(name, location, location, null);
		setColor(color);
		if (king != null)
			return;
	}
	public Kingdom(String name, String kingID, String uniqueID, Location loc, Location spawn, int color){
		this(name, kingID, uniqueID, loc, spawn, null, null, color);
	}
	public Kingdom(String name, String kingID, Location loc, Location spawn, ArrayList<Town> townChildren, ArrayList<Village> villageChildren){
		this(name, kingID, null, loc, spawn, townChildren, villageChildren, getKingdoms().size());
	}
	public Kingdom(String name, String kingID, String uniqueID, Location loc, Location spawn, ArrayList<Town> townChildren, ArrayList<Village> villageChildren, int color){
		super(name, loc, spawn, uniqueID);
		if (!Validate.isNull(kingID)) 
			setKing(UUID.fromString(kingID));
			
		if (!Validate.isNull(townChildren))
			townChildren.forEach(child->child.setOwner(this));
		if (!Validate.isNull(villageChildren))
			villageChildren.forEach(child->child.setOwner(this));
		setColor(color);
	}
	/**
	 * Kingdom Join
	 * @param p - Player Instance
	 * @param kingdomitalName - Name of Kingdom
	 * @return void
	 */
	public  void join(Player p){
		PlayerWrapper wrapper = PlayerWrapper.getWrapper(p);
		
		//Player joins this Kingdom
		Cach.StaticKingdom = this;
		ChatManager.Chat(p, Config.getChat("JoinSuccess"));
		wrapper.setKingdom(this);
		wrapper.getScoreboard().kingdomBoard(p);
		addMember(p.getUniqueId());
		Config.saveUsers(getLocation().getWorld());
	}
		
	/**
	 * Kingdom Leave
	 * @param p - Player Instance
	 * @param kingdomitalName - Name of Kingdom
	 * @return void
	 */
	public  void leave(Player p){
		PlayerWrapper wrapper = PlayerWrapper.getWrapper(p);
		if (!wrapper.getKingdom().equals(this))
			return;
		if(!getMembers().contains(p.getUniqueId()))
			return;
		
		Cach.StaticKingdom = this;
		ChatManager.Chat(p, Config.getChat("LeaveSuccess"));
		wrapper.setKingdom(null);
		wrapper.getScoreboard().neutralBoard(p);
		removeMember(p.getUniqueId());
		Config.saveUsers(getLocation().getWorld());
	}
	
//Getters
	public Player getKing(){
		return Bukkit.getPlayer(king);
	}
	/**
	 * Get Kingdom ID
	 * @return Integer
	 */
	public Integer getColor(){
		return color;
	}
	/**
	 * Get Kingdom ID
	 * @return Integer
	 */
	public String getColorSymbol(){
		return ColorManager.intToSymbols(color);
	}
	/**
	 * Get Total Map Capture Percentage
	 * @return Double
	 */
	public Double getCapturePercent(){
		for(Village op : Village.getVillages()){
			if(op.getParent() == null){
				villages.add(op);
			}
		}
		double kingdomturePercent = Math.ceil(((towns.size() + villages.size()/Town.getTowns().size() * 100)));

		return kingdomturePercent;
	}
	
//Setters
	/**
	 * Set Kingdom King
	 * @param king - String
	 * @return void
	 */
	public void setKing(UUID uuid){
		this.king = uuid;
	}
	/**
	 * Set Kingdom ID
	 * @param kingdomitalID - Integer
	 * @return void
	 */
	public void setColor(Integer color){
		this.color = color;
	}
	/**
	 * Set Kingdom Location/Spawn
	 * @param location - Location
	 * @return void
	 */

//Booleans Checks
	public boolean isNeutral(){
		if (getName().equals("Neutral"))
			return true;
		return false;
	}

//Outposts
	private ArrayList<Village> villages = new ArrayList<Village>();
	
	public Village getOutpost(UUID ID) {
		
		for (Village op : villages) {
			if (!op.getUUID().equals(ID))
				continue;
			return op;
		}
		return null;
	}
	public ArrayList<Village> getOutposts(){
		return villages;
	}
	public boolean addOutpost(Village op) {
		if (villages.add(op))
			return true;
		return false;
	}
	public boolean addOutposts(ArrayList<Village> ops) {
		if (villages.addAll(ops))
			return true;
		return false;
	}
	public boolean removeOutpost(Village op) {
		if (villages.remove(op))
			return true;
		return false;
	}
	public boolean removeOutposts(ArrayList<Village> ops) {
		if (villages.removeAll(ops))
			return true;
		return false;
		
	}
	
//Objectives
	private ArrayList<Town> towns = new ArrayList<Town>();
	
	public Town getObjective(UUID ID) {
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
	 * @param obj - Objective
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
	 * @param obj - Objective
	 * @return void
	 */
	public void removeObjective(Town obj){
		this.towns.remove(obj);
	}
	public void removeObjectives(ArrayList<Town> objs){
		this.towns.removeAll(objs);
	}
	
//Members
	private ArrayList<UUID> members = new ArrayList<UUID>();
	
	public boolean hasMembers(){
		if (members.size() != 0)
			return true;
		return false;
	}
	public Player getMember(UUID uniqueID){
		return Bukkit.getPlayer(uniqueID);
	}
	public boolean hasMember(UUID uniqueID){
		for(UUID id : getMembers())
			if (id.equals(uniqueID))
				return true;
		return false;
	}
	public ArrayList<UUID> getMembers(){
		return members;
	}
	public void addMembers(ArrayList<UUID> members){
		members.addAll(members);
	}
	public void addMember(UUID member){
		members.add(member);
	}
	public boolean removeMember(UUID member) {
		if (members.remove(member))
			return true;
		return false;
	}
	public boolean removeMembers(ArrayList<UUID> members) {
		if (members.removeAll(members))
			return true;
		return false;
	}
	public void clearMembers(){
		members.clear();
	}

//kingdomitals
	private static ArrayList<Kingdom> kingdoms = new ArrayList<Kingdom>();
	
	public static ArrayList<Kingdom> getKingdoms(){
		return kingdoms;
	}
	public static ArrayList<Kingdom> getKingdoms(World world){
		ArrayList<Kingdom> kingdoms = new ArrayList<Kingdom>();
		Kingdom.getKingdoms().stream()
			.filter(kingdom->kingdom.getLocation().getWorld().equals(world))
			.forEach(kingdom->{
				kingdoms.add(kingdom);
			});
		return kingdoms;
	}
	public static Kingdom getKingdom(UUID ID, World world){
		for (Kingdom kingdom : getKingdoms()){
			if (kingdom.getUUID().equals(ID)
					&& kingdom.getLocation().getWorld().equals(world))
			return kingdom;
		}
		return null;
	}
	public static Kingdom getKingdom(String name, World world){
		for (Kingdom kingdom : getKingdoms(world)){
			if (kingdom.getName().equals(name)
					&& kingdom.getLocation().getWorld().equals(world))
			return kingdom;
		}
		return null;
	}

	public static void addKingdoms(ArrayList<Kingdom> kingdom){
		kingdoms.addAll(kingdom);
	}
	public static void addKingdom(Kingdom kingdom){
		kingdoms.add(kingdom);
	}
	public static boolean removeKingdom(Kingdom kingdom){
		if (kingdoms.remove(kingdom))
			return true;
		return false;
	}
	public static boolean removeKingdoms(ArrayList<Kingdom> kingdom){
		if (kingdoms.removeAll(kingdom))
			return true;
		return false;
	}

	public static void clear(){
		Kingdom.getKingdoms().forEach(kingdom->{
			kingdom.members.clear();
			kingdom.towns.clear();
			kingdom.villages.clear();
		});
		kingdoms.clear();
	}
	
	@Override
	public boolean create(Player p){
		addKingdom(this);
		if (Marker.update(this)){
			Config.saveKingdoms(getLocation().getWorld());
			return true;
		}
		return false;
	}
	@Override
	public boolean delete(){
		removeKingdom(this);
		if (Marker.remove(this))
			return true;
		return false;
	}
	@Override
	public void updateGlass(){
	}
}
