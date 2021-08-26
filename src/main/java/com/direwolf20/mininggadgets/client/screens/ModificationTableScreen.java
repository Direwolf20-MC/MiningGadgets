package com.direwolf20.mininggadgets.client.screens;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.containers.ModificationTableContainer;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.UpgradeCard;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeTools;
import com.direwolf20.mininggadgets.common.network.PacketHandler;
import com.direwolf20.mininggadgets.common.network.packets.PacketExtractUpgrade;
import com.direwolf20.mininggadgets.common.network.packets.PacketInsertUpgrade;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraftforge.client.gui.ScrollPanel;
import net.minecraftforge.fmllegacy.ForgeI18n;

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
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);

        this.scrollingUpgrades.render(stack, mouseX, mouseY, partialTicks);
        this.renderTooltip(stack, mouseX, mouseY); // @mcp: renderTooltip = renderHoveredToolTip

        int relX = (this.width) / 2;
        int relY = (this.height) / 2;

        drawCenteredString(stack, font, ForgeI18n.getPattern(String.format("%s.%s", MiningGadgets.MOD_ID, "text.modification_table")), relX, relY - 100, 0xFFFFFF);

        if (this.container.getUpgradesCache().size() == 0) {
            String string = ForgeI18n.getPattern(String.format("%s.%s", MiningGadgets.MOD_ID, "text.empty_table_helper"));
            String[] parts = string.split("\n");
            for (int i = 0; i < parts.length; i++) {
                drawScaledCenteredString(stack, (relX + 17) - (font.width(parts[0]) / 2), (relY - 68) + (i * font.lineHeight), .8f, parts[i], 0xFFFFFF);
            }
        }
    }

    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY) {
    }

    private void drawScaledCenteredString(PoseStack matrices, int x, int y, float scale, String textComponent, int color) {
        matrices.pushPose();
        matrices.translate(x, y, 0);
        matrices.scale(scale, scale, scale);
        drawString(matrices, font, textComponent, 0, 0, color);
        matrices.popPose();
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        getMinecraft().getTextureManager().bindForSetup(GUI);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        this.blit(stack, relX - 23, relY, 0, 0, this.imageWidth + 23, this.imageHeight);
    }

    @Override
    public void init() {
        super.init();

        this.scrollingUpgrades = new ScrollingUpgrades(Minecraft.getInstance(), this.imageWidth - 14, 72, topPos + 7, leftPos + 7, this);
        this.addRenderableWidget(this.scrollingUpgrades);
   }

    @Override
    public boolean mouseClicked(double mouseXIn, double mouseYIn, int p_231044_5_) {
        ItemStack heldStack = this.playerInventory.getSelected();
        ItemStack gadget = this.container.slots.get(0).getItem();
        if (!gadget.isEmpty() && gadget.getItem() instanceof MiningGadget && !heldStack.isEmpty() && heldStack.getItem() instanceof UpgradeCard) {
            if (scrollingUpgrades.isMouseOver(mouseXIn, mouseYIn)) {
                // Send packet to remove the item from the inventory and add it to the table
                if (UpgradeTools.containsUpgrade(gadget, ((UpgradeCard) heldStack.getItem()).getUpgrade())) {
                    return false;
                }

                PacketHandler.sendToServer(new PacketInsertUpgrade(this.tePos, heldStack));
                playerInventory.setPickedItem(ItemStack.EMPTY);
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
        protected void drawPanel(PoseStack mStack, int entryRight, int relativeY, Tesselator tess, int mouseX, int mouseY) {
            Upgrade currentUpgrade = null;
            int x = (entryRight - this.width) + 3;
            int y = relativeY;

            int index = 0;
            for (Upgrade upgrade : this.parent.container.getUpgradesCache()) {
                Minecraft.getInstance().getItemRenderer().renderGuiItem(new ItemStack(upgrade.getCard()), x, y);

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
        public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
            super.render(stack, mouseX, mouseY, partialTicks);

            if( this.upgrade != null  )
                this.parent.renderTooltip(stack, Lists.transform(this.upgrade.getStack().getTooltipLines(this.parent.getMinecraft().player, TooltipFlag.Default.NORMAL), Component::getVisualOrderText), mouseX, mouseY);
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
