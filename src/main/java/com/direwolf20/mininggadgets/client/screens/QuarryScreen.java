package com.direwolf20.mininggadgets.client.screens;

import com.direwolf20.mininggadgets.client.screens.widget.UpgradeScrollingList;
import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.containers.QuarryContainer;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public class QuarryScreen extends ContainerScreen<QuarryContainer> {
    private ResourceLocation GUI = new ResourceLocation(MiningGadgets.MOD_ID, "textures/gui/quarry_screen.png");

    private BlockPos tePos;
    private UpgradeScrollingList list;

    public QuarryScreen(QuarryContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        this.tePos = container.getTE().getPos();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);

        this.list.render(mouseX, mouseY, partialTicks);
        List<Upgrade> upgradesCache = this.container.getUpgradesCache();
        if( upgradesCache.size() > 0 )
            drawCenteredString(Minecraft.getInstance().fontRenderer, "Cache Created with " + upgradesCache.size() + " upgrades", 0, 0, 0xfff);
        else
            drawCenteredString(Minecraft.getInstance().fontRenderer, "No upgrades found :cry:", 0, 0, 0xfff);

        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        getMinecraft().getTextureManager().bindTexture(GUI);
        blit(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    public void init() {
        super.init();

        this.list = new UpgradeScrollingList(Minecraft.getInstance(), this.xSize - 57, 72, guiTop + 7, guiLeft + 50, this, upgrade -> {});

        this.children.add(this.list);
   }
}
