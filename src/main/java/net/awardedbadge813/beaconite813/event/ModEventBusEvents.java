package net.awardedbadge813.beaconite813.event;


import net.awardedbadge813.beaconite813.beaconite813;
import net.awardedbadge813.beaconite813.entity.ModEntities;
import net.awardedbadge813.beaconite813.entity.client.BubbleModel;
import net.awardedbadge813.beaconite813.entity.client.BubbleRenderer;
import net.awardedbadge813.beaconite813.entity.custom.BubbleEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import org.checkerframework.checker.signature.qual.SignatureUnknown;

@EventBusSubscriber(modid= beaconite813.MOD_ID, bus=EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerLayers (EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BubbleModel.LAYER_LOCATION, BubbleModel::createBodyLayer);

    }

    @SubscribeEvent
    public static void registerattributes (EntityAttributeCreationEvent event) {

        event.put(ModEntities.BUBBLE.get(), BubbleEntity.createMobAttributes().build());

    }



}
