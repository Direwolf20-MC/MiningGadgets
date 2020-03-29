package com.direwolf20.mininggadgets.client.screens.widget;

import com.direwolf20.mininggadgets.common.containers.MinerAcceptingContainer;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.gui.ScrollPanel;

import java.util.function.Consumer;

public class UpgradeScrollingList extends ScrollPanel {
    ContainerScreen<? extends MinerAcceptingContainer> parent;
    Upgrade upgrade = null;
    Consumer<Upgrade> onClick;

    public UpgradeScrollingList(Minecraft client, int width, int height, int top, int left, ContainerScreen<? extends MinerAcceptingContainer> parent, Consumer<Upgrade> onClick) {
        super(client, width, height, top, left);
        this.parent = parent;
        this.onClick = onClick;
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
        return (int) Math.ceil(this.parent.getContainer().getUpgradesCache().size() / 7f) * 20;
    }

    @Override
    protected void drawPanel(int entryRight, int relativeY, Tessellator tess, int mouseX, int mouseY) {
        int perRow = width / 22;

        Upgrade currentUpgrade = null;
        int x = (entryRight - this.width) + 3;
        int y = relativeY;

        int index = 0;
        for (Upgrade upgrade : this.parent.getContainer().getUpgradesCache()) {
            Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(new ItemStack(upgrade.getCard()), x, y);

            if( isMouseOver(mouseX, mouseY) && (mouseX > x && mouseX < x + 15 && mouseY > y && mouseY < y + 15)  )
                currentUpgrade = upgrade;

            x += 22;
            index ++;
            if( index % perRow == 0 ) {
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

        this.onClick.accept(this.upgrade);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if( this.upgrade != null  )
            this.parent.renderTooltip(I18n.format(this.upgrade.getLocal()), mouseX, mouseY);
    }
}
