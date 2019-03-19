package eu.kingconquest.conquest.hook;

import eu.kingconquest.conquest.Conquest;
import eu.kingconquest.conquest.util.Validate;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Vault {
    public static Permission perms = null;
    public static Economy econ = null;
    
    //public static Chat chat = null;

	/**
	 * @info Vault hook.
	 */
	public Vault() {
		if (setupPermissions())
			Hooks.put("&6| --&3 Permissions [&6Vault API&3]", true);
		else
			Hooks.put("&6| --&3 Permissions [&6Vault API&3]", false);
		
		if (setupEcoonomy() && Validate.isNull(EconAPI.econ))
			Hooks.put("&6| --&3 Economy [&6Vault API&3]", true);
		else
			Hooks.put("&6| --&3 Economy [&6Vault API&3]", false);
		
	}

	/**
	 * @info Access permissions
	 * @return boolean
	 */
    private boolean setupPermissions(){
        RegisteredServiceProvider<Permission> rsp = Conquest.getInstance().getServer().getServicesManager().getRegistration(Permission.class);
        if (Validate.isNull(rsp))
        	return false;
        perms = rsp.getProvider();
        return Validate.notNull(perms);
    }

	/**
	 * @info Access permissions
	 * @return boolean
	 */
    private boolean setupEcoonomy(){
        RegisteredServiceProvider<Economy> rsp = Conquest.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
        if (Validate.isNull(rsp))
        	return false;
        econ = rsp.getProvider();
        return Validate.notNull(econ);
    }
}
