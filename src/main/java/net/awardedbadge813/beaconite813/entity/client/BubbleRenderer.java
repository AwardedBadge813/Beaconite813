package net.awardedbadge813.beaconite813.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.awardedbadge813.beaconite813.beaconite813;
import net.awardedbadge813.beaconite813.entity.custom.BubbleEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BubbleRenderer extends MobRenderer<BubbleEntity, BubbleModel<BubbleEntity>> {
    public BubbleRenderer(EntityRendererProvider.Context context) {
        super(context, new BubbleModel<>(context.bakeLayer(BubbleModel.LAYER_LOCATION)), 0f);
    }

    @Override
    public ResourceLocation getTextureLocation(BubbleEntity bubbleEntity) {
        return ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/entity/bubble/bubble.png");
    }

    @Override
    public void render(BubbleEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.scale(5f, 5f, 5f);
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
