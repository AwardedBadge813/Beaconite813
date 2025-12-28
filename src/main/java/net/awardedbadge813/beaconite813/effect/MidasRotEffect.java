package net.awardedbadge813.beaconite813.effect;

import com.ibm.icu.text.Normalizer2;
import net.awardedbadge813.beaconite813.Config;
import net.awardedbadge813.beaconite813.damagesource.ModDamageTypes;
import net.awardedbadge813.beaconite813.util.BeaconiteLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.max;
import static java.lang.Math.random;

public class MidasRotEffect extends MobEffect {



    public MidasRotEffect(MobEffectCategory category, int color) {
        super(category, color);
    }


    @Override
    public boolean applyEffectTick(@NotNull LivingEntity livingEntity, int amplifier) {

        if (BeaconiteLib.effectDisabled(ModEffects.MIDAS_ROT.value())) {
            livingEntity.removeEffect(ModEffects.MIDAS_ROT);
        }

        //the entity will drop something anyway, this logic adds another loot roll instance to the death tick.
        Level thisLevel = livingEntity.level();
        if (livingEntity.level().getGameTime()%(max((int)40L/max(amplifier, 1), 1L))==0L && !thisLevel.isClientSide() && !livingEntity.isDeadOrDying()) {
            livingEntity.hurt(new DamageSource(livingEntity.level().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ModDamageTypes.MIDAS_ROT)), 2);
            BlockPos lootPos = livingEntity.getOnPos();
            //making a custom loot table roll bc im lazy as hell and can't bother making one in the datagen.
            //I also can't be bothered to do a reverse Z-table so this probably isn't the optimal way to do the math.
            ArrayList<ItemStack> lootRoll = getMidasLootRoll();
            Container droppingContainer = new SimpleContainer(lootRoll.size());
            for (int i = 0; i < lootRoll.size(); i++) {
                droppingContainer.setItem(i, lootRoll.get(i));
            }
            Containers.dropContents(thisLevel, lootPos, droppingContainer);
        }
        return super.applyEffectTick(livingEntity, amplifier);
    }

    public ArrayList<ItemStack> getMidasLootRoll() {
        int base = Config.MIDAS_ROT_LOOT_BASE.get();
        ArrayList<ItemStack> toReturn = new ArrayList<>();
        for(Item item : BeaconiteLib.midasLootEntries) {
            //hopefully prevents a ton of hashmap queries?
            int maxAmount = BeaconiteLib.midasLootAmounts.get(item);
            double weight = (double) BeaconiteLib.midasLootWeights.get(item) / base;
            int actualAmount = 0;
            //want to roll each item independently.
            for(int i=0; i<maxAmount; i++) {
                if (random()<=weight) {
                    actualAmount++;
                }
            }
            if (actualAmount>0) {
                toReturn.add(new ItemStack(item, actualAmount));
            }

        }
        return toReturn;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
