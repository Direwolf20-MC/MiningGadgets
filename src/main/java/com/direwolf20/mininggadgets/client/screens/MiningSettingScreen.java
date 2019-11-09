package com.direwolf20.mininggadgets.client.screens;

import com.direwolf20.mininggadgets.MiningGadgets;
import com.direwolf20.mininggadgets.client.screens.widget.ToggleButton;
import com.direwolf20.mininggadgets.common.gadget.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.gadget.upgrade.UpgradeTools;
import com.direwolf20.mininggadgets.common.network.PacketHandler;
import com.direwolf20.mininggadgets.common.network.Packets.PacketUpdateUpgrade;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class MiningSettingScreen extends Screen {
    private ItemStack gadget;

    public MiningSettingScreen(ItemStack gadget) {
        super(new StringTextComponent("title"));

        this.gadget = gadget;
    }

    @Override
    protected void init() {
        // Filters out the non-toggleable options
        List<Upgrade> toggleableList = UpgradeTools.getUpgrades(this.gadget).stream().filter(Upgrade::isToggleable).collect(Collectors.toList());

        int index = 0, x = ((width / 2) - ((toggleableList.size() / 2) * 30));
        for (Upgrade upgrade : toggleableList) {
            addButton(new ToggleButton(
                    x + (index * 30),
                    (height / 2) + 40,
                    UpgradeTools.getName(upgrade),
                    new ResourceLocation(MiningGadgets.MOD_ID, "textures/item/upgrade_" + upgrade.getName() + ".png"),
                    send -> this.toggleUpgrade(upgrade, send)
            ));

            // Spaces the upgrades
            index ++;
        }
    }

    private boolean toggleUpgrade(Upgrade upgrade, boolean update) {
        // When the button is clicked we toggle
        if( update ) {
            upgrade.setEnabled(!upgrade.isEnabled());
            PacketHandler.sendToServer(new PacketUpdateUpgrade(upgrade.getName()));
        }

        // When we're just init the gui, we check if it's on or off.
        return upgrade.isEnabled();
    }

    @Override
    public void tick() {
//        if(!InputMappings.isKeyDown(getMinecraft().mainWindow.getHandle(), ClientSetup.gadgetMenu.getKey().getKeyCode())) {
//            onClose();
//        }

        super.tick();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);

        drawCenteredString(getMinecraft().fontRenderer, "Mining Gadget", (width / 2), (height / 2) - 75, Color.WHITE.getRGB());
        drawCenteredString(getMinecraft().fontRenderer, "Toggle Upgrades", (width / 2), (height / 2) + 25, Color.WHITE.getRGB());

        this.children.forEach(e -> {
            if( !(e instanceof ToggleButton) )
                return;

            ToggleButton btn = ((ToggleButton) e);
            if( mouseX > btn.x && mouseX < btn.x + btn.getWidth() && mouseY > btn.y && mouseY < btn.y + btn.getHeight() )
                renderTooltip(btn.getTooltip(), mouseX, (height / 2) + 90);
        });
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
