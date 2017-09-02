package org.cyclops.everlastingabilities.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.item.ItemAbilityTotem;
import org.cyclops.everlastingabilities.item.ItemAbilityTotemConfig;

import java.util.Random;

public class TotemRecycleRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    private final Random rand = new Random();
    private long seed = rand.nextLong();
    
    @Override
    public boolean matches(InventoryCrafting invCrafting, World world) {
    
        int inputCount = 0;
        for (int i = 0; i < invCrafting.getSizeInventory(); i++) {
            ItemStack slot = invCrafting.getStackInSlot(i);
            if (!slot.isEmpty()) {
                if (slot.getItem() instanceof ItemAbilityTotem) {
                    inputCount++;
                }
                else {
                    // non-totem item found in recipe
                    return false;
                }
            }
        }
        return inputCount == ItemAbilityTotemConfig.totemCraftingCount;
    }
    
    @Override
    public ItemStack getCraftingResult(InventoryCrafting invCrafting) {
        // Item is being taken out of crafting grid.

        // Select one of the inputs at random, and use its rarity for the rarity of the output.
        int inputIndex = 0;
        rand.setSeed(seed);
        int inputTargetIndex = rand.nextInt(ItemAbilityTotemConfig.totemCraftingCount);
        EnumRarity rarity = EnumRarity.COMMON;

        for (int i = 0; i < invCrafting.getSizeInventory(); i++) {
            ItemStack slot = invCrafting.getStackInSlot(i);
            if (!slot.isEmpty()) {
                if (slot.getItem() instanceof ItemAbilityTotem) {
                    if (inputIndex >= inputTargetIndex) {
                        rarity = ItemAbilityTotem.getInstance().getRarity(slot);
                        break;
                    }
                    inputIndex++;
                }
                else {
                    // non-totem item found in recipe
                    // this should never happen because matches() will return false.
                    // We should probably throw() here.
                    return ItemStack.EMPTY;
                }
            }
        }

        // 20% chance of a bump
        if (rarity.ordinal() < EnumRarity.EPIC.ordinal() && rand.nextInt(100) < ItemAbilityTotemConfig.totemCraftingRarityIncreasePercent) {
            rarity = EnumRarity.values()[rarity.ordinal()+1];
        }

        return AbilityHelpers.getRandomTotem(rarity, rand);
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= ItemAbilityTotemConfig.totemCraftingCount;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(ItemAbilityTotem.getInstance());
    }
    
    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        seed++;
        return ForgeHooks.defaultRecipeGetRemainingItems(inv);
    }
}
