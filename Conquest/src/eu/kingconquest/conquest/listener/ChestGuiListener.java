package eu.kingconquest.conquest.listener;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import eu.kingconquest.conquest.util.ChestGui;
import eu.kingconquest.conquest.util.Validate;


public class ChestGuiListener implements Listener{
	
    @EventHandler
    public void onClick(InventoryClickEvent e){
        if (!(e.getWhoClicked() instanceof Player))
            return;
        Player p = (Player) e.getWhoClicked();
        UUID inventoryUUID = ChestGui.openInventories.get(p.getUniqueId());
        if (!Validate.isNull(inventoryUUID)){
            e.setCancelled(true);
            ChestGui GUI = ChestGui.getInventoriesByUUID().get(inventoryUUID);
            GUI.setClickType(e.getClick());
            ChestGui.onGuiAction action = GUI.getActions().get(e.getSlot());
    		if (!Validate.isNull(action))
                action.onClick(p);
        }
    }
    
    @EventHandler
    public void onClose(InventoryCloseEvent e){
        Player p = (Player) e.getPlayer();
        ChestGui.openInventories.remove(p.getUniqueId());
    }
}
