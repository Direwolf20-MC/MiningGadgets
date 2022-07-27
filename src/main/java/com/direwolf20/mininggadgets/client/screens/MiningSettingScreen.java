package com.direwolf20.mininggadgets.client.screens;

import com.direwolf20.mininggadgets.api.upgrades.StandardUpgrades;
import com.direwolf20.mininggadgets.client.screens.widget.ToggleButton;
import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.items.MiningGadget;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.network.PacketHandler;
import com.direwolf20.mininggadgets.common.network.packets.*;
import com.direwolf20.mininggadgets.common.upgrades.UpgradeHolder;
import com.direwolf20.mininggadgets.common.upgrades.impl.FortuneUpgrade;
import com.direwolf20.mininggadgets.common.upgrades.impl.SilkUpgrade;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.widget.Slider;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class MiningSettingScreen extends Screen implements Slider.ISlider {
    private ItemStack gadget;

    private int beamRange;
    private int freezeDelay;
    private float volume;
    private int currentSize = 1;
    private boolean isWhitelist = true;
    private boolean isPrecision = true;
    private Slider rangeSlider;
    private Slider volumeSlider;
    private Slider freezeDelaySlider;
    private List<UpgradeHolder> toggleableList = new ArrayList<>();
    private HashMap<UpgradeHolder, ToggleButton> upgradeButtons = new HashMap<>();
    private boolean containsFreeze = false;

    public MiningSettingScreen(ItemStack gadget) {
        super(new TextComponent("title"));

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
        List<UpgradeHolder> upgrades = MiningGadget.getUpgrades(this.gadget);
        toggleableList = upgrades;
        containsFreeze = upgrades.stream().map(e -> e.upgrade().getId()).anyMatch(e -> e == StandardUpgrades.FREEZING);
        boolean containsVoid = upgrades.stream().map(e -> e.upgrade().getId()).anyMatch(e -> e == StandardUpgrades.VOID);

        isWhitelist = MiningProperties.getWhiteList(gadget);
        isPrecision = MiningProperties.getPrecisionMode(gadget);

        int top = baseY - (containsFreeze ? 80 : 60);

        // Right size
        // Remove 6 from x to center it as the padding on the right pushes off center... (I'm a ui nerd)
        int index = 0, x = baseX + 10, y = top + (containsVoid ? 45 : 20);
        for (UpgradeHolder upgrade : toggleableList) {
            // TODO: add back
//            ToggleButton btn = new ToggleButton(x + (index * 30), y, new TextComponent(ForgeI18n.parseMessage(upgrade.getLocal()).replace(ForgeI18n.parseMessage(upgrade.getLocalReplacement()), "")), new ResourceLocation(MiningGadgets.MOD_ID, "textures/item/upgrade_" + upgrade.getName() + ".png"), send -> this.toggleUpgrade(upgrade, send));
//            addRenderableWidget(btn);
//            upgradeButtons.put(upgrade, btn);

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
        leftWidgets.add(sizeButton = new Button(baseX - 135, 0, 125, 20, new TranslatableComponent("mininggadgets.tooltip.screen.size", currentSize), (button) -> {
            currentSize = currentSize == 1 ? 3 : 1;
            button.setMessage(getTrans("tooltip.screen.size", currentSize));
            PacketHandler.sendToServer(new PacketChangeMiningSize());
        }));

        leftWidgets.add(rangeSlider = new Slider(baseX - 135, 0, 125, 20, getTrans("tooltip.screen.range").append(": "), TextComponent.EMPTY, 1, MiningProperties.getBeamMaxRange(gadget), this.beamRange, false, true, s -> {}, this));

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
        leftWidgets.add(volumeSlider = new Slider(baseX - 135, 0, 125, 20, getTrans("tooltip.screen.volume").append(": "), new TextComponent("%"), 0, 100, Math.min(100, volume * 100), false, true, s -> {}, this));

        // Freeze delay
        if( containsFreeze )
            leftWidgets.add(freezeDelaySlider = new Slider(baseX - 135, 0, 125, 20, getTrans("tooltip.screen.freeze_delay").append(": "), new TextComponent(" ").append(getTrans("tooltip.screen.ticks")), 0, 10, MiningProperties.getFreezeDelay(gadget), false, true, s -> {}, this));

        // Button logic
        if(upgrades.stream().filter(UpgradeHolder::active).noneMatch(e -> e.upgrade().getId() == StandardUpgrades.THREE_BY_THREE))
            sizeButton.active = false;

        // Lay the buttons out, too lazy to figure out the math every damn time.
        // Ordered by where you add them.
        for(int i = 0; i < leftWidgets.size(); i ++) {
            leftWidgets.get(i).y = (top + 20) + (i * 25);
            addRenderableWidget(leftWidgets.get(i));
        }
    }

    private boolean toggleUpgrade(UpgradeHolder upgrade, boolean update) {
        // When the button is clicked we toggle
        if( update ) {
            this.updateButtons(upgrade);
            PacketHandler.sendToServer(new PacketUpdateUpgrade(upgrade.upgrade().getId()));
        }

        // When we're just init the gui, we check if it's on or off.
        return upgrade.active();
    }

    private void updateButtons(UpgradeHolder upgrade) {
        for(Map.Entry<UpgradeHolder, ToggleButton> btn : this.upgradeButtons.entrySet()) {
            UpgradeHolder btnUpgrade = btn.getKey();

            if( (btnUpgrade.upgrade() instanceof FortuneUpgrade && btn.getValue().isEnabled() && upgrade.upgrade() instanceof SilkUpgrade)
                    || ((btnUpgrade.upgrade() instanceof SilkUpgrade) && btn.getValue().isEnabled() && upgrade.upgrade() instanceof FortuneUpgrade) ) {
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
                    assert e instanceof Slider;

                    // This is a bit silly, not going to lie
                    List<FormattedText> helpText = Arrays.stream(getTrans("tooltip.screen.delay_explain").getString().split("\n")).map(TextComponent::new).collect(Collectors.toList());
                    renderTooltip(stack, Language.getInstance().getVisualOrder(helpText), ((Slider)e).x - 8, ((Slider)e).y + 40);
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

    @Override
    public void onChangeSliderValue(Slider slider) {
        //Future proofing for other potential sliders
        if (slider.equals(rangeSlider))
            this.beamRange = slider.getValueInt();

        if (slider.equals(freezeDelaySlider))
            this.freezeDelay = slider.getValueInt();

        if (slider.equals(volumeSlider))
            this.volume = slider.getValueInt() / 100f;
    }

    public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
        rangeSlider.dragging = false;
        volumeSlider.dragging = false;
        if( freezeDelaySlider != null )
            freezeDelaySlider.dragging = false;
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if( rangeSlider.isMouseOver(mouseX, mouseY) ) {
            rangeSlider.sliderValue += (.1f * (delta > 0 ? 1 : -1));
            rangeSlider.updateSlider();
        }
        if( freezeDelaySlider != null && freezeDelaySlider.isMouseOver(mouseX, mouseY) ) {
            freezeDelaySlider.sliderValue += (.1f * (delta > 0 ? 1 : -1));
            freezeDelaySlider.updateSlider();
        }
        if( volumeSlider.isMouseOver(mouseX, mouseY) ) {
            volumeSlider.sliderValue += (.01f * (delta > 0 ? 1 : -1));
            volumeSlider.updateSlider();
        }
        return false;
    }

    private static TranslatableComponent getTrans(String key, Object... args) {
        return new TranslatableComponent(MiningGadgets.MOD_ID + "." + key, args);
    }

    public static final class WhitelistButton extends Button {
        private boolean isWhitelist;

        public WhitelistButton(int widthIn, int heightIn, int width, int height, boolean isWhitelist, OnPress onPress) {
            super(widthIn, heightIn, width, height, TextComponent.EMPTY, onPress);
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
