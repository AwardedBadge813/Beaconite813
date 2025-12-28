package net.awardedbadge813.beaconite813.block.custom;

import com.mojang.serialization.MapCodec;
import net.awardedbadge813.beaconite813.entity.ModBlockEntities;
import net.awardedbadge813.beaconite813.entity.RegalBeaconBlockEntity;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class RegalBeaconBlock extends BaseEntityBlock {
    public RegalBeaconBlock(Properties properties) {
        super(properties);
    }
    public static final MapCodec<RegalBeaconBlock> CODEC = simpleCodec(RegalBeaconBlock::new);

    public @NotNull MapCodec<RegalBeaconBlock> codec() {
        return CODEC;
    }



    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new RegalBeaconBlockEntity(pos, state);
    }

    @Override
    protected void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean movedByPiston) {
        if(state.getBlock()!=newState.getBlock()) {
            if(level.getBlockEntity(pos) instanceof RegalBeaconBlockEntity regalBeaconBlockEntity) {
                regalBeaconBlockEntity.drops();
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }

    }
//
    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        if(Screen.hasShiftDown()){
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.regal_beacon1.tooltip"));
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.regal_beacon2.tooltip"));
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.regal_beacon3.tooltip"));
        }
        if (Screen.hasControlDown()){
            tooltipComponents.add(Component.translatable("item.beaconite813.midas_rot_effect_icon"));
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.midas_rot_effect.tooltip"));
            tooltipComponents.add(Component.translatable("item.beaconite813.hypertrophy_effect_icon"));
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.hypertrophy_effect.tooltip"));
        }
        if(!Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.shift.tooltip"));
        }
        if(!Screen.hasControlDown()) {
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.ctrl.tooltip"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        if(level.isClientSide()) {
            return null;
        }

        return createTickerHelper(blockEntityType, ModBlockEntities.REGAL_BEACON_BE.get(),
                (level1, blockPos, blockState, blockEntity) -> blockEntity.tick(level1, blockPos, blockState));

    }


    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack pStack, @NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos,
                                                       @NotNull Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHitResult) {
        if(!pLevel.isClientSide()){
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if(entity instanceof RegalBeaconBlockEntity regalBeaconBlockEntity) {
                pPlayer.openMenu(new SimpleMenuProvider(regalBeaconBlockEntity, Component.literal("Regal Beacon")), pPos);
            } else {
                throw new IllegalStateException("Container Provider Missing");
            }
        }
        return ItemInteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }
}
