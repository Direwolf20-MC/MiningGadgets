package com.direwolf20.mininggadgets.client.gui;

import com.direwolf20.mininggadgets.MiningGadgets;
import com.direwolf20.mininggadgets.common.containers.ModificationTableContainer;
import com.direwolf20.mininggadgets.common.network.PacketHandler;
import com.direwolf20.mininggadgets.common.network.Packets.PacketExtractUpgrade;
import com.direwolf20.mininggadgets.common.network.Packets.PacketInsertUpgrade;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

public class ModificationTableScreen extends ContainerScreen<ModificationTableContainer> {

    private DireButton buttonInsert, buttonExtract;

    private ResourceLocation GUI = new ResourceLocation(MiningGadgets.MOD_ID, "textures/gui/modificationtable.png");
    private BlockPos tePos;

    public ModificationTableScreen(ModificationTableContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        this.tePos = container.getTE().getPos();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.blit(relX, relY, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void init() {
        super.init();
        buttonInsert = addButton(createAndAddButton(27, 5, 14, 10, "<-", (button) -> PacketHandler.sendToServer(new PacketInsertUpgrade(tePos))));
        buttonExtract = addButton(createAndAddButton(27, 15, 14, 10, "->", (button) -> PacketHandler.sendToServer(new PacketExtractUpgrade(tePos))));
    }

    private DireButton createAndAddButton(int x, int y, int witdth, int height, String text, Button.IPressable action) {
        DireButton button = new DireButton(guiLeft + x, guiTop + y, witdth, height, text, action);
        return button;
    }
}
