package com.direwolf20.mininggadgets.client.screens;

import com.direwolf20.mininggadgets.MiningGadgets;
import com.direwolf20.mininggadgets.client.screens.widget.ToggleButton;
import com.direwolf20.mininggadgets.common.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.gadget.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.gadget.upgrade.UpgradeTools;
import com.direwolf20.mininggadgets.common.network.PacketHandler;
import com.direwolf20.mininggadgets.common.network.packets.*;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MiningSettingScreen extends Screen implements GuiSlider.ISlider {
    private ItemStack gadget;
    private Button sizeButton;

    private int beamRange = 0;
    private int currentSize = 1;
    private boolean isWhitelist = true;
    private boolean isPrecision = true;
    private GuiSlider rangeSlider;
    private List<Upgrade> toggleableList = new ArrayList<>();

    public MiningSettingScreen(ItemStack gadget) {
        super(new StringTextComponent("title"));

        this.gadget = gadget;
        this.beamRange = MiningProperties.getBeamRange(gadget);
    }

    @Override
    protected void init() {
        int baseX = width / 2, baseY = height / 2;

        // Filters out the non-toggleable options
        toggleableList.clear();
        toggleableList = UpgradeTools.getUpgrades(this.gadget).stream().filter(Upgrade::isToggleable).collect(Collectors.toList());
        boolean containsVoid = UpgradeTools.containsUpgradeFromList(toggleableList, Upgrade.VOID_JUNK);

        isWhitelist = MiningProperties.getWhiteList(gadget);
        isPrecision = MiningProperties.getPrecisionMode(gadget);

        // Remove 6 from x to center it as the padding on the right pushes off center... (I'm a ui nerd)
        int index = 0, x = baseX + 10, y = baseY - (containsVoid ? 20 : 50);
        for (Upgrade upgrade : toggleableList) {
            addButton(new ToggleButton(x + (index * 30), y, UpgradeTools.getName(upgrade), new ResourceLocation(MiningGadgets.MOD_ID, "textures/item/upgrade_" + upgrade.getName() + ".png"), send -> this.toggleUpgrade(upgrade, send)));

            // Spaces the upgrades
            index ++;
            if( index % 4 == 0 ) {
                index = 0;
                y += 35;
            }
        }

        currentSize = MiningProperties.getRange(gadget);
        sizeButton = new Button(baseX - 135, baseY - 50, 115, 20, new TranslationTextComponent("mininggadgets.tooltip.screen.size", currentSize).getUnformattedComponentText(), (button) -> {
            currentSize = currentSize == 1 ? 3 : 1;
            button.setMessage(getTrans("tooltip.screen.size", currentSize));
            PacketHandler.sendToServer(new PacketChangeMiningSize());
        });

        rangeSlider = new GuiSlider(baseX - 135, baseY - 25, 115, 20, getTrans("tooltip.screen.range") + ": ", "", 1, MiningProperties.getBeamMaxRange(gadget), this.beamRange, false, true, s -> {}, this);

        addButton(sizeButton);
        addButton(rangeSlider);
        addButton(new Button(baseX - 135, baseY, 115, 20, getTrans("tooltip.screen.visuals_menu"), (button) -> {
            ModScreens.openVisualSettingsScreen(gadget);
        }));

        //Precision Mode
        addButton(new Button(baseX - 135, baseY + 25, 115, 20, getTrans("tooltip.screen.precision_mode", isPrecision), (button) -> {
            isPrecision = !isPrecision;
            button.setMessage(getTrans("tooltip.screen.precision_mode", isPrecision));
            PacketHandler.sendToServer(new PacketTogglePrecision());
        }));

        // Don't add if we don't have voids
        if( containsVoid ) {
            addButton(new Button(baseX + 10, baseY - 50, 95, 20, getTrans("tooltip.screen.edit_filters"), (button) -> {
                PacketHandler.sendToServer(new PacketOpenFilterContainer());
            }));

            addButton(new WhitelistButton(baseX + 10 + (115 - 20), baseY - 50, 20, 20, isWhitelist, (button) -> {
                isWhitelist = !isWhitelist;
                ((WhitelistButton) button).setWhitelist(isWhitelist);
                PacketHandler.sendToServer(new PacketToggleFilters());
            }));
        }

        // Button logic
        if( !UpgradeTools.containsActiveUpgrade(gadget, Upgrade.THREE_BY_THREE) )
            sizeButton.active = false;
    }

    private boolean toggleUpgrade(Upgrade upgrade, boolean update) {
        // When the button is clicked we toggle
        if( update )
            PacketHandler.sendToServer(new PacketUpdateUpgrade(upgrade.getName()));

        // When we're just init the gui, we check if it's on or off.
        return upgrade.isEnabled();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);

        drawString(getMinecraft().fontRenderer, getTrans("tooltip.screen.mining_gadget"), (width / 2) - 135, (height / 2) - 70, Color.WHITE.getRGB());
        drawString(getMinecraft().fontRenderer, getTrans("tooltip.screen.toggle_upgrades"), (width / 2) + 10, (height / 2) - 70, Color.WHITE.getRGB());

        if( toggleableList.size() == 0 )
            drawString(getMinecraft().fontRenderer, getTrans("tooltip.screen.no_upgrades"), (width / 2) + 10, (height / 2) - 50, Color.GRAY.getRGB());

        this.children.forEach(e -> {
            if( !(e instanceof ToggleButton) && !(e instanceof WhitelistButton) )
                return;

            if( e instanceof WhitelistButton ) {
                if( e.isMouseOver(mouseX, mouseY) )
                    renderTooltip(isWhitelist ? getTrans("tooltip.screen.whitelist") : getTrans("tooltip.screen.blacklist"), mouseX, mouseY);
            } else {
                ToggleButton btn = ((ToggleButton) e);
                if (btn.isMouseOver(mouseX, mouseY))
                    renderTooltip(btn.getTooltip(), mouseX, mouseY);
            }
        });
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
        //Future proofing for other potential sliders
        if (slider.equals(rangeSlider))
            this.beamRange = slider.getValueInt();
    }

    public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
        rangeSlider.dragging = false;
        return false;
    }

    private static String getTrans(String key, Object... args) {
        return new TranslationTextComponent(MiningGadgets.MOD_ID + "." + key, args).getUnformattedComponentText();
    }

    public static final class WhitelistButton extends Button {
        private boolean isWhitelist;

        public WhitelistButton(int widthIn, int heightIn, int width, int height, boolean isWhitelist, IPressable onPress) {
            super(widthIn, heightIn, width, height, "", onPress);
            this.isWhitelist = isWhitelist;
        }

        @Override
        public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
            GlStateManager.disableTexture();
            GlStateManager.color4f(.4f, .4f, .4f, 1f);
            this.blit(this.x, this.y, 0, 0, this.width, this.height);

            if( this.isWhitelist )
                GlStateManager.color4f(1f, 1f, 1f, 1f);
            else
                GlStateManager.color4f(0f, 0f, 0f, 1f);

            this.blit(this.x + 2, this.y + 2, 0, 0, this.width-4, this.height-4);
            GlStateManager.enableTexture();
        }

        public void setWhitelist(boolean whitelist) {
            isWhitelist = whitelist;
        }
    }
}
