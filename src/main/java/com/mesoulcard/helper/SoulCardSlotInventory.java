package com.mesoulcard.helper;

import appeng.api.inventories.BaseInternalInventory;
import appeng.api.upgrades.IUpgradeInventory;
import com.mesoulcard.core.ModConstants;
import com.mesoulcard.core.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class SoulCardSlotInventory extends BaseInternalInventory implements IUpgradeInventory {
    private final ItemLike upgradableItem;
    private final Runnable changeCallback;
    private ItemStack stack = ItemStack.EMPTY;

    public SoulCardSlotInventory(ItemLike upgradableItem, Runnable changeCallback) {
        this.upgradableItem = upgradableItem;
        this.changeCallback = changeCallback;
    }

    @Override
    public ItemLike getUpgradableItem() {
        return this.upgradableItem;
    } // which pattern provider owns the upgrade

    @Override
    public int getInstalledUpgrades(ItemLike upgrade) {
        return !this.stack.isEmpty() && this.stack.is(upgrade.asItem()) ? this.stack.getCount() : 0;
    }

    @Override
    public int getMaxInstalled(ItemLike upgrade) {
        return upgrade == Registration.SOUL_CARD.get() ? ModConstants.SOUL_CARD_PATTERN_PROVIDER_UPGRADE_LIMIT : 0;
    }

    @Override
    public void readFromNBT(CompoundTag data, String subtag, HolderLookup.Provider registries) {
        this.stack = ItemStack.EMPTY;

        if (!data.contains(subtag, Tag.TAG_LIST)) {
            return;
        }

        var tagList = data.getList(subtag, Tag.TAG_COMPOUND);
        for (var itemTag : tagList) {
            var itemCompound = (CompoundTag) itemTag;
            if (itemCompound.getInt("Slot") == 0) {
                this.stack = ItemStack.parseOptional(registries, itemCompound);
                if (!this.stack.isEmpty()) {
                    this.stack.setCount(Math.min(this.stack.getCount(), this.getSlotLimit(0)));
                }
                return;
            }
        }
    }

    @Override
    public void writeToNBT(CompoundTag data, String subtag, HolderLookup.Provider registries) {
        if (this.stack.isEmpty()) {
            data.remove(subtag);
            return;
        }

        var items = new ListTag();
        var itemTag = new CompoundTag();
        itemTag.putInt("Slot", 0);
        items.add(this.stack.save(registries, itemTag));
        data.put(subtag, items);
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public int getSlotLimit(int slot) {
        return ModConstants.SOUL_CARD_PATTERN_PROVIDER_UPGRADE_LIMIT;
    }

    @Override
    public ItemStack getStackInSlot(int slotIndex) {
        return slotIndex == 0 ? this.stack : ItemStack.EMPTY;
    }

    @Override
    public void setItemDirect(int slotIndex, ItemStack stack) {
        if (slotIndex != 0) {
            return;
        }

        var newStack = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
        if (!newStack.isEmpty()) {
            newStack.setCount(Math.min(newStack.getCount(), this.getSlotLimit(slotIndex)));
        }

        this.stack = newStack;
        this.changeCallback.run();
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return slot == 0 && stack.is(Registration.SOUL_CARD.get());
    }
}
