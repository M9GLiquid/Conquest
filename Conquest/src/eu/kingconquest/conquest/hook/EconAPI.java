package eu.kingconquest.conquest.hook;

import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Player;

import eu.kingconquest.conquest.database.YmlStorage;
import eu.kingconquest.conquest.util.Message;
import eu.kingconquest.conquest.util.MessageType;
import eu.kingconquest.conquest.util.Validate;

public class EconAPI{
    public static Vault econ = null; //TNEAPI

    public EconAPI(){
    	//tneEcon = TNE.instance.api;
	}
    
	public static boolean createBank(UUID uuid, World world){
		if (Validate.notNull(econ)){
	    	/*String string = "kingdom-" + uuid.toString();
	    	if (!econ.bankExists(string, world.getUID().toString())){
	    		econ.createBank(string, world.getUID().toString());
	    	if (econ.bankExists(string, world.getUID().toString()))
	    		return true;
	    	return false;
	    	}else{
	    		return false;
	    	}*/
		}
		return false;
    }

	public static boolean createBank(Player player, World world){
		if (Validate.notNull(econ)){
	    	/*if (!econ.bankExists(player, world.getUID().toString())){
	    		econ.createBank(player, world.getUID().toString());
	    	if (econ.bankExists(player, world.getUID().toString()))
	    		return true;
	    	return false;
	    	}else{
	    		return false;
	    	}*/
		}else if (Validate.notNull(Vault.econ)){
			if (Vault.econ.createBank(player.getUniqueId().toString(), player).transactionSuccess())
				return true;
			else
				return false;
		}
		return false;
    }
	
	@SuppressWarnings("deprecation")
	public static boolean createAccount(UUID uuid){
		if (Validate.notNull(econ)){
			/*String string = "kingdom-" + uuid.toString();
	    	if (!econ.accountExists(string)){
				econ.createAccount(string);
	    	if (econ.accountExists(string))
	    		return true;
	    	return false;
	    	}else{
	    		return false;
	    	}*/
		}else if (Validate.notNull(Vault.econ)){
			if (Vault.econ.createPlayerAccount(uuid.toString()))
				return true;
			else
				return false;
		}
		return false;
    }

    public static boolean createAccount(Player player){
		if (Validate.notNull(econ)){
	    	/*if (!econ.accountExists(player)){
				econ.createAccount(player);
	    	if (econ.accountExists(player))
	    		return true;
	    	return false;
	    	}else{
	    		return false;
	    	}*/
		}else if (Validate.notNull(Vault.econ)){
			if (Vault.econ.createPlayerAccount(player))
				return true;
			else
				return false;
		}
		return false;
    }
    
    @SuppressWarnings("deprecation")
	public static boolean bankExist(UUID uuid, World world){
		if (Validate.notNull(econ)){
	    	/*String string = "kingdom-" + uuid.toString();
	    	if (econ.bankExists(string, world.getUID().toString()))
	    		return true;
	    	else
	    		return false;*/
		}else if (Validate.notNull(Vault.econ)){
			if (Vault.econ.isBankOwner(uuid.toString(), uuid.toString()).transactionSuccess())
				return true;
			else
				return false;
		}
		return false;
    }

	public static boolean accountExist(Player player){
		if (Validate.notNull(econ)){
	    	/*if (econ.accountExists(player))
	    		return true;
	    	else
	    		return false;*/
		}else if (Validate.notNull(Vault.econ)){
			if (Vault.econ.hasAccount(player))
				return true;
			else
				return false;
		}
		return false;
    }
    
    @SuppressWarnings("deprecation")
	public static boolean accountExist(UUID uuid){
		if (Validate.notNull(econ)){
	    	/*String string = "kingdom-" + uuid.toString();
	    	if (econ.accountExists(string))
	    		return true;
	    	else
	    		return false;*/
		}else if (Validate.notNull(Vault.econ)){
			if (Vault.econ.hasAccount(uuid.toString()))
				return true;
			else
				return false;
		}
		return false;
    }

	public static boolean addFunds(Player player, Double amount){
		if (Validate.notNull(econ)){
	    	/*Double d = econ.getBalance(player);
	    	econ.fundsAdd(player, amount);
	    	if (d < econ.getBalance(player)){
	    		ChatManager.Chat(player, "&3Added " + Config.getDoubles("CapCash", player.getLocation()) +"$ to you're account");
	    		return true;
	    	}else
	    		return false;*/
		}else if (Validate.notNull(Vault.econ)){
			if (Vault.econ.depositPlayer(player, amount).transactionSuccess()){
				new Message(player, MessageType.CHAT, "&3Added " + YmlStorage.getDouble("CapCash", player.getLocation()) +"$ to you're account");
				return true;
			}else
				return false;
		}
		return false;
    }
    
	@SuppressWarnings("deprecation")
	public static boolean addFunds(UUID uuid, Double amount){
		if (Validate.notNull(econ)){
			/*String string = "kingdom-" + uuid.toString();
	    	Double d = econ.getBalance(string);
	    	econ.fundsAdd(string, amount);
	    	if (d < econ.getBalance(string))
	    		return true;
	    	else
	    		return false;*/
		}else if (Validate.notNull(Vault.econ)){
			if (Vault.econ.depositPlayer("kingdom-" + uuid.toString(), amount).transactionSuccess()){
				return true;
			}else
				return false;
		}
		return false;
    }
    
	public static Double getBalance(Player player){
		/*if (Validate.notNull(econ))
	    	return econ.getBalance(player);
		else*/ if (Validate.notNull(Vault.econ))
			return Vault.econ.getBalance(player);
		return 0.0d;
    }

	@SuppressWarnings("deprecation")
	public static Double getBalance(UUID uuid){
		if (Validate.notNull(econ)){
	    	/*String string = "kingdom-" + uuid.toString();
	    	return econ.getBalance(string);*/
		}else if (Validate.notNull(Vault.econ))
			return Vault.econ.getBalance("kingdom-" +uuid.toString());
		return 0.0d;
    }
	
	/*public static boolean addBankMember(UUID uuid){
    	return true;
    }*/
    
	/*public static boolean addBankMember(Player player){
    	return true;
    }*/
    
    /*public static boolean removeBankMember(Player player){
    	return true;
    }*/
    
    /*public static boolean removeBankMember(UUID uuid){
    	return true;
    }*/
    
   /* public static boolean isBankMember(Player player){
    	return true;
    }*/
    
   /* public static boolean isBankMember(UUID uuid){
    	return true;
    }*/
    
    /*public static boolean getBank(String string){
    	return true;
    }*/
}
