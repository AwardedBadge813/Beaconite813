package net.awardedbadge813.beaconite813.block.custom;

import net.awardedbadge813.beaconite813.util.BeaconiteLib;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class ToggleableBlockItem  extends BlockItem {
    public ToggleableBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    public boolean isDisabled() {
        return !BeaconiteLib.masterToggleTable.contains(this.asItem());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (!BeaconiteLib.masterToggleTable.contains(this.asItem())) {
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.disabled.tooltip"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
