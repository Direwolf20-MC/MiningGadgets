package com.direwolf20.mininggadgets.client.screens;

import com.direwolf20.mininggadgets.client.screens.widget.UpgradeScrollingList;
import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.containers.ModificationTableContainer;
import com.direwolf20.mininggadgets.common.network.PacketHandler;
import com.direwolf20.mininggadgets.common.network.packets.PacketExtractUpgrade;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

public class ModificationTableScreen extends ContainerScreen<ModificationTableContainer> {
    private ResourceLocation GUI = new ResourceLocation(MiningGadgets.MOD_ID, "textures/gui/modificationtable.png");
    private BlockPos tePos;
    private UpgradeScrollingList scrollingUpgrades;

    public ModificationTableScreen(ModificationTableContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        this.tePos = container.getTE().getPos();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);

        this.scrollingUpgrades.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        getMinecraft().getTextureManager().bindTexture(GUI);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.blit(relX - 23, relY, 0, 0, this.xSize + 23, this.ySize);
    }

    @Override
    public void init() {
        super.init();

        this.scrollingUpgrades = new UpgradeScrollingList(Minecraft.getInstance(), this.xSize - 14, 72, guiTop + 7, guiLeft + 7, this, upgrade -> {
            PacketHandler.sendToServer(new PacketExtractUpgrade(this.tePos, upgrade.getName(), upgrade.getName().length()));
        });

        this.children.add(this.scrollingUpgrades);
   }
}
