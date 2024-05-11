package com.direwolf20.mininggadgets.client.screens;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.containers.ModificationTableContainer;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.UpgradeCard;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeTools;
import com.direwolf20.mininggadgets.common.network.data.ExtractUpgradePayload;
import com.direwolf20.mininggadgets.common.network.data.InsertUpgradePayload;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.client.gui.widget.ScrollPanel;
import net.neoforged.neoforge.network.PacketDistributor;


public class ModificationTableScreen extends AbstractContainerScreen<ModificationTableContainer> {
    private ResourceLocation GUI = new ResourceLocation(MiningGadgets.MOD_ID, "textures/gui/modificationtable.png");
    private BlockPos tePos;
    private ModificationTableContainer container;
    private Inventory playerInventory;
    private ScrollingUpgrades scrollingUpgrades;


    public ModificationTableScreen(ModificationTableContainer container, Inventory inv, Component name) {
        super(container, inv, name);
        this.tePos = container.getTE().getBlockPos();
        this.container = container;
        this.playerInventory = inv;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        // this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        this.scrollingUpgrades.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY); // @mcp: renderTooltip = renderHoveredToolTip

        int relX = (this.width) / 2;
        int relY = (this.height) / 2;

        //guiGraphics.drawCenteredString(font, ForgeI18n.getPattern(String.format("%s.%s", MiningGadgets.MOD_ID, "text.modification_table")), relX, relY - 100, 0xFFFFFF);
        guiGraphics.drawCenteredString(font, Component.translatable(MiningGadgets.MOD_ID + ".text.modification_table"), relX, relY - 100, 0xFFFFFF);

        if (this.container.getUpgradesCache().size() == 0) {
            String string = Component.translatable(MiningGadgets.MOD_ID + ".text.modification_table").getString();
            //String string = MiningGadgets.MOD_ID + ".text.empty_table_helper";
            String[] parts = string.split("\n");
            for (int i = 0; i < parts.length; i++) {
                drawScaledCenteredString(guiGraphics, (relX + 17) - (font.width(parts[0]) / 2), (relY - 68) + (i * font.lineHeight), .8f, parts[i], 0xFFFFFF);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    private void drawScaledCenteredString(GuiGraphics guiGraphics, int x, int y, float scale, String textComponent, int color) {
        PoseStack matrices = guiGraphics.pose();
        matrices.pushPose();
        matrices.translate(x, y, 0);
        matrices.scale(scale, scale, scale);
        guiGraphics.drawString(font, textComponent, 0, 0, color);
        matrices.popPose();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(GUI, relX - 23, relY, 0, 0, this.imageWidth + 23, this.imageHeight);
    }

    @Override
    public void init() {
        super.init();

        this.scrollingUpgrades = new ScrollingUpgrades(Minecraft.getInstance(), this.imageWidth - 14, 72, topPos + 7, leftPos + 7, this);
        this.addRenderableWidget(this.scrollingUpgrades);
   }

    @Override
    public boolean mouseClicked(double mouseXIn, double mouseYIn, int p_231044_5_) {
        ItemStack heldStack = this.menu.getCarried();
        ItemStack gadget = this.container.slots.get(0).getItem();
        if (!gadget.isEmpty() && gadget.getItem() instanceof MiningGadget && !heldStack.isEmpty() && heldStack.getItem() instanceof UpgradeCard) {
            if (scrollingUpgrades.isMouseOver(mouseXIn, mouseYIn)) {
                // Send packet to remove the item from the inventory and add it to the table
                if (UpgradeTools.containsUpgrade(gadget, ((UpgradeCard) heldStack.getItem()).getUpgrade())) {
                    return false;
                }

                PacketDistributor.sendToServer(new InsertUpgradePayload(this.tePos, heldStack));
                this.menu.setCarried(ItemStack.EMPTY);
            }
        }
        return super.mouseClicked(mouseXIn, mouseYIn, p_231044_5_);
    }

    private static class ScrollingUpgrades extends ScrollPanel implements NarratableEntry {
        ModificationTableScreen parent;
        Upgrade upgrade = null;



        ScrollingUpgrades(Minecraft client, int width, int height, int top, int left, ModificationTableScreen parent) {
            super(client, width, height, top, left);
            this.parent = parent;
        }

        // Fixes a forge bug where the screen will screen when no scroll is available
        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double scroll, double scrollY) {
            if (this.getContentHeight() < this.height)
                return false;

            return super.mouseScrolled(mouseX, mouseY, scroll, scrollY);
        }

        @Override
        protected int getContentHeight() {
            return (int) Math.ceil(this.parent.container.getUpgradesCache().size() / 7f) * 20;
        }

        @Override
        protected void drawPanel(GuiGraphics guiGraphics, int entryRight, int relativeY, Tesselator tess, int mouseX, int mouseY) {
            Upgrade currentUpgrade = null;
            int x = (entryRight - this.width) + 3;
            int y = relativeY;

            int index = 0;
            for (Upgrade upgrade : this.parent.container.getUpgradesCache()) {
                guiGraphics.renderItem(new ItemStack(upgrade.getCardItem().get()), x, y);

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

            PacketDistributor.sendToServer(new ExtractUpgradePayload(this.parent.tePos, this.upgrade.getName(), this.upgrade.getName().length()));
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
            super.render(guiGraphics, mouseX, mouseY, partialTicks);

            if( this.upgrade != null  )
                guiGraphics.renderTooltip(Minecraft.getInstance().font, Lists.transform(new ItemStack(this.upgrade.getCardItem().get()).getTooltipLines(Item.TooltipContext.EMPTY, this.parent.getMinecraft().player, TooltipFlag.Default.NORMAL), Component::getVisualOrderText), mouseX, mouseY);
        }

        @Override
        public NarrationPriority narrationPriority() {
            return NarrationPriority.NONE;
        }

        @Override
        public void updateNarration(NarrationElementOutput p_169152_) {

        }
    }
}
