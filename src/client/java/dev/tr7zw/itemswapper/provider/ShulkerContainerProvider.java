package dev.tr7zw.itemswapper.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import dev.tr7zw.itemswapper.ItemSwapperSharedMod;
import dev.tr7zw.itemswapper.api.AvailableSlot;
import dev.tr7zw.itemswapper.api.client.ContainerProvider;
import dev.tr7zw.itemswapper.config.ConfigHolder;
import dev.tr7zw.itemswapper.util.ShulkerHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Client-side provider used to open shulker contents in the inventory UI. */
public class ShulkerContainerProvider implements ContainerProvider {

    @Override
    public Set<Item> getItemHandlers() {
        return ShulkerHelper.getShulkerItems();
    }

    @Override
    public List<AvailableSlot> processItemStack(ItemStack itemStack, Item item, boolean limit, int slotId) {
        if (!areShulkersEnabled()) {
            return Collections.emptyList();
        }
        List<ItemStack> shulkerItems = ShulkerHelper.getItems(itemStack);
        List<AvailableSlot> slots = new ArrayList<>();
        if (shulkerItems != null) {
            for (int x = 0; x < shulkerItems.size(); x++) {
                if (shulkerItems.get(x).getItem() == item) {
                    slots.add(new AvailableSlot(slotId, x, shulkerItems.get(x)));
                    if (limit) {
                        return slots;
                    }
                }
            }
        }
        return slots;
    }

    @Override
    public NonNullList<AvailableSlot> getItemStacks(ItemStack itemStack, int slotId) {
        if (!areShulkersEnabled()) {
            return NonNullList.create();
        }
        List<ItemStack> shulkerItems = ShulkerHelper.getItems(itemStack);
        NonNullList<AvailableSlot> slots = NonNullList.create();
        if (shulkerItems != null) {
            for (int x = 0; x < shulkerItems.size(); x++) {
                slots.add(new AvailableSlot(slotId, x, shulkerItems.get(x)));
            }
        }
        return slots;
    }

    private static boolean areShulkersEnabled() {
        return ItemSwapperSharedMod.instance.getSessionSettings().isEnableShulkers()
                && !ConfigHolder.getInstance().getGeneral().getConfig().disableShulkers;
    }

}
