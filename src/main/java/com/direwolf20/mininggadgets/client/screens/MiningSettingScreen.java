package com.direwolf20.mininggadgets.client.screens;

import com.direwolf20.mininggadgets.client.screens.widget.ToggleButton;
import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeTools;
import com.direwolf20.mininggadgets.common.network.data.*;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import net.neoforged.neoforge.network.PacketDistributor;

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
    private ExtendedSlider rangeSlider;
    private ExtendedSlider volumeSlider;
    private ExtendedSlider freezeDelaySlider;
    private List<Upgrade> toggleableList = new ArrayList<>();
    private HashMap<Upgrade, ToggleButton> upgradeButtons = new HashMap<>();
    private boolean containsFreeze = false;
    private MiningProperties.SizeMode currentMode;

    public MiningSettingScreen(ItemStack gadget) {
        super(Component.literal("title"));

        this.gadget = gadget;
        this.beamRange = MiningProperties.getBeamRange(gadget);
        this.volume = MiningProperties.getVolume(gadget);
        this.freezeDelay = MiningProperties.getFreezeDelay(gadget);
        this.currentMode = MiningProperties.getSizeMode(gadget);
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
            addRenderableWidget(
                    Button.builder(getTrans("tooltip.screen.edit_filters"), (button) -> {
                        PacketDistributor.SERVER.noArg().send(new OpenFilterContainerPayload());
                    }).pos(baseX + 10, top + 20).size( 95, 20).build()
            );

            addRenderableWidget(new WhitelistButton(baseX + 10 + (115 - 20), top + 20, 20, 20, isWhitelist, (button) -> {
                isWhitelist = !isWhitelist;
                ((WhitelistButton) button).setWhitelist(isWhitelist);
                PacketDistributor.SERVER.noArg().send(new ToggleFiltersPayload());
            }));
        }

        // Left size
        currentSize = MiningProperties.getRange(gadget);
        int maxMiningRange = MiningProperties.getMaxMiningRange(gadget);

        Button sizeButton;
        leftWidgets.add(sizeButton = Button.builder(getTrans("tooltip.screen.size", currentSize), (button) -> {
            if (currentSize == maxMiningRange)
                currentSize = 1;
            else
                currentSize += 2;

            button.setMessage(getTrans("tooltip.screen.size", currentSize));
            PacketDistributor.SERVER.noArg().send(new ChangeMiningSizePayload());
        }).pos(baseX - 135, 0).size(125, 20).build());

        if (maxMiningRange > 3) {
            leftWidgets.add(Button.builder(currentMode.getTooltip(), (button) -> {
                currentMode = MiningProperties.nextSizeMode(gadget);

                button.setMessage(currentMode.getTooltip());
                PacketDistributor.SERVER.noArg().send(new ChangeMiningSizeModePayload());
            }).pos(baseX - 135, 0).size(125, 20).build());
        }

        ///ForgeSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString)
        leftWidgets.add(rangeSlider = new ExtendedSlider(baseX - 135, 0, 125, 20, getTrans("tooltip.screen.range").append(": "), Component.empty(), 1, MiningProperties.getBeamMaxRange(gadget), this.beamRange, true) {
            @Override
            protected void applyValue() {
                beamRange = this.getValueInt();
            }
        });

        leftWidgets.add(Button.builder(getTrans("tooltip.screen.visuals_menu"), (button) -> {
            ModScreens.openVisualSettingsScreen(gadget);
        }).pos(baseX - 135, 0).size(125, 20).build());

        //Precision Mode
        leftWidgets.add(Button.builder(getTrans("tooltip.screen.precision_mode", isPrecision), (button) -> {
            isPrecision = !isPrecision;
            button.setMessage(getTrans("tooltip.screen.precision_mode", isPrecision));
            PacketDistributor.SERVER.noArg().send(new TogglePrecisionPayload());
        }).pos(baseX - 135, 0).size(125, 20).build());

        // volume slider
        leftWidgets.add(volumeSlider = new ExtendedSlider(baseX - 135, 0, 125, 20, getTrans("tooltip.screen.volume").append(": "), Component.literal("%"), 0, 100, volume * 100, true) {
            @Override
            protected void applyValue() {
                volume = (float) (this.getValue() / 100D);
            }
        });

        // Freeze delay
        if( containsFreeze )
            leftWidgets.add(freezeDelaySlider = new ExtendedSlider(baseX - 135, 0, 125, 20, getTrans("tooltip.screen.freeze_delay").append(": "), Component.literal(" ").append(getTrans("tooltip.screen.ticks")), 0, 10, MiningProperties.getFreezeDelay(gadget), true) {
                @Override
                protected void applyValue() {
                    freezeDelay = this.getValueInt();
                }
            });

        // Button logic
        if (maxMiningRange == 1)
            sizeButton.active = false;

        // Lay the buttons out, too lazy to figure out the math every damn time.
        // Ordered by where you add them.
        for(int i = 0; i < leftWidgets.size(); i ++) {
            leftWidgets.get(i).setY((top + 20) + (i * 25));
            addRenderableWidget(leftWidgets.get(i));
        }
    }

    private boolean toggleUpgrade(Upgrade upgrade, boolean update) {
        // When the button is clicked we toggle
        if( update ) {
            this.updateButtons(upgrade);
            PacketDistributor.SERVER.noArg().send(new UpdateUpgradePayload(upgrade.getName()));
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
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        //this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        int top = (height / 2) - (containsFreeze ? 80 : 60);

        guiGraphics.drawString(font, getTrans("tooltip.screen.mining_gadget"), (width / 2) - 135, top, Color.WHITE.getRGB(), false);
        guiGraphics.drawString(font, getTrans("tooltip.screen.toggle_upgrades"), (width / 2) + 10, top, Color.WHITE.getRGB(), false);

        if(toggleableList.size() == 0 )
            guiGraphics.drawString(font, getTrans("tooltip.screen.no_upgrades"), (width / 2) + 10, top + 20, Color.GRAY.getRGB(), false);

        this.children().forEach(e -> {
            if( !(e instanceof ToggleButton) && !(e instanceof WhitelistButton) && !e.equals(freezeDelaySlider) )
                return;

            if( e instanceof WhitelistButton ) {
                if( e.isMouseOver(mouseX, mouseY) )
                    guiGraphics.renderTooltip(font, isWhitelist ? getTrans("tooltip.screen.whitelist") : getTrans("tooltip.screen.blacklist"), mouseX, mouseY);
            } else if( e.equals(freezeDelaySlider) ) {
                if( e.isMouseOver(mouseX, mouseY) ) {
                    assert e instanceof ExtendedSlider;

                    // This is a bit silly, not going to lie
                    List<FormattedText> helpText = Arrays.stream(getTrans("tooltip.screen.delay_explain").getString().split("\n")).map(Component::literal).collect(Collectors.toList());
                    guiGraphics.renderTooltip(font, Language.getInstance().getVisualOrder(helpText), ((ExtendedSlider) e).getX() - 8, ((ExtendedSlider) e).getY() + 40);
                }
            } else {
                assert e instanceof ToggleButton;
                ToggleButton btn = ((ToggleButton) e);
                if (btn.isMouseOver(mouseX, mouseY))
                    guiGraphics.renderTooltip(font, btn.getOurTooltip(), DefaultTooltipPositioner.INSTANCE,  mouseX, mouseY);
            }
        });
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void removed() {
        PacketDistributor.SERVER.noArg().send(new ChangeRangePayload(this.beamRange));
        PacketDistributor.SERVER.noArg().send(new ChangeVolumePayload(this.volume));
        PacketDistributor.SERVER.noArg().send(new ChangeFreezeDelayPayload(this.freezeDelay));

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
    public boolean mouseScrolled(double mouseX, double mouseY, double delta, double deltaY) {
        if (rangeSlider.isMouseOver(mouseX, mouseY)) {
            rangeSlider.setValue(rangeSlider.getValueInt() + (delta > 0 ? 1 : -1));
            beamRange = rangeSlider.getValueInt();
        }
        if (freezeDelaySlider != null && freezeDelaySlider.isMouseOver(mouseX, mouseY)) {
            freezeDelaySlider.setValue(freezeDelaySlider.getValueInt() + (delta > 0 ? 1 : -1));
            freezeDelay = freezeDelaySlider.getValueInt();
        }
        if (volumeSlider.isMouseOver(mouseX, mouseY)) {
            volumeSlider.setValue(volumeSlider.getValueInt() + (delta > 0 ? 1 : -1));
            volume = volumeSlider.getValueInt();
        }
        return false;
    }


    public static final class WhitelistButton extends Button {
        private boolean isWhitelist;

        public WhitelistButton(int widthIn, int heightIn, int width, int height, boolean isWhitelist, OnPress onPress) {
            super(Button.builder(Component.empty(), onPress).pos(widthIn, heightIn).size(width, height));
            this.isWhitelist = isWhitelist;
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
//            guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0xFFa8a8a8);
            guiGraphics.fill(this.getX() + 2, this.getY() + 2, this.getX() + this.width - 2, this.getY() + this.height - 2, this.isWhitelist ? 0xFFFFFFFF : 0xFF000000);
        }

        public void setWhitelist(boolean whitelist) {
            isWhitelist = whitelist;
        }
    }
}
