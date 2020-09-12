package com.direwolf20.mininggadgets.client;

//import com.direwolf20.mininggadgets.common.Config;
//import com.direwolf20.mininggadgets.common.MiningGadgets;
//import com.direwolf20.mininggadgets.common.items.MiningGadget;
//import com.direwolf20.mininggadgets.common.items.ModItems;
//import mezz.jei.api.IModPlugin;
//import mezz.jei.api.JeiPlugin;
//import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
//import mezz.jei.api.registration.ISubtypeRegistration;
//import net.minecraft.util.ResourceLocation;
//
//@JeiPlugin
//public class MiningGadgetsJEI implements IModPlugin {
//    @Override
//    public ResourceLocation getPluginUid() {
//        return new ResourceLocation(MiningGadgets.MOD_ID, "jei_plugin");
//    }
//
//    @Override
//    public void registerItemSubtypes(ISubtypeRegistration registration) {
//        registration.registerSubtypeInterpreter(ModItems.MININGGADGET.get(), itemStack -> {
//            if(!(itemStack.getItem() instanceof MiningGadget))
//                return ISubtypeInterpreter.NONE;
//
//            double energy = itemStack.getOrCreateTag().getDouble("energy");
//            if (energy == 0)
//                return "empty";
//            else if (energy == Config.MININGGADGET_MAXPOWER.get())
//                return "charged";
//
//            return ISubtypeInterpreter.NONE;
//        });
//    }
//}
