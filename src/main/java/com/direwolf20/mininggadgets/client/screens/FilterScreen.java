package com.direwolf20.mininggadgets.client.screens;

import com.direwolf20.mininggadgets.common.containers.FilterContainer;
import com.direwolf20.mininggadgets.common.containers.GhostSlot;
import com.direwolf20.mininggadgets.common.network.PacketHandler;
import com.direwolf20.mininggadgets.common.network.packets.PacketGhostSlot;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

/**
 * Complete props and thanks to @amadones for their awesome implementation of this system
 * and their help whilst implementing it :heart:
 */
public class FilterScreen extends ContainerScreen<FilterContainer> {
    // Stealing the normal chest gui, should make this a tad simpler.
    private static final ResourceLocation TEXTURE =  new ResourceLocation("textures/gui/container/generic_54.png");

    private ItemStack stack;

    public FilterScreen(FilterContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        font.drawString("Filters", 8, 6, 4210752);
        font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8, (this.ySize - 96 + 3), 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        getMinecraft().getTextureManager().bindTexture(TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;

        // Stolen from minecraft chests :D
        this.blit(x, y, 0, 0, this.xSize, 71);
        this.blit(x, y + 71, 0, 126, this.xSize, 96);
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
