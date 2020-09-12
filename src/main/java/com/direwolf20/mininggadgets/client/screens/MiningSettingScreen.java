package com.direwolf20.mininggadgets.client.screens;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.client.screens.widget.ToggleButton;
import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.items.upgrade.Upgrade;
import com.direwolf20.mininggadgets.common.items.upgrade.UpgradeTools;
import com.direwolf20.mininggadgets.common.network.PacketHandler;
import com.direwolf20.mininggadgets.common.network.packets.*;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.client.gui.widget.Slider;

import java.awt.*;
import java.awt.Color;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

// todo: refactor and clean up
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
    private List<Upgrade> toggleableList = new ArrayList<>();
    private HashMap<Upgrade, ToggleButton> upgradeButtons = new HashMap<>();
    private boolean containsFreeze = false;

    public MiningSettingScreen(ItemStack gadget) {
        super(new StringTextComponent("title"));

        this.gadget = gadget;
        this.beamRange = MiningProperties.getBeamRange(gadget);
        this.volume = MiningProperties.getVolume(gadget);
        this.freezeDelay = MiningProperties.getFreezeDelay(gadget);
    }

    @Override
    protected void init() {
        List<Widget> leftWidgets = new ArrayList<>();

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
            addButton(btn);
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
            addButton(new Button(baseX + 10, top + 20, 95, 20, getTrans("tooltip.screen.edit_filters"), (button) -> {
                PacketHandler.sendToServer(new PacketOpenFilterContainer());
            }));

            addButton(new WhitelistButton(baseX + 10 + (115 - 20), top + 20, 20, 20, isWhitelist, (button) -> {
                isWhitelist = !isWhitelist;
                ((WhitelistButton) button).setWhitelist(isWhitelist);
                PacketHandler.sendToServer(new PacketToggleFilters());
            }));
        }

        // Left size
        currentSize = MiningProperties.getRange(gadget);

        Button sizeButton;
        leftWidgets.add(sizeButton = new Button(baseX - 135, 0, 125, 20, new TranslationTextComponent("mininggadgets.tooltip.screen.size", currentSize), (button) -> {
            currentSize = currentSize == 1 ? 3 : 1;
            button.setMessage(getTrans("tooltip.screen.size", currentSize));
            PacketHandler.sendToServer(new PacketChangeMiningSize());
        }));

        leftWidgets.add(rangeSlider = new Slider(baseX - 135, 0, 125, 20, getTrans("tooltip.screen.range").appendString(": "), StringTextComponent.EMPTY, 1, MiningProperties.getBeamMaxRange(gadget), this.beamRange, false, true, s -> {}, this));

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
        leftWidgets.add(volumeSlider = new Slider(baseX - 135, 0, 125, 20, getTrans("tooltip.screen.volume").appendString(": "), new StringTextComponent("%"), 0, 100, Math.min(100, volume * 100), false, true, s -> {}, this));

        // Freeze delay
        if( containsFreeze )
            leftWidgets.add(freezeDelaySlider = new Slider(baseX - 135, 0, 125, 20, getTrans("tooltip.screen.freeze_delay").appendString(": "), new StringTextComponent(" ").append(getTrans("tooltip.screen.ticks")), 0, 10, MiningProperties.getFreezeDelay(gadget), false, true, s -> {}, this));

        // Button logic
        if( !UpgradeTools.containsActiveUpgrade(gadget, Upgrade.THREE_BY_THREE) )
            sizeButton.active = false;

        // Lay the buttons out, too lazy to figure out the math every damn time.
        // Ordered by where you add them.
        for(int i = 0; i < leftWidgets.size(); i ++) {
            leftWidgets.get(i).y = (top + 20) + (i * 25);
            addButton(leftWidgets.get(i));
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
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);

        int top = (height / 2) - (containsFreeze ? 80 : 60);

        drawString(stack, getMinecraft().fontRenderer, getTrans("tooltip.screen.mining_gadget"), (width / 2) - 135, top, Color.WHITE.getRGB());
        drawString(stack, getMinecraft().fontRenderer, getTrans("tooltip.screen.toggle_upgrades"), (width / 2) + 10, top, Color.WHITE.getRGB());

        if( toggleableList.size() == 0 )
            drawString(stack, getMinecraft().fontRenderer, getTrans("tooltip.screen.no_upgrades"), (width / 2) + 10, top + 20, Color.GRAY.getRGB());

        this.children.forEach(e -> {
            if( !(e instanceof ToggleButton) && !(e instanceof WhitelistButton) && !e.equals(freezeDelaySlider) )
                return;

            if( e instanceof WhitelistButton ) {
                if( e.isMouseOver(mouseX, mouseY) )
                    renderTooltip(stack, isWhitelist ? getTrans("tooltip.screen.whitelist") : getTrans("tooltip.screen.blacklist"), mouseX, mouseY);
            } else if( e.equals(freezeDelaySlider) ) {
                if( e.isMouseOver(mouseX, mouseY) ) {
                    assert e instanceof Slider;

                    // This is a bit silly, not going to lie
                    List<ITextProperties> helpText = Arrays.stream(getTrans("tooltip.screen.delay_explain").getString().split("\n")).map(StringTextComponent::new).collect(Collectors.toList());
                    renderTooltip(stack, LanguageMap.getInstance().func_244260_a(helpText), ((Slider)e).x - 8, ((Slider)e).y + 40);
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
    public void onClose() {
        PacketHandler.sendToServer(new PacketChangeRange(this.beamRange));
        PacketHandler.sendToServer(new PacketChangeVolume(this.volume));
        PacketHandler.sendToServer(new PacketChangeFreezeDelay(this.freezeDelay));

        super.onClose();
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        InputMappings.Input mouseKey = InputMappings.getInputByCode(p_keyPressed_1_, p_keyPressed_2_);
        if (p_keyPressed_1_ == 256 || minecraft.gameSettings.keyBindInventory.isActiveAndMatches(mouseKey)) {
            closeScreen();

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
        if( freezeDelaySlider.isMouseOver(mouseX, mouseY) ) {
            freezeDelaySlider.sliderValue += (.1f * (delta > 0 ? 1 : -1));
            freezeDelaySlider.updateSlider();
        }
        if( volumeSlider.isMouseOver(mouseX, mouseY) ) {
            volumeSlider.sliderValue += (1f * (delta > 0 ? 1 : -1));
            volumeSlider.updateSlider();
        }
        return false;
    }

    private static TranslationTextComponent getTrans(String key, Object... args) {
        return new TranslationTextComponent(MiningGadgets.MOD_ID + "." + key, args);
    }

    public static final class WhitelistButton extends Button {
        private boolean isWhitelist;

        public WhitelistButton(int widthIn, int heightIn, int width, int height, boolean isWhitelist, IPressable onPress) {
            super(widthIn, heightIn, width, height, StringTextComponent.EMPTY, onPress);
            this.isWhitelist = isWhitelist;
        }

        @Override
        public void renderButton(MatrixStack stack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
            RenderSystem.disableTexture();
            RenderSystem.color4f(.4f, .4f, .4f, 1f);
            this.blit(stack, this.x, this.y, 0, 0, this.width, this.height);

            if( this.isWhitelist )
                RenderSystem.color4f(1f, 1f, 1f, 1f);
            else
                RenderSystem.color4f(0f, 0f, 0f, 1f);

            this.blit(stack, this.x + 2, this.y + 2, 0, 0, this.width-4, this.height-4);
            RenderSystem.enableTexture();
        }

        public void setWhitelist(boolean whitelist) {
            isWhitelist = whitelist;
        }
    }
}
