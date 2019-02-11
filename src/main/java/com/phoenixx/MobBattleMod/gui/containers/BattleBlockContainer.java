package com.phoenixx.MobBattleMod.gui.containers;

import com.phoenixx.MobBattleMod.blocks.tileEntities.BattleBlockTileEntity;
import com.phoenixx.MobBattleMod.gui.BattleBlockSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class BattleBlockContainer extends Container
{
    public BattleBlockContainer(InventoryPlayer inventoryPlayer, BattleBlockTileEntity battleBlockTileEntity)
    {
        if (battleBlockTileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH)) {
            IItemHandler inventory = battleBlockTileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);

            int slotCounter = 0;

            // Team One Inventory
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 4; x++) {
                    this.addSlotToContainer(new BattleBlockSlot(inventory, slotCounter, 9 + x * 18, 28 + y * 18));
                    slotCounter++;
                }
            }

            // Team Two Inventory
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 4; x++) {
                    this.addSlotToContainer(new BattleBlockSlot(inventory, slotCounter, 97 + x * 18, 28 + y * 18));
                    slotCounter++;
                }
            }

            // PLAYER HOT-BAR INVENTORY
            for (int x = 0; x < 9; x++) {
                this.addSlotToContainer(new Slot(inventoryPlayer, x, 8 + x * 18, 142));
            }
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack();
            stack = stackInSlot.copy();

            int containerSlots = 24;

            if (index < containerSlots) {
                if (!this.mergeItemStack(stackInSlot, containerSlots, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(stackInSlot, 0, containerSlots, false)) {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            slot.onTake(player, stackInSlot);

        }
        return stack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return true;
    }
}