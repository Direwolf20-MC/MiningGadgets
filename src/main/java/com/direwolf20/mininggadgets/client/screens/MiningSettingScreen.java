package com.direwolf20.mininggadgets.client.screens;

import com.direwolf20.mininggadgets.MiningGadgets;
import com.direwolf20.mininggadgets.client.screens.widget.ToggleButton;
import com.direwolf20.mininggadgets.common.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.gadget.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.gadget.upgrade.UpgradeTools;
import com.direwolf20.mininggadgets.common.network.PacketHandler;
import com.direwolf20.mininggadgets.common.network.packets.PacketChangeMiningSize;
import com.direwolf20.mininggadgets.common.network.packets.PacketChangeRange;
import com.direwolf20.mininggadgets.common.network.packets.PacketUpdateUpgrade;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class MiningSettingScreen extends Screen implements GuiSlider.ISlider {
    private ItemStack gadget;
    private Button sizeButton;
    private Button visualButton;
    private int beamRange = 0;

    public MiningSettingScreen(ItemStack gadget) {
        super(new StringTextComponent("title"));

        this.gadget = gadget;
        this.beamRange = MiningProperties.getBeamRange(gadget);
    }

    @Override
    protected void init() {
        int baseX = width / 2, baseY = height / 2;

        // Filters out the non-toggleable options
        List<Upgrade> toggleableList = UpgradeTools.getUpgrades(this.gadget).stream().filter(Upgrade::isToggleable).collect(Collectors.toList());

        // Remove 6 from x to center it as the padding on the right pushes off center... (I'm a ui nerd)
        int index = 0, x = baseX - (((toggleableList.size() * 30) / 2) - 6);
        for (Upgrade upgrade : toggleableList) {
            addButton(new ToggleButton(x + (index * 30), baseY + 40, UpgradeTools.getName(upgrade), new ResourceLocation(MiningGadgets.MOD_ID, "textures/item/upgrade_" + upgrade.getName() + ".png"), send -> this.toggleUpgrade(upgrade, send)));

            // Spaces the upgrades
            index ++;
        }

        sizeButton = new Button(baseX - (150 / 2), baseY - 50, 150, 20, String.format("Size: %1$d x %1$d", MiningProperties.getRange(gadget)), (button) -> {
            if( MiningProperties.getRange(gadget) == 1 )
                button.setMessage(String.format("Size: %1$d x %1$d", 3));
            else
                button.setMessage(String.format("Size: %1$d x %1$d", 1));

            PacketHandler.sendToServer(new PacketChangeMiningSize());
        });

        addButton(sizeButton);
        addButton(new GuiSlider(baseX - (150 / 2), baseY - 25, 150, 20, "Range: ", "", 0, MiningProperties.getBeamMaxRange(gadget), this.beamRange, false, true, s -> {}, this));

        visualButton = new Button(baseX - (150 / 2), baseY - 0, 150, 20, "Open Visuals Menu", (button) -> {
            ModScreens.openVisualSettingsScreen(gadget);
        });
        addButton(visualButton);

        // Button logic
        if( !UpgradeTools.containsActiveUpgrade(gadget, Upgrade.THREE_BY_THREE) )
            sizeButton.active = false;
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
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);

        drawCenteredString(getMinecraft().fontRenderer, "Mining Gadget", (width / 2), (height / 2) - 70, Color.WHITE.getRGB());
        drawCenteredString(getMinecraft().fontRenderer, "Toggle Upgrades", (width / 2), (height / 2) + 20, Color.WHITE.getRGB());

        this.children.forEach(e -> {
            if( !(e instanceof ToggleButton) )
                return;

            ToggleButton btn = ((ToggleButton) e);
            if( mouseX > btn.x && mouseX < btn.x + btn.getWidth() && mouseY > btn.y && mouseY < btn.y + btn.getHeight() )
                renderTooltip(btn.getTooltip(), mouseX, (height / 2) + 90);
        });
    }

    public void openFilters() {
        //        player.openContainer(new MiningContainer.MiningProvider(itemstack));
//        NetworkHooks.openGui((ServerPlayerEntity) player, new MiningContainer.MiningProvider(itemstack), (data) -> {
//            data.writeItemStack(itemstack);
//        });
//        if (!world.isRemote && player instanceof ServerPlayerEntity)
//            NetworkHooks.openGui((ServerPlayerEntity) player, new MiningContainer.MiningProvider());
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        super.onClose();
        PacketHandler.sendToServer(new PacketChangeRange(this.beamRange));
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        this.beamRange = slider.getValueInt();
    }
}
