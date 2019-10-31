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

        this.width = 200;
        this.onPress = onPress;

        this.enabled = this.onPress.test(false);
    }

    @Override
    public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
//        super.renderButton(p_renderButton_1_, p_renderButton_2_, p_renderButton_3_);

        GlStateManager.disableTexture();
        GlStateManager.color4f(0, 0, 0, 1f);
        this.blit(this.x, this.y, 0, 0, 40, 22);

        GlStateManager.color4f(1f, 1f, 1f, 1f);
        this.blit(this.enabled ? this.x + 20 : this.x, this.y, 0, 0, 20, 20);
        GlStateManager.enableTexture();

        drawString(Minecraft.getInstance().fontRenderer, getMessage(), this.x, this.y, Color.WHITE.getRGB());
    }

    @Override
    public void onClick(double p_onClick_1_, double p_onClick_3_) {

        System.out.println("button clicked");
        this.enabled = this.onPress.test(true);
    }
}
