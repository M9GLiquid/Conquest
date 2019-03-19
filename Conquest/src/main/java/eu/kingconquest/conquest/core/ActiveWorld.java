package eu.kingconquest.conquest.core;

import eu.kingconquest.conquest.event.ActiveWorldCreateEvent;
import eu.kingconquest.conquest.event.ActiveWorldDeleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.sql.Timestamp;
import java.util.ArrayList;

public class ActiveWorld {
    private static ArrayList<ActiveWorld> worlds = new ArrayList<>();
    World world;
    Timestamp enable_date;
    Timestamp disable_date;
    boolean enabled;
    private boolean update;

    public ActiveWorld(World world, Timestamp enable_date, Timestamp disable_date, boolean enabled) {
        this.world = world;
        this.enable_date = enable_date;
        this.disable_date = disable_date;
        this.enabled = enabled;
        update(true);
        addWorld(this);
    }

    public static ArrayList<ActiveWorld> getWorlds() {
        return worlds;
    }

    public static void addWorld(ActiveWorld aWorld) {
        worlds.add(aWorld);
    }

    public static void addWorlds(ArrayList<ActiveWorld> aWorld) {
        worlds.addAll(aWorld);
    }

    public static ActiveWorld getActiveWorld(World world) {
        for (ActiveWorld aWorld : getWorlds())
            if (aWorld.getWorld().equals(world))
                return aWorld;
        return null;
    }

    public Timestamp getEnableDate() {
        return enable_date;
    }

    public void setEnableDate(String enable_date) {
        update(true);
        this.enable_date = Timestamp.valueOf(enable_date);
    }

    public Timestamp getDisableDate() {

        return disable_date;
    }

    public void setDisableDate(String disable_date) {
        update(true);
        this.enable_date = Timestamp.valueOf(disable_date);
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        update(true);
        this.enabled = enabled;
    }

    public boolean create() {
        update(true);
        Bukkit.getPluginManager().callEvent(new ActiveWorldCreateEvent(this));
        return false;
    }

    public boolean delete() {
        update(true);
        Bukkit.getPluginManager().callEvent(new ActiveWorldDeleteEvent(this));
        return false;
    }

    public World getWorld() {
        return world;
    }

    public void update(boolean update) {
        this.update = update;
    }

    public boolean getUpdate() {
        return this.update;
    }
}
