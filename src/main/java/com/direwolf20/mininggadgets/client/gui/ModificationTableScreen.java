package com.direwolf20.mininggadgets.client.gui;

import com.direwolf20.mininggadgets.MiningGadgets;
import com.direwolf20.mininggadgets.common.containers.ModificationTableContainer;
import com.direwolf20.mininggadgets.common.gadget.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.network.PacketHandler;
import com.direwolf20.mininggadgets.common.network.Packets.PacketExtractUpgrade;
import com.direwolf20.mininggadgets.common.network.Packets.PacketInsertUpgrade;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.gui.ScrollPanel;

public class ModificationTableScreen extends ContainerScreen<ModificationTableContainer> {

    private DireButton buttonInsert, buttonExtract;

    private ResourceLocation GUI = new ResourceLocation(MiningGadgets.MOD_ID, "textures/gui/modificationtable.png");
    private BlockPos tePos;
    private ModificationTableContainer container;
    private ScrollingUpgrades scrollingUpgrades;


    public ModificationTableScreen(ModificationTableContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        this.tePos = container.getTE().getPos();
        this.container = container;
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
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.blit(relX, relY, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void init() {
        super.init();

        this.scrollingUpgrades = new ScrollingUpgrades(Minecraft.getInstance(), this.xSize - 12, 50, guiTop + 30, guiLeft + 7, this);
        this.children.add(this.scrollingUpgrades);
        buttonInsert = addButton(createAndAddButton(27, 5, 14, 10, "<-", (button) -> PacketHandler.sendToServer(new PacketInsertUpgrade(tePos))));
        buttonExtract = addButton(createAndAddButton(27, 15, 14, 10, "->", (button) -> PacketHandler.sendToServer(new PacketExtractUpgrade(tePos))));
    }

    private DireButton createAndAddButton(int x, int y, int witdth, int height, String text, Button.IPressable action) {
        DireButton button = new DireButton(guiLeft + x, guiTop + y, witdth, height, text, action);
        return button;
    }

    private static class ScrollingUpgrades extends ScrollPanel {
        ModificationTableScreen parent;

        public ScrollingUpgrades(Minecraft client, int width, int height, int top, int left, ModificationTableScreen parent) {
            super(client, width, height, top, left);
            this.parent = parent;
        }

        @Override
        protected int getContentHeight() {
            return 60;
        }

        @Override
        protected void drawBackground() {

        }

        @Override
        protected void drawPanel(int entryRight, int relativeY, Tessellator tess, int mouseX, int mouseY) {
            int x = (entryRight - this.width) + 3;
            for (Upgrade upgrade : this.parent.container.getUpgradesCache()) {
                Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(new ItemStack(upgrade.getCard()), x, relativeY);
                x += 20;
            }
        }
    }
}
