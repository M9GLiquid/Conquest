package eu.kingconquest.conquest.listener;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import eu.kingconquest.conquest.util.ChestGui;
import eu.kingconquest.conquest.util.Validate;


public class ChestGuiListener implements Listener{

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onClick(InventoryClickEvent e){
		if (!(e.getWhoClicked() instanceof Player))
			return;
		if (Validate.notNull(e.getCurrentItem())){
			if (Validate.notNull(e.getCurrentItem().getData().getItemType())){
				if (e.getCurrentItem().getData().getItemType().equals(Material.AIR.name())){ // if item is air, return
					e.setCancelled(true);
					return;
				}
			}
		}
		Player p = (Player) e.getWhoClicked();
		UUID inventoryUUID = ChestGui.openInventories.get(p.getUniqueId());
		if (Validate.notNull(inventoryUUID)){
			e.setCancelled(true);
			ChestGui GUI = ChestGui.getInventoriesByUUID().get(inventoryUUID);
			GUI.setClickType(e.getClick());
			if (Validate.notNull(GUI.getActions().get(e.getSlot()))){
				ChestGui.onGuiAction action = GUI.getActions().get(e.getSlot());
				if (Validate.notNull(action))
					action.onClick(p);
			}
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent e){
		Player p = (Player) e.getPlayer();
		ChestGui.openInventories.remove(p.getUniqueId());
	}
}
