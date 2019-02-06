package com.phoenixx.MobBattleMod.init;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModRecipes {

	public static void init() {
		GameRegistry.addSmelting(ModBlocks.BATTLE_BLOCK, new ItemStack(Blocks.DIAMOND_BLOCK, 2), 3.0f);
	}
}
