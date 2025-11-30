package net.awardedbadge813.beaconite813;

import net.awardedbadge813.beaconite813.block.ModBlocks;
import net.awardedbadge813.beaconite813.entity.ModBlockEntities;
import net.awardedbadge813.beaconite813.entity.ModEntities;
import net.awardedbadge813.beaconite813.entity.client.BubbleRenderer;
import net.awardedbadge813.beaconite813.entity.client.ExplosionEntityRenderer;
import net.awardedbadge813.beaconite813.entity.custom.BasicBeaconRenderer;
import net.awardedbadge813.beaconite813.item.ModCreativeModeTabs;
import net.awardedbadge813.beaconite813.item.ModItems;
import net.awardedbadge813.beaconite813.recipe.ModRecipes;
import net.awardedbadge813.beaconite813.screen.custom.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(beaconite813.MOD_ID)
public class beaconite813 {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "beaconite813";
    public static final Logger LOGGER = LogUtils.getLogger();
    //This mod was made from the NeoForged ModdevGradle template.
    //Since I am a hoarder I have kept the example code used to make block/items, which is what you see below.
    // Feel free to ignore anything inside the /*  */.

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
        public static class ClientModEvents {
            @SubscribeEvent
            public static void registerScreens(RegisterMenuScreensEvent event) {
                event.register(ModMenuTypes.REFINERY_MENU.get(), RefineryScreen::new);
                event.register(ModMenuTypes.UNSTABLE_BEACON_MENU.get(), UnstableBeaconScreen::new);
                event.register(ModMenuTypes.CONSTRUCTOR_MENU.get(), ConstructorScreen::new);
                event.register(ModMenuTypes.LIVING_BEACON_MENU.get(), LivingBeaconScreen::new);
            }

            @SubscribeEvent
            public static void onClientSetup(FMLClientSetupEvent event) {
                EntityRenderers.register(ModEntities.EXPLOSION.get(), ExplosionEntityRenderer::new);
                EntityRenderers.register(ModEntities.BUBBLE.get(), BubbleRenderer::new);
                BlockEntityRenderers.register(ModBlockEntities.LIVING_BEACON_BE.get(), BasicBeaconRenderer::new);
                BlockEntityRenderers.register(ModBlockEntities.REFINERY_BE.get(), BasicBeaconRenderer::new);
                BlockEntityRenderers.register(ModBlockEntities.CONSTRUCTOR_BE.get(), BasicBeaconRenderer::new);
                BlockEntityRenderers.register(ModBlockEntities.UNSTABLE_BEACON_BE.get(), BasicBeaconRenderer::new);

            }


        }



    /*
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    // Creates a new Block with the id "examplemod:example_block", combining the namespace and path
    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    // Creates a new BlockItem with the id "examplemod:example_block", combining the namespace and path
    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);

    // Creates a new food item with the id "examplemod:example_id", nutrition 1 and saturation 2
    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
     public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.beaconite813")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(EXAMPLE_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
            }).build());
    */

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public beaconite813(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // register modded items, block, etc. to the register.
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModEntities.register(modEventBus);
        ModRecipes.register(modEventBus);








        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (beaconite813) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        //NeoForge.EVENT_BUS.register(this);



        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("Beaconite Mod is Here!");

        //if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
        //    LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        //}

        LOGGER.info("{}{}", Config.EXPLOSION_RADIUS.get(), Config.EXPLOSION_RADIUS.getAsInt());

        //Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));




    }

    // Add the example block item to the building block tab





    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("Loading Beaconite Mod-I'm Active!");
    }
 /*
    GENERAL CREDITS :

    Credit to Kaupenjoe for most of the underlying codebase, thank you! extremely helpful resource for turning a bunch of funny words into code that works.
    mostly used for registries & setup (the 1.21.1 modding tutorial), within which I modified the code to actually suit this mod instead of make bismuth.

    Also credit to Kaupenjoe for the general GUI design, not sure where it came from but used the style of the pedestal GUI for all GUIs in the mod.
    I believe this is also credit to mojang since that GUI is based on vanilla?

    credit to Desieben for the ItemStackHandler QuickStack code, should be additionally credited whenever the code is used, but I also wanted to include it here for comprehensivity.


 */
}
