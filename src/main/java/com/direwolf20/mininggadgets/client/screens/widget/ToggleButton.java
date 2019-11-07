package com.direwolf20.mininggadgets.client.screens.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;

import java.util.function.Predicate;

public class ToggleButton extends Widget {
    private Predicate<Boolean> onPress;
    private boolean enabled = false;

    public ToggleButton(int xIn, int yIn, int width, String msg, Predicate<Boolean> onPress) {
        super(xIn, yIn, msg);

        this.width = 25;
        this.height = 25;
        this.onPress = onPress;

        this.enabled = this.onPress.test(false);
    }

    @Override
    public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        Minecraft.getInstance().getTextureManager().bindTexture(WIDGETS_LOCATION);

        if( enabled )
            GlStateManager.color4f(0f, 1f, 0f, .5f);
        else
            GlStateManager.color4f(1f, 1f, 1f, .5f);

        this.blit(this.x, this.y, 3, 46, this.width, this.height);
    }

    @Override
    public void onClick(double p_onClick_1_, double p_onClick_3_) {
        this.enabled = this.onPress.test(true);
    }
}
