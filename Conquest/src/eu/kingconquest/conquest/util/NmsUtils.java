package eu.kingconquest.conquest.util;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_11_R1.Item;

public class NmsUtils{
	
	private static Class<?> getNMSClass(String path, String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName(path + "." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
	
	public static ItemStack setItemStackSize(ItemStack stack, int size) {
        try {
        	Class<?> nmsItemStackClass = getNMSClass("net.minecraft.server", "ItemStack");
            Field nmsItemStackField = nmsItemStackClass.getDeclaredField("item");
            Class<?> nmsItemClass= nmsItemStackClass.getDeclaredMethod("getItem").getReturnType();
            Item instance = (Item) nmsItemClass.newInstance();
            Field nmsItemField = nmsItemClass.getDeclaredField("maxStackSize");
            nmsItemField.setAccessible(true);
            nmsItemField.setInt(instance, size);
            nmsItemStackField.setAccessible(true);
            nmsItemStackField.set(stack, instance);
            return stack;
        }catch(Exception e){
        	e.printStackTrace();
            return stack;
        }
    }
}

