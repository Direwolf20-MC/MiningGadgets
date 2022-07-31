package com.direwolf20.mininggadgets.client;

import com.direwolf20.mininggadgets.client.screens.ModificationTableScreen;
import com.direwolf20.mininggadgets.common.Config;
import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@MethodsReturnNonnullByDefault
@SuppressWarnings("unused")
@JeiPlugin
public class MiningGadgetsJEI implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(MiningGadgets.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        IIngredientSubtypeInterpreter<ItemStack> chargedProvider = (stack, uid) -> {
            if (!(stack.getItem() instanceof MiningGadget)) {
                return IIngredientSubtypeInterpreter.NONE;
            }

            double energy = stack.getOrCreateTag().getDouble("energy");
            if (energy == 0) {
                return "empty";
            } else if (energy == Config.MININGGADGET_MAXPOWER.get()) {
                return "charged";
            }

            return IIngredientSubtypeInterpreter.NONE;
        };

        registration.registerSubtypeInterpreter(ModItems.MININGGADGET.get(), chargedProvider);
        registration.registerSubtypeInterpreter(ModItems.MININGGADGET_SIMPLE.get(), chargedProvider);
        registration.registerSubtypeInterpreter(ModItems.MININGGADGET_FANCY.get(), chargedProvider);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(ModificationTableScreen.class, new ModificationTableContainerHandler());
    }

    private static class ModificationTableContainerHandler implements IGuiContainerHandler<ModificationTableScreen> {
        @Override
        public List<Rect2i> getGuiExtraAreas(ModificationTableScreen containerScreen) {
            return new ArrayList<>(Collections.singleton(new Rect2i((containerScreen.width / 2) - 120, (containerScreen.height / 2) - 5, 25, 35)));
        }
    }
}
