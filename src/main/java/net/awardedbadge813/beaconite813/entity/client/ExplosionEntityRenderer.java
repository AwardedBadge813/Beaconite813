package net.awardedbadge813.beaconite813.entity.client;

import net.awardedbadge813.beaconite813.beaconite813;
import net.awardedbadge813.beaconite813.entity.custom.ExplosionEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ExplosionEntityRenderer extends EntityRenderer<ExplosionEntity> {
        // In our constructor, we just forward to super.

    public ExplosionEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ExplosionEntity explosionEntity) {
        return ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/entity/boom/boom.png");
    }
}
