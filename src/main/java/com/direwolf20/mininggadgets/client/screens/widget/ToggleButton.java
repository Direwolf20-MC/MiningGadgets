package com.direwolf20.mininggadgets.client.screens.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;

import java.awt.*;
import java.util.function.Predicate;

public class ToggleButton extends Widget {
    private Predicate<Boolean> onPress;
    private boolean enabled = false;

    public ToggleButton(int xIn, int yIn, int width, String msg, Predicate<Boolean> onPress) {
        super(xIn, yIn, msg);

        this.width = 160;
        this.height = 18;
        this.onPress = onPress;

        this.enabled = this.onPress.test(false);
    }

    @Override
    public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        GlStateManager.disableTexture();
        GlStateManager.color4f(0, 0, 0, 1f);
        this.blit(this.x, this.y, 0, 0, this.width, this.height);

        int right = this.x + this.width - 2;
        GlStateManager.color4f(.2f, .2f, .2f, 1f);
        this.blit(right - 29, this.y + 1, 0, 0, 30, this.height - 2);

        if( this.enabled )
            GlStateManager.color4f(0f, 1f, 0f, 1f);
        else
            GlStateManager.color4f(1f, 0f, 0f, 1f);

        this.blit(this.enabled ? right - 10 : right - 28, this.y + 2, 0, 0, 10, this.height - 4);
        GlStateManager.enableTexture();

        drawString(Minecraft.getInstance().fontRenderer, getMessage(), this.x + 6, this.y + (this.height / 2) - 4, Color.WHITE.getRGB());
    }

    @Override
    public void onClick(double p_onClick_1_, double p_onClick_3_) {

        System.out.println("button clicked");
        this.enabled = this.onPress.test(true);
    }
}
