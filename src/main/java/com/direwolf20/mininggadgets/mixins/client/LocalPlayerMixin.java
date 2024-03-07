package com.direwolf20.mininggadgets.mixins.client;

import com.direwolf20.mininggadgets.common.items.MiningGadget;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Debug(export = true)
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @Shadow public abstract InteractionHand getUsedItemHand();

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"))
    public boolean $mg_onAiStepUsingItemSlowDown(LocalPlayer instance) {
        ItemStack itemInHand = ((LivingEntity) (Object) this).getItemInHand(this.getUsedItemHand());
        if (itemInHand.getItem() instanceof MiningGadget) return false;
        return instance.isUsingItem();
    }
}

