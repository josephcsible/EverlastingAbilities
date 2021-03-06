package org.cyclops.everlastingabilities.ability;

import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.world.World;
import org.cyclops.cyclopscore.helper.MinecraftHelpers;

import java.util.List;

/**
 * Ability type for fertility.
 * @author rubensworks
 */
public class AbilityTypeFertility extends AbilityTypeDefault {

    private static final int TICK_MODULUS = MinecraftHelpers.SECOND_IN_TICKS;

    public AbilityTypeFertility(String id, int rarity, int maxLevel, int baseXpPerLevel) {
        super(id, rarity, maxLevel, baseXpPerLevel);
    }

    protected int getDurationMultiplier() {
        return 3;
    }

    @Override
    public void onTick(EntityPlayer player, int level) {
        World world = player.world;
        if (!world.isRemote && player.world.getWorldTime() % TICK_MODULUS == 0) {
            int radius = level * 2;
            List<EntityAnimal> mobs = world.getEntitiesWithinAABB(EntityAnimal.class,
                    player.getEntityBoundingBox().grow(radius, radius, radius), EntitySelectors.NOT_SPECTATING);
            for (EntityAnimal animal : mobs) {
                animal.setInLove(player);
            }
        }
    }
}
