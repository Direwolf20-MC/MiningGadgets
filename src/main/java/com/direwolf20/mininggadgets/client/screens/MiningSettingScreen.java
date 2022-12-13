package com.direwolf20.mininggadgets.client.screens;

import com.direwolf20.mininggadgets.client.screens.widget.ToggleButton;
import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeTools;
import com.direwolf20.mininggadgets.common.network.PacketHandler;
import com.direwolf20.mininggadgets.common.network.packets.*;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.widget.ForgeSlider;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class MiningSettingScreen extends Screen {
    private ItemStack gadget;

    private int beamRange;
    private int freezeDelay;
    private float volume;
    private int currentSize = 1;
    private boolean isWhitelist = true;
    private boolean isPrecision = true;
    private ForgeSlider rangeSlider;
    private ForgeSlider volumeSlider;
    private ForgeSlider freezeDelaySlider;
    private List<Upgrade> toggleableList = new ArrayList<>();
    private HashMap<Upgrade, ToggleButton> upgradeButtons = new HashMap<>();
    private boolean containsFreeze = false;

    public MiningSettingScreen(ItemStack gadget) {
        super(Component.literal("title"));

        this.gadget = gadget;
        this.beamRange = MiningProperties.getBeamRange(gadget);
        this.volume = MiningProperties.getVolume(gadget);
        this.freezeDelay = MiningProperties.getFreezeDelay(gadget);
    }

    @Override
    protected void init() {
        List<AbstractWidget> leftWidgets = new ArrayList<>();

        int baseX = width / 2, baseY = height / 2;

        // Filters out the non-toggleable options
        toggleableList.clear();
        toggleableList = UpgradeTools.getUpgrades(this.gadget).stream().filter(Upgrade::isToggleable).collect(Collectors.toList());
        containsFreeze = UpgradeTools.containsUpgradeFromList(toggleableList, Upgrade.FREEZING);
        boolean containsVoid = UpgradeTools.containsUpgradeFromList(toggleableList, Upgrade.VOID_JUNK);

        isWhitelist = MiningProperties.getWhiteList(gadget);
        isPrecision = MiningProperties.getPrecisionMode(gadget);

        int top = baseY - (containsFreeze ? 80 : 60);

        // Right size
        // Remove 6 from x to center it as the padding on the right pushes off center... (I'm a ui nerd)
        int index = 0, x = baseX + 10, y = top + (containsVoid ? 45 : 20);
        for (Upgrade upgrade : toggleableList) {
            ToggleButton btn = new ToggleButton(x + (index * 30), y, UpgradeTools.getName(upgrade), new ResourceLocation(MiningGadgets.MOD_ID, "textures/item/upgrade_" + upgrade.getName() + ".png"), send -> this.toggleUpgrade(upgrade, send));
            addRenderableWidget(btn);
            upgradeButtons.put(upgrade, btn);

            // Spaces the upgrades
            index ++;
            if( index % 4 == 0 ) {
                index = 0;
                y += 35;
            }
        }

        // Don't add if we don't have voids
        if( containsVoid ) {
            addRenderableWidget(new Button(baseX + 10, top + 20, 95, 20, getTrans("tooltip.screen.edit_filters"), (button) -> {
                PacketHandler.sendToServer(new PacketOpenFilterContainer());
            }));

            addRenderableWidget(new WhitelistButton(baseX + 10 + (115 - 20), top + 20, 20, 20, isWhitelist, (button) -> {
                isWhitelist = !isWhitelist;
                ((WhitelistButton) button).setWhitelist(isWhitelist);
                PacketHandler.sendToServer(new PacketToggleFilters());
            }));
        }

        // Left size
        currentSize = MiningProperties.getRange(gadget);

        Button sizeButton;
        leftWidgets.add(sizeButton = new Button(baseX - 135, 0, 125, 20, Component.translatable("mininggadgets.tooltip.screen.size", currentSize), (button) -> {
            if (UpgradeTools.getUpgrades(gadget).contains(Upgrade.FIVE_BY_FIVE)) {
                currentSize = currentSize == 3 ? 5 : currentSize == 1 ? 3 : 1;
            } else {
                currentSize = currentSize == 1 ? 3 : 1;
            }
            button.setMessage(getTrans("tooltip.screen.size", currentSize));
            PacketHandler.sendToServer(new PacketChangeMiningSize());
        }));

        ///ForgeSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString)
        leftWidgets.add(rangeSlider = new ForgeSlider(baseX - 135, 0, 125, 20, getTrans("tooltip.screen.range").append(": "), Component.empty(), 1, MiningProperties.getBeamMaxRange(gadget), this.beamRange, true) {
            @Override
            protected void applyValue() {
                beamRange = this.getValueInt();
            }
        });

        leftWidgets.add(new Button(baseX - 135, 0, 125, 20, getTrans("tooltip.screen.visuals_menu"), (button) -> {
            ModScreens.openVisualSettingsScreen(gadget);
        }));

        //Precision Mode
        leftWidgets.add(new Button(baseX - 135, 0, 125, 20, getTrans("tooltip.screen.precision_mode", isPrecision), (button) -> {
            isPrecision = !isPrecision;
            button.setMessage(getTrans("tooltip.screen.precision_mode", isPrecision));
            PacketHandler.sendToServer(new PacketTogglePrecision());
        }));

        // volume slider
        leftWidgets.add(volumeSlider = new ForgeSlider(baseX - 135, 0, 125, 20, getTrans("tooltip.screen.volume").append(": "), Component.literal("%"), 0, 100, Math.min(100, volume * 100), true) {
            @Override
            protected void applyValue() {
                volume = (float) this.getValue();
            }
        });

        // Freeze delay
        if( containsFreeze )
            leftWidgets.add(freezeDelaySlider = new ForgeSlider(baseX - 135, 0, 125, 20, getTrans("tooltip.screen.freeze_delay").append(": "), Component.literal(" ").append(getTrans("tooltip.screen.ticks")), 0, 10, MiningProperties.getFreezeDelay(gadget), true) {
                @Override
                protected void applyValue() {
                    freezeDelay = this.getValueInt();
                }
            });

        // Button logic
        if( !UpgradeTools.containsActiveUpgrade(gadget, Upgrade.THREE_BY_THREE) &&
            !UpgradeTools.containsActiveUpgrade(gadget, Upgrade.FIVE_BY_FIVE) )
            sizeButton.active = false;

        // Lay the buttons out, too lazy to figure out the math every damn time.
        // Ordered by where you add them.
        for(int i = 0; i < leftWidgets.size(); i ++) {
            leftWidgets.get(i).y = (top + 20) + (i * 25);
            addRenderableWidget(leftWidgets.get(i));
        }
    }

    private boolean toggleUpgrade(Upgrade upgrade, boolean update) {
        // When the button is clicked we toggle
        if( update ) {
            this.updateButtons(upgrade);
            PacketHandler.sendToServer(new PacketUpdateUpgrade(upgrade.getName()));
        }

        // When we're just init the gui, we check if it's on or off.
        return upgrade.isEnabled();
    }

    private void updateButtons(Upgrade upgrade) {
        for(Map.Entry<Upgrade, ToggleButton> btn : this.upgradeButtons.entrySet()) {
            Upgrade btnUpgrade = btn.getKey();

            if( (btnUpgrade.lazyIs(Upgrade.FORTUNE_1) && btn.getValue().isEnabled() && upgrade.lazyIs(Upgrade.SILK) )
                    || ((btnUpgrade.lazyIs(Upgrade.SILK)) && btn.getValue().isEnabled() && upgrade.lazyIs(Upgrade.FORTUNE_1)) ) {
                this.upgradeButtons.get(btn.getKey()).setEnabled(false);
            }
        }
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);

        int top = (height / 2) - (containsFreeze ? 80 : 60);

        drawString(stack, getMinecraft().font, getTrans("tooltip.screen.mining_gadget"), (width / 2) - 135, top, Color.WHITE.getRGB());
        drawString(stack, getMinecraft().font, getTrans("tooltip.screen.toggle_upgrades"), (width / 2) + 10, top, Color.WHITE.getRGB());

        if( toggleableList.size() == 0 )
            drawString(stack, getMinecraft().font, getTrans("tooltip.screen.no_upgrades"), (width / 2) + 10, top + 20, Color.GRAY.getRGB());

        this.children().forEach(e -> {
            if( !(e instanceof ToggleButton) && !(e instanceof WhitelistButton) && !e.equals(freezeDelaySlider) )
                return;

            if( e instanceof WhitelistButton ) {
                if( e.isMouseOver(mouseX, mouseY) )
                    renderTooltip(stack, isWhitelist ? getTrans("tooltip.screen.whitelist") : getTrans("tooltip.screen.blacklist"), mouseX, mouseY);
            } else if( e.equals(freezeDelaySlider) ) {
                if( e.isMouseOver(mouseX, mouseY) ) {
                    assert e instanceof ForgeSlider;

                    // This is a bit silly, not going to lie
                    List<FormattedText> helpText = Arrays.stream(getTrans("tooltip.screen.delay_explain").getString().split("\n")).map(Component::literal).collect(Collectors.toList());
                    renderTooltip(stack, Language.getInstance().getVisualOrder(helpText), ((ForgeSlider)e).x - 8, ((ForgeSlider)e).y + 40);
                }
            } else {
                assert e instanceof ToggleButton;
                ToggleButton btn = ((ToggleButton) e);
                if (btn.isMouseOver(mouseX, mouseY))
                    renderTooltip(stack, btn.getTooltip(), mouseX, mouseY);
            }
        });
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void removed() {
        PacketHandler.sendToServer(new PacketChangeRange(this.beamRange));
        PacketHandler.sendToServer(new PacketChangeVolume(this.volume));
        PacketHandler.sendToServer(new PacketChangeFreezeDelay(this.freezeDelay));

        super.removed();
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        InputConstants.Key mouseKey = InputConstants.getKey(p_keyPressed_1_, p_keyPressed_2_);
        if (p_keyPressed_1_ == 256 || minecraft.options.keyInventory.isActiveAndMatches(mouseKey)) {
            onClose();

            return true;
        }

        return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }

    private static MutableComponent getTrans(String key, Object... args) {
        return Component.translatable(MiningGadgets.MOD_ID + "." + key, args);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if( rangeSlider.isMouseOver(mouseX, mouseY) ) {
            rangeSlider.setValue(rangeSlider.getValueInt() + (delta > 0 ? 1 : -1));
            beamRange = rangeSlider.getValueInt();
        }
        if( freezeDelaySlider != null && freezeDelaySlider.isMouseOver(mouseX, mouseY) ) {
            freezeDelaySlider.setValue(freezeDelaySlider.getValueInt() + (delta > 0 ? 1 : -1));
            freezeDelay = freezeDelaySlider.getValueInt();
        }
        if( volumeSlider.isMouseOver(mouseX, mouseY) ) {
            volumeSlider.setValue(volumeSlider.getValueInt() + (delta > 0 ? 1 : -1));
            volume = volumeSlider.getValueInt();
        }
        return false;
    }


    public static final class WhitelistButton extends Button {
        private boolean isWhitelist;

        public WhitelistButton(int widthIn, int heightIn, int width, int height, boolean isWhitelist, OnPress onPress) {
            super(widthIn, heightIn, width, height, Component.empty(), onPress);
            this.isWhitelist = isWhitelist;
        }

        @Override
        public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
            fill(stack, this.x, this.y, this.x + this.width, this.y + this.height, 0xFFa8a8a8);
            fill(stack, this.x + 2, this.y + 2, this.x + this.width - 2, this.y + this.height - 2, this.isWhitelist ? 0xFFFFFFFF : 0xFF000000);
        }

        public void setWhitelist(boolean whitelist) {
            isWhitelist = whitelist;
        }
    }
}
