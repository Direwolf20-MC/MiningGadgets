package com.direwolf20.mininggadgets.client.screens.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;

import java.awt.*;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class ToggleButton extends Widget {
    private Predicate<Boolean> onPress;
    private boolean enabled;
    private ResourceLocation texture;

    public ToggleButton(int xIn, int yIn, ITextComponent msg, ResourceLocation texture, Predicate<Boolean> onPress) {
        super(xIn, yIn, 21, 26, msg);

        this.onPress = onPress;
        this.texture = texture;

        this.enabled = this.onPress.test(false);
    }

    @Override
    public void renderButton(MatrixStack stack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        Color activeColor = this.enabled ? Color.GREEN : Color.RED;

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param, GlStateManager.SourceFactor.ONE.param, GlStateManager.DestFactor.ZERO.param);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.param, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.param);

        RenderSystem.disableTexture();
        RenderSystem.color4f(activeColor.getRed() / 255f, activeColor.getGreen() / 255f, activeColor.getBlue() / 255f, this.enabled ? .4f : .6f);
        blit(stack, this.x, this.y, 0, 0, this.width, this.height);
        RenderSystem.enableTexture();

        RenderSystem.color4f(1f, 1f, 1f, 1f);
        Minecraft.getInstance().getTextureManager().bindTexture(texture);
        blit(stack, this.x +2, this.y + 5, 0, 0, 16, 16, 16, 16);
    }

    public List<IReorderingProcessor> getTooltip() {
        return LanguageMap.getInstance().func_244260_a(Arrays.asList(this.getMessage(), new StringTextComponent("Enabled: " + this.enabled).mergeStyle(this.enabled ? TextFormatting.GREEN : TextFormatting.RED)));
    }

    @Override
    public void onClick(double p_onClick_1_, double p_onClick_3_) {
        this.enabled = !this.enabled;
        this.onPress.test(true);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
