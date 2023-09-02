package com.direwolf20.mininggadgets.client.screens.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            Font fontrenderer = Minecraft.getInstance().font;
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            this.isHovered = isMouseOver(mouseX, mouseY);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value);
            guiGraphics.blit(WIDGETS_LOCATION, this.getX(), this.getY(), 0, 46, this.width / 2, this.height);
            guiGraphics.blit(WIDGETS_LOCATION, this.getX() + this.width / 2, this.getY(), 200 - this.width / 2, 46, this.width / 2, this.height);


            int bottomToDraw = 2;
            guiGraphics.blit(WIDGETS_LOCATION, this.getX(), this.getY() + this.height - bottomToDraw, 0, 66 - bottomToDraw, this.width / 2, bottomToDraw);
            guiGraphics.blit(WIDGETS_LOCATION, this.getX() + this.width / 2, this.getY() + this.height - bottomToDraw, 200 - this.width / 2, 66 - bottomToDraw, this.width / 2, bottomToDraw);

            int j = 14737632;

            if (this.packedFGColor != 0) {
                j = this.packedFGColor;
            } else if (!this.active) {
                j = 10526880;
            } else if (this.isHovered) {
                j = 16777120;
            }

            guiGraphics.drawCenteredString(fontrenderer, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 7) / 2, j);
        }
    }
}
