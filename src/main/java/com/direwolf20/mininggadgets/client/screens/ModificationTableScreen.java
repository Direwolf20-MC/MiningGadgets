package com.direwolf20.mininggadgets.client.screens;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.containers.ModificationTableContainer;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.network.PacketHandler;
import com.direwolf20.mininggadgets.common.network.packets.PacketExtractUpgrade;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.gui.ScrollPanel;

public class ModificationTableScreen extends ContainerScreen<ModificationTableContainer> {
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
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);

        this.scrollingUpgrades.render(stack, mouseX, mouseY, partialTicks);
        this.func_230459_a_(stack, mouseX, mouseY); // @mcp: func_230459_a_ = renderHoveredToolTip
    }

    @Override
    protected void func_230451_b_(MatrixStack stack, int mouseX, int mouseY) { // @mcp: func_230451_b_ = drawGuiContainerForegroundLayer
        stack.push();
        stack.translate(xSize / 2f, guiTop - 70, 0);
        stack.scale(.8f, .8f, .8f);
        drawCenteredString(stack, font,new TranslationTextComponent(String.format("%s.%s", MiningGadgets.MOD_ID, "text.shift_click")), 0, 0, 0xFFFFFF);
        stack.pop();
    }

    @Override
    protected void func_230450_a_(MatrixStack stack, float partialTicks, int mouseX, int mouseY) { // @mcp: func_230450_a_ = drawGuiContainerBackgroundLayer
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        getMinecraft().getTextureManager().bindTexture(GUI);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.blit(stack, relX - 23, relY, 0, 0, this.xSize + 23, this.ySize);
    }

    @Override
    public void init() {
        super.init();

        this.scrollingUpgrades = new ScrollingUpgrades(Minecraft.getInstance(), this.xSize - 14, 72, guiTop + 7, guiLeft + 7, this);
        this.children.add(this.scrollingUpgrades);
   }

    private static class ScrollingUpgrades extends ScrollPanel {
        ModificationTableScreen parent;
        Upgrade upgrade = null;

        ScrollingUpgrades(Minecraft client, int width, int height, int top, int left, ModificationTableScreen parent) {
            super(client, width, height, top, left);
            this.parent = parent;
        }

        // Fixes a forge bug where the screen will screen when no scroll is available
        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
            if (this.getContentHeight() < this.height)
                return false;

            return super.mouseScrolled(mouseX, mouseY, scroll);
        }

        @Override
        protected int getContentHeight() {
            return (int) Math.ceil(this.parent.container.getUpgradesCache().size() / 7f) * 20;
        }

        @Override
        protected void drawPanel(MatrixStack mStack, int entryRight, int relativeY, Tessellator tess, int mouseX, int mouseY) {
            Upgrade currentUpgrade = null;
            int x = (entryRight - this.width) + 3;
            int y = relativeY;

            int index = 0;
            for (Upgrade upgrade : this.parent.container.getUpgradesCache()) {
                Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(new ItemStack(upgrade.getCard()), x, y);

                if( isMouseOver(mouseX, mouseY) && (mouseX > x && mouseX < x + 15 && mouseY > y && mouseY < y + 15)  )
                    currentUpgrade = upgrade;

                x += 22;
                index ++;
                if( index % 7 == 0 ) {
                    y += 20;
                    x = (entryRight - this.width) + 3;
                }
            }

            if(currentUpgrade == null || !currentUpgrade.equals(this.upgrade))
                this.upgrade = currentUpgrade;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if( !isMouseOver(mouseX, mouseY) || this.upgrade == null )
                return false;

            PacketHandler.sendToServer(new PacketExtractUpgrade(this.parent.tePos, this.upgrade.getName(), this.upgrade.getName().length()));
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
            super.render(stack, mouseX, mouseY, partialTicks);

            if( this.upgrade != null  )
                this.parent.renderTooltip(stack, this.upgrade.getStack().getTooltip(this.parent.getMinecraft().player, ITooltipFlag.TooltipFlags.NORMAL), mouseX, mouseY);
        }
    }
}
