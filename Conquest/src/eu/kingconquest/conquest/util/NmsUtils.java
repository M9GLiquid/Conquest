package eu.kingconquest.conquest.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

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
            Method nmsItemMethod= nmsItemStackClass.getDeclaredMethod("d");
            nmsItemMethod.invoke(nmsItemClass, size);
            nmsItemStackField.setAccessible(true);
            nmsItemStackField.set(nmsItemStackClass, nmsItemClass);
            return stack;
        }catch(Exception e){
        	e.printStackTrace();
            return stack;
        }
    }
}

