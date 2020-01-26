package com.direwolf20.mininggadgets.client.screens.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class ToggleButton extends Widget {
    private Predicate<Boolean> onPress;
    private boolean enabled;
    private ResourceLocation texture;

    public ToggleButton(int xIn, int yIn, String msg, ResourceLocation texture, Predicate<Boolean> onPress) {
        super(xIn, yIn, msg);

        this.width = 21;
        this.height = 26;
        this.onPress = onPress;
        this.texture = texture;

        this.enabled = this.onPress.test(false);
    }

    @Override
    public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        Color activeColor = this.enabled ? Color.GREEN : Color.RED;

        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param, GlStateManager.SourceFactor.ONE.param, GlStateManager.DestFactor.ZERO.param);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param);

        GlStateManager.disableTexture();
        GlStateManager.color4f(activeColor.getRed() / 255f, activeColor.getGreen() / 255f, activeColor.getBlue() / 255f, this.enabled ? .4f : .6f);
        blit(this.x, this.y, 0, 0, this.width, this.height);
        GlStateManager.enableTexture();

        GlStateManager.color4f(1f, 1f, 1f, 1f);
        Minecraft.getInstance().getTextureManager().bindTexture(texture);
        blit(this.x +2, this.y + 5, 0, 0, 16, 16, 16, 16);
    }

    public List<String> getTooltip() {
        return Arrays.asList(this.getMessage(), new StringTextComponent("Enabled: " + this.enabled).setStyle(new Style().setColor(this.enabled ? TextFormatting.GREEN : TextFormatting.RED)).getFormattedText());
    }

    @Override
    public void onClick(double p_onClick_1_, double p_onClick_3_) {
        this.onPress.test(true);
        this.enabled = !this.enabled;
    }
}
