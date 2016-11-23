package eu.kingconquest.conquest.hook;

import org.bukkit.plugin.RegisteredServiceProvider;

import eu.kingconquest.conquest.Main;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class Vault {
    public static Economy econ = null;
    public static Permission perms = null;
    //public static Chat chat = null;

	/**
	 * @info Vault hook.
	 * @param return: void
	 */
	public Vault() {
		if (setupPermissions())
			Hooks.put("&6| --&3 Permissions [&6Vault&3]", true);
		else
			Hooks.put("&6| --&3 Permissions [&6Vault&3]", false);
		
		if (setupEconomy())
			Hooks.put("&6| --&3 Economy [&6Vault&3]", true);
		else
			Hooks.put("&6| --&3 Economy [&6Vault&3]", false);
	}

	/**
	 * @info Access permissions
	 * @return boolean
	 */
    private boolean setupPermissions(){
        RegisteredServiceProvider<Permission> rsp = Main.getInstance().getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null)
        	return false;
        perms = rsp.getProvider();
        return perms != null;
    }

	/**
	 * @info Access economy
	 * @return boolean
	 */
    private boolean setupEconomy(){
        RegisteredServiceProvider<Economy> rsp = Main.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
        	return false;
        econ = rsp.getProvider();
        return econ != null;
    }
}
