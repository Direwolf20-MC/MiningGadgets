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
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.client.gui.widget.ScrollPanel;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jspecify.annotations.NonNull;


public class ModificationTableScreen extends AbstractContainerScreen<ModificationTableContainer> {
    private Identifier GUI = Identifier.fromNamespaceAndPath(MiningGadgets.MOD_ID, "textures/gui/modificationtable.png");
    private BlockPos tePos;
    private ModificationTableContainer container;
    private ScrollingUpgrades scrollingUpgrades;


    public ModificationTableScreen(ModificationTableContainer container, Inventory inv, Component name) {
        super(container, inv, name);
        this.tePos = container.getTE().getBlockPos();
        this.container = container;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);

        this.scrollingUpgrades.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);

        int relX = (this.width) / 2;
        int relY = (this.height) / 2;

        guiGraphics.text(font, Component.translatable(MiningGadgets.MOD_ID + ".text.modification_table"), relX, relY - 100, 0xFFFFFF);

        if (this.container.getUpgradesCache().size() == 0) {
            String string = Component.translatable(MiningGadgets.MOD_ID + ".text.modification_table").getString();
            String[] parts = string.split("\n");
            for (int i = 0; i < parts.length; i++) {
                drawScaledCenteredString(guiGraphics, (relX + 17) - (font.width(parts[0]) / 2), (relY - 68) + (i * font.lineHeight), .8f, parts[i], 0xFFFFFF);
            }
        }
    }

    @Override
    protected void extractLabels(@NonNull GuiGraphicsExtractor graphics, int xm, int ym) {}

    private void drawScaledCenteredString(GuiGraphicsExtractor guiGraphics, int x, int y, float scale, String textComponent, int color) {
//        PoseStack matrices = guiGraphics.pose();
//        guiGraphics.pose().pushMatrix();
//        guiGraphics.pose().translate(x, y);
//        guiGraphics.pose().scaleAround(scale, scale, scale);
//        matrices.scale(scale, scale, scale);
        guiGraphics.text(font, textComponent, 0, 0, color);
//        guiGraphics.pose().pushMatrix();
    }


    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, relX - 23, relY, 0, 0, this.imageWidth + 23, this.imageHeight, 256, 256);
    }

    @Override
    public void init() {
        super.init();

        this.scrollingUpgrades = new ScrollingUpgrades(Minecraft.getInstance(), this.imageWidth - 14, 72, topPos + 7, leftPos + 7, this);
        this.addRenderableWidget(this.scrollingUpgrades);
   }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        ItemStack heldStack = this.menu.getCarried();
        ItemStack gadget = this.container.slots.get(0).getItem();
        if (!gadget.isEmpty() && gadget.getItem() instanceof MiningGadget && !heldStack.isEmpty() && heldStack.getItem() instanceof UpgradeCard) {
            if (scrollingUpgrades.isMouseOver(event.x(), event.y())) {
                // Send packet to remove the item from the inventory and add it to the table
                if (UpgradeTools.containsUpgrade(gadget, ((UpgradeCard) heldStack.getItem()).getUpgrade())) {
                    return false;
                }

                ClientPacketDistributor.sendToServer(new InsertUpgradePayload(this.tePos, heldStack));
                this.menu.setCarried(ItemStack.EMPTY);
            }
        }
        return super.mouseClicked(event, doubleClick);
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
        protected void drawPanel(GuiGraphicsExtractor guiGraphics, int entryRight, int relativeY, int mouseX, int mouseY) {
            Upgrade currentUpgrade = null;
            int x = (entryRight - this.width) + 3;
            int y = relativeY;

            int index = 0;
            for (Upgrade upgrade : this.parent.container.getUpgradesCache()) {
                guiGraphics.item(new ItemStack(upgrade.getCardItem().get()), x, y);

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
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            if( !isMouseOver(event.x(), event.y()) || this.upgrade == null )
                return false;

            ClientPacketDistributor.sendToServer(new ExtractUpgradePayload(this.parent.tePos, this.upgrade.getName(), this.upgrade.getName().length()));
            return super.mouseClicked(event, doubleClick);
        }

        @Override
        public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
            super.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);

            if(this.upgrade != null)
                guiGraphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, new ItemStack(this.upgrade.getCardItem().get())
                                .getTooltipLines(Item.TooltipContext.EMPTY, this.parent.getMinecraft().player, TooltipFlag.Default.NORMAL), mouseX, mouseY);
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
