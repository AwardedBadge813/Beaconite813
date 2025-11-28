package net.awardedbadge813.beaconite813.block.custom;

import com.mojang.serialization.MapCodec;
import net.awardedbadge813.beaconite813.entity.LivingBeaconBlockEntity;
import net.awardedbadge813.beaconite813.entity.ModBlockEntities;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;

public class LivingBeaconBlock  extends BaseEntityBlock {
    public LivingBeaconBlock(Properties properties) {
        super(properties);
    }
    public static final MapCodec<LivingBeaconBlock> CODEC = simpleCodec(LivingBeaconBlock::new);

    public MapCodec<LivingBeaconBlock> codec() {
        return CODEC;
    }



    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LivingBeaconBlockEntity(pos, state);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if(state.getBlock()!=newState.getBlock()) {
            if(level.getBlockEntity(pos) instanceof LivingBeaconBlockEntity livingBeaconBlockEntity) {
                livingBeaconBlockEntity.drops();
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }

    }
//
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if(Screen.hasShiftDown()){
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.living_beacon1.tooltip"));
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.living_beacon2.tooltip"));

            tooltipComponents.add(Component.translatable("tooltip.beaconite813.living_beacon3.tooltip"));
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.living_beacon4.tooltip"));
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.living_beacon5.tooltip"));
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.living_beacon6.tooltip"));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.shift.tooltip"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if(level.isClientSide()) {
            return null;
        }

        return createTickerHelper(blockEntityType, ModBlockEntities.LIVING_BEACON_BE.get(),
                (level1, blockPos, blockState, blockEntity) -> blockEntity.tick(level1, blockPos, blockState));

    }


    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos,
                                              Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        if(!pLevel.isClientSide()){
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if(entity instanceof LivingBeaconBlockEntity livingBeaconBlockEntity) {
                ((ServerPlayer) pPlayer).openMenu(new SimpleMenuProvider(livingBeaconBlockEntity, Component.literal("Living Beacon")), pPos);
            } else {
                throw new IllegalStateException("Container Provider Missing");
            }
        }
        return ItemInteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
