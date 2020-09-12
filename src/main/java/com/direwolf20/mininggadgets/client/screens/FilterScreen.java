package com.direwolf20.mininggadgets.client.screens;

import com.direwolf20.mininggadgets.common.containers.FilterContainer;
import com.direwolf20.mininggadgets.common.containers.GhostSlot;
import com.direwolf20.mininggadgets.common.network.PacketHandler;
import com.direwolf20.mininggadgets.common.network.packets.PacketGhostSlot;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Complete props and thanks to @amadones for their awesome implementation of this system
 * and their help whilst implementing it :heart:
 */
public class FilterScreen extends ContainerScreen<FilterContainer> {
    // Stealing the normal chest gui, should make this a tad simpler.
    private static final ResourceLocation TEXTURE =  new ResourceLocation("textures/gui/container/generic_54.png");

    public FilterScreen(FilterContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(stack, mouseX, mouseY); // @mcp: func_230459_a_ = renderHoveredToolTip
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack stack, int mouseX, int mouseY) {
        font.drawString(stack, new TranslationTextComponent("mininggadgets.tooltip.single.filters").getString(), 8, 6, 4210752);
        font.drawString(stack, this.playerInventory.getDisplayName().getString(), 8, (this.ySize - 96 + 3), 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        getMinecraft().getTextureManager().bindTexture(TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;

        // Stolen from minecraft chests :D
        this.blit(stack, x, y, 0, 0, this.xSize, 71);
        this.blit(stack, x, y + 71, 0, 126, this.xSize, 96);
    }

    @Override
    public boolean mouseClicked(double x, double y, int btn) {
        if (hoveredSlot == null || !(hoveredSlot instanceof GhostSlot))
            return super.mouseClicked(x, y, btn);

        // By splitting the stack we can get air easily :) perfect removal basically
        ItemStack stack = getMinecraft().player.inventory.getItemStack();
        stack = stack.copy().split(hoveredSlot.getSlotStackLimit()); // Limit to slot limit
        hoveredSlot.putStack(stack); // Temporarily update the client for continuity purposes

        PacketHandler.sendToServer(new PacketGhostSlot(hoveredSlot.slotNumber, stack));
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
