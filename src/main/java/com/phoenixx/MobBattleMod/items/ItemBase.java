package com.phoenixx.MobBattleMod.items;

import com.phoenixx.MobBattleMod.MobBattleMod;
import com.phoenixx.MobBattleMod.init.ModItems;
import com.phoenixx.MobBattleMod.util.IHasModel;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemBase extends Item implements IHasModel {

	public ItemBase(String name)
	{
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(CreativeTabs.MATERIALS);
		
		ModItems.ITEMS.add(this);
	}
	
	@Override
	public void registerModels() 
	{
		MobBattleMod.proxy.registerItemRenderer(this, 0, "inventory");
	}
}
