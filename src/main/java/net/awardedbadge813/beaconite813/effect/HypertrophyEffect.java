package net.awardedbadge813.beaconite813.effect;

import com.jcraft.jorbis.Block;
import net.awardedbadge813.beaconite813.Config;
import net.awardedbadge813.beaconite813.util.BeaconiteLib;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.*;

public class HypertrophyEffect extends MobEffect {



    public HypertrophyEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity livingEntity, int amplifier) {

        if (BeaconiteLib.effectDisabled(ModEffects.HYPERTROPHY.value())) {
            livingEntity.removeEffect(ModEffects.HYPERTROPHY);
        }
        //the entity will drop something anyway, this logic adds another loot roll instance to the death tick.
        Level thisLevel = livingEntity.level();
        if (livingEntity.isDeadOrDying() && !thisLevel.isClientSide()) {


            BlockPos lootPos = livingEntity.getOnPos();

            LootParams.Builder builder = new LootParams.Builder((ServerLevel) thisLevel);
            //the following is provided by Neoforge documentation.

            builder.withParameter(LootContextParams.ORIGIN, livingEntity.getEyePosition());
            //this makes player drops available
            builder.withOptionalParameter(LootContextParams.ATTACKING_ENTITY, ((ServerLevel) thisLevel).getRandomPlayer());
            //we want the chance of getting good items to be higher, since you did spend diamonds/emeralds/a lot of lapis on this.
            builder.withLuck((amplifier+1)*2);

            LootParams params = builder.create(LootContextParamSets.EMPTY);

            ResourceKey<LootTable> lootTableKey = livingEntity.getLootTable();
            LootTable table = Objects.requireNonNull(thisLevel.getServer()).reloadableRegistries().getLootTable(lootTableKey);





            //if the amplifier is high, you can get bonus rolls bc im so nice.
            int rollsAmount = (int) (max(random() * amplifier, 0))* (Config.ALLOW_EXTRA_ROLLS.get()? 1:0)+1;

            //the arraylist to container makes the loot rolls happen at once instead of exploding.
            ArrayList<ItemStack> items = new ArrayList<>();
            for (int j=0; j<rollsAmount; j++) {
                List<ItemStack> lootRoll = table.getRandomItems(params);
                items.addAll(lootRoll);
            }
            Container droppingContainer = new SimpleContainer(items.size());
            for (int slot = 0; slot<items.size(); slot++) {
                droppingContainer.setItem(slot, items.get(slot));
            }
            Containers.dropContents(thisLevel, lootPos, droppingContainer);
            livingEntity.removeEffect(ModEffects.HYPERTROPHY);
        }
        return super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
