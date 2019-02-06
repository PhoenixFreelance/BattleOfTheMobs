package com.phoenixx.MobBattleMod.proxy;

import net.minecraft.item.Item;

public class CommonProxy 
{
	public void registerItemRenderer(Item item, int meta, String id) {}

	public boolean isSinglePlayer()
	{
		return false;
	}

	public boolean isGamePaused()
	{
		return false;
	}
}
