package com.phoenixx.MobBattleMod.init;

import com.phoenixx.MobBattleMod.blocks.BattleBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import java.util.ArrayList;
import java.util.List;

public class ModBlocks 
{
	public static final List<Block> BLOCKS = new ArrayList<Block>();
	
	//Blocks
	public static final Block BATTLE_BLOCK = new BattleBlock("battle_block", Material.IRON);
	
}
