package net.awardedbadge813.beaconite813.block.custom;

import com.mojang.serialization.MapCodec;
import net.awardedbadge813.beaconite813.entity.ModBlockEntities;
import net.awardedbadge813.beaconite813.entity.EtherealBeaconBlockEntity;
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

import java.util.List;

public class EtherealBeaconBlock extends BaseEntityBlock {
    public EtherealBeaconBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new EtherealBeaconBlockEntity(blockPos, blockState);
    }
    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }


    @Override
    protected void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean movedByPiston) {
        if(state.getBlock()!=newState.getBlock()) {
            if(level.getBlockEntity(pos) instanceof EtherealBeaconBlockEntity etherealBeaconBlockEntity) {
                etherealBeaconBlockEntity.drops();
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }

    }
    public static final MapCodec<EtherealBeaconBlock> CODEC = simpleCodec(EtherealBeaconBlock::new);

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack pStack, @NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos,
                                                       @NotNull Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHitResult) {
        if(!pLevel.isClientSide()){
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if(entity instanceof EtherealBeaconBlockEntity etherealBeaconBlockEntity) {
                pPlayer.openMenu(new SimpleMenuProvider(etherealBeaconBlockEntity, Component.literal("Ethereal Beacon")), pPos);
            } else {
                throw new IllegalStateException("Container Provider Missing");
            }
        }
        return ItemInteractionResult.sidedSuccess(pLevel.isClientSide());
    }
    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        if(Screen.hasShiftDown()){
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.ethereal_beacon1.tooltip"));
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.ethereal_beacon2.tooltip"));
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.ethereal_beacon3.tooltip"));
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.ethereal_beacon4.tooltip"));
        }
        if (Screen.hasControlDown()){
            tooltipComponents.add(Component.translatable("item.beaconite813.auric_interference_icon"));
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.auric_interference.tooltip"));
        }
        if(!Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.shift.tooltip"));
        }
        if(!Screen.hasControlDown()) {
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.ctrl.tooltip"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    public  <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        if(level.isClientSide()) {
            return null;
        }

        return createTickerHelper(blockEntityType, ModBlockEntities.ETHER_BEACON_BE.get(),
                (level1, blockPos, blockState, blockEntity) -> blockEntity.tick(level1, blockPos, blockState));
    }
}
