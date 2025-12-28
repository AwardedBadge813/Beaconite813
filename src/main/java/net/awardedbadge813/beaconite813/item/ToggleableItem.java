package net.awardedbadge813.beaconite813.item;

import net.awardedbadge813.beaconite813.util.BeaconiteLib;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ToggleableItem  extends Item {
    public ToggleableItem(Properties properties) {
        super(properties);
    }

    public boolean isDisabled() {
        return !BeaconiteLib.masterToggleTable.contains(this);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (!BeaconiteLib.masterToggleTable.contains(this)) {
            tooltipComponents.add(Component.translatable("tooltip.beaconite813.disabled.tooltip"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
