package eu.kingconquest.conquest.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Pagination{
    private int limit = 45;
	private int items = 0;
	private int pageNr = 0;
	private int currentItem =  0;
	
	public Pagination(){
	}
    
	public int getOffset() {
		return (pageNr + 1) * limit;
	}
	
	public int getLimit() {
		return limit;
	}
	
	public int getPageNr(){
		return pageNr;
	}
	
	private void setPageNr(int pageNr){
		this.pageNr = pageNr;
	}
	
	public int getItems() {
		return this.items;
	}

	public void setItems(int items){
		this.items = items;
	}
	
	public int getCurrentItem(){
		return this.currentItem;
	}

	public void setCurrentItem(int currentItem){
		this.currentItem = currentItem;
	}
	
	public void next(ChestGui parent){
		if (!(getItems() > getOffset()))
			return;
		parent.setItem(5, new ItemStack(Material.CHORUS_FRUIT_POPPED), player -> {
			setPageNr(getPageNr() + 1);
			parent.display();
		}, "§2>>","§1-----------------"
				+ "\n§4 Page " + (getPageNr() + 1) + " >>"
				);
	}
	
	public void previous(ChestGui parent){
		if (getPageNr() < 1)
			return;
		parent.setItem(3, new ItemStack(Material.CHORUS_FRUIT), player -> {
			setPageNr(getPageNr() - 1);
    		setCurrentItem(getPageNr() * getLimit());
			parent.display();
		}, "§2<<","§1-----------------"
				+ "\n§4 << Page "+ (getPageNr() - 1) 
				);
	}
}
