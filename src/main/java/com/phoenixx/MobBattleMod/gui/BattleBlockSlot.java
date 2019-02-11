package com.phoenixx.MobBattleMod.gui;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class BattleBlockSlot extends SlotItemHandler {

    public BattleBlockSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        /*if (stack.getItem() == Items.SPAWN_EGG) {
            return true;
        } else {
            return false;
        }*/

        return true;
    }
}