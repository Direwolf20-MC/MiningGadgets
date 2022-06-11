package com.direwolf20.mininggadgets.client.screens;

import com.direwolf20.mininggadgets.common.containers.FilterContainer;
import com.direwolf20.mininggadgets.common.containers.GhostSlot;
import com.direwolf20.mininggadgets.common.network.PacketHandler;
import com.direwolf20.mininggadgets.common.network.packets.PacketGhostSlot;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

/**
 * Complete props and thanks to @amadones for their awesome implementation of this system
 * and their help whilst implementing it :heart:
 */
public class FilterScreen extends AbstractContainerScreen<FilterContainer> {
    // Stealing the normal chest gui, should make this a tad simpler.
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

    public FilterScreen(FilterContainer container, Inventory inv, Component name) {
        super(container, inv, name);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);

        this.renderTooltip(stack, mouseX, mouseY); // @mcp: renderTooltip = renderHoveredToolTip
    }

    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) {
        font.draw(stack, Component.translatable("mininggadgets.tooltip.single.filters").getString(), 8, 6, 4210752);
        font.draw(stack, this.menu.getCarried().getDisplayName().getString(), 8, (this.imageHeight - 96 + 3), 4210752);
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        // Stolen from minecraft chests :D
        this.blit(stack, x, y, 0, 0, this.imageWidth, 71);
        this.blit(stack, x, y + 71, 0, 126, this.imageWidth, 96);
    }

    @Override
    public boolean mouseClicked(double x, double y, int btn) {
        if (hoveredSlot == null || !(hoveredSlot instanceof GhostSlot))
            return super.mouseClicked(x, y, btn);

        // By splitting the stack we can get air easily :) perfect removal basically
        ItemStack stack = this.menu.getCarried();// getMinecraft().player.inventoryMenu.getCarried();
        stack = stack.copy().split(hoveredSlot.getMaxStackSize()); // Limit to slot limit
        hoveredSlot.set(stack); // Temporarily update the client for continuity purposes

        PacketHandler.sendToServer(new PacketGhostSlot(hoveredSlot.index, stack));
        return true;
    }

    @Override
    public boolean mouseReleased(double x, double y, int btn) {
        if (hoveredSlot == null || !(hoveredSlot instanceof GhostSlot))
            return super.mouseReleased(x, y, btn);

        return true;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double amt) {
        if (hoveredSlot == null || !(hoveredSlot instanceof GhostSlot))
            return super.mouseScrolled(x, y, amt);

        return true;
    }
}
