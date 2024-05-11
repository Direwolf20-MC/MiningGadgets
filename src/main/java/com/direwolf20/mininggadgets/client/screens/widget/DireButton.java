package com.direwolf20.mininggadgets.client.screens.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class DireButton extends Button {

    public DireButton(int x, int y, int widthIn, int heightIn, Component buttonText, OnPress action) {
        super(new Builder(buttonText, action)
                .pos(x, y)
                .size(widthIn, heightIn));
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            Font fontrenderer = Minecraft.getInstance().font;
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            this.isHovered = isMouseOver(mouseX, mouseY);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            guiGraphics.blitSprite(SPRITES.get(this.active, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight());

            int j = 14737632;

            if (this.packedFGColor != 0) {
                j = this.packedFGColor;
            } else if (!this.active) {
                j = 10526880;
            } else if (this.isHovered) {
                j = 16777120;
            }

            guiGraphics.drawCenteredString(fontrenderer, this.getMessage().getString(), this.getX() + this.width / 2, this.getY() + (this.height - 7) / 2, j);
        }
    }
}
