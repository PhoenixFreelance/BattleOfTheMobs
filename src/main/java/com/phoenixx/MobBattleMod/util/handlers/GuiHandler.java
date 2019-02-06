package com.phoenixx.MobBattleMod.util.handlers;

import com.phoenixx.MobBattleMod.blocks.tileEntities.BattleBlockTileEntity;
import com.phoenixx.MobBattleMod.gui.BattleBlockGui;
import com.phoenixx.MobBattleMod.gui.containers.BattleBlockContainer;
import com.phoenixx.MobBattleMod.util.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if(ID == Reference.GUI_BATTLE_BLOCK) return new BattleBlockContainer(player.inventory, (BattleBlockTileEntity) world.getTileEntity(new BlockPos(x,y,z)));

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if(ID == Reference.GUI_BATTLE_BLOCK) {
            return new BattleBlockGui(player.inventory, (BattleBlockTileEntity) world.getTileEntity(new BlockPos(x, y, z)));
        }
        return null;
    }
}