package com.direwolf20.mininggadgets.client.events;

import com.direwolf20.mininggadgets.MiningGadgets;
import com.direwolf20.mininggadgets.common.blocks.RenderBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MiningGadgets.MOD_ID, value = Dist.CLIENT)
public class EventDrawBlockHighlightEvent {

    @SubscribeEvent
    static void drawBlockHighlightEvent(DrawHighlightEvent evt) {
        Vec3d vec = evt.getTarget().getHitVec();
        if (Minecraft.getInstance().world.getBlockState(new BlockPos(vec.x, vec.y, vec.z)).getBlock() instanceof RenderBlock)
            evt.setCanceled(true);
    }
}
