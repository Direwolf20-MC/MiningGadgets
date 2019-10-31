package com.direwolf20.mininggadgets.client.screens;

import com.direwolf20.mininggadgets.client.screens.widget.ToggleButton;
import com.direwolf20.mininggadgets.common.gadget.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.gadget.upgrade.UpgradeTools;
import com.direwolf20.mininggadgets.common.network.PacketHandler;
import com.direwolf20.mininggadgets.common.network.Packets.PacketUpdateUpgrade;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MiningSettingScreen extends Screen {
    private ItemStack gadget;
    private List<Upgrade> gadgetUpgrades = new ArrayList<>();

    public MiningSettingScreen(ItemStack gadget) {
        super(new StringTextComponent("title"));

        this.gadget = gadget;
        this.gadgetUpgrades.addAll(UpgradeTools.getUpgrades(this.gadget));
    }

    @Override
    protected void init() {
        int x = 0;
        List<Upgrade> toggleableList = this.gadgetUpgrades.stream().filter(Upgrade::isToggleable).collect(Collectors.toList());
        for (Upgrade upgrade : toggleableList) {
            addButton(new ToggleButton((width / 2) - (160 / 2), ((height / 2) - ((toggleableList.size() * 25) / 2)) + (x * 25), 50, I18n.format(upgrade.getLocal()).replace(I18n.format(upgrade.getLocalReplacement()), ""), shouldUpdate -> {
                // When the button is clicked we toggle
                if( shouldUpdate ) {
                    upgrade.setEnabled(!upgrade.isEnabled());
                    updateUpgrade(upgrade);
                }

                // When we're just init the gui, we check if it's on or off.
                return upgrade.isEnabled();
            }));

            x += 1;
        }
    }

    private void updateUpgrade(Upgrade upgrade) {
        PacketHandler.sendToServer(new PacketUpdateUpgrade(upgrade.getName()));
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


    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
