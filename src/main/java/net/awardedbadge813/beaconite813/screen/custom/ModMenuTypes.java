package net.awardedbadge813.beaconite813.screen.custom;

import net.awardedbadge813.beaconite813.beaconite813;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, beaconite813.MOD_ID);




    public static final DeferredHolder<MenuType<?>, MenuType<RefineryMenu>> REFINERY_MENU =
            registerMenuType("refinery_menu", RefineryMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<UnstableBeaconMenu>> UNSTABLE_BEACON_MENU =
            registerMenuType("unstable_beacon_menu", UnstableBeaconMenu::new);
    public static final DeferredHolder<MenuType<?>, MenuType<ConstructorMenu>> CONSTRUCTOR_MENU =
            registerMenuType("constructor_menu", ConstructorMenu::new);






    private static<T extends AbstractContainerMenu>DeferredHolder<MenuType<?>, MenuType<T>>
    registerMenuType(String name, IContainerFactory<T> pFactory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(pFactory));
    }


    public static void register(IEventBus eventbus) {
        MENUS.register(eventbus);
    }
}
