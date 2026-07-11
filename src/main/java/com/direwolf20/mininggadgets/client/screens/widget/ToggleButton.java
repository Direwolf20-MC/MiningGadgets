package com.direwolf20.mininggadgets.client.screens.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class ToggleButton extends AbstractWidget {
    private Predicate<Boolean> onPress;
    private boolean enabled;
    private Identifier texture;

    public ToggleButton(int xIn, int yIn, Component msg, Identifier texture, Predicate<Boolean> onPress) {
        super(xIn, yIn, 21, 26, msg);

        this.onPress = onPress;
        this.texture = texture;

        this.enabled = this.onPress.test(false);
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        Color activeColor = this.enabled ? Color.GREEN : Color.RED;

        guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, ((this.enabled ? 0x68000000 : 0x9B000000)) + activeColor.getRGB());

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, this.getX() +2, this.getY() + 5, 0, 0, 16, 16, 16, 16);
    }

    public List<Component> getOurTooltip() {
        return List.of(
                this.getMessage(),
                Component.literal("Enabled: " + this.enabled)
                        .withStyle(this.enabled ? ChatFormatting.GREEN : ChatFormatting.RED)
        );
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        this.enabled = !this.enabled;
        this.onPress.test(true);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput p_259858_) {

    }
}
