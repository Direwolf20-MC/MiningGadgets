package com.direwolf20.mininggadgets.client.screens;

import com.direwolf20.mininggadgets.common.items.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.network.data.ChangeBreakTypePayload;
import com.direwolf20.mininggadgets.common.network.data.ChangeColorPayload;
import com.direwolf20.mininggadgets.common.util.CodecHelpers;
import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.shorts.ShortConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;

public class MiningVisualsScreen extends Screen {
    private ItemStack gadget;
    private Button blockBreakButton;
    private short red;
    private short green;
    private short blue;
    private short red_inner;
    private short green_inner;
    private short blue_inner;
    private ExtendedSlider sliderRedInner;
    private ExtendedSlider sliderGreenInner;
    private ExtendedSlider sliderBlueInner;
    private ExtendedSlider sliderRedOuter;
    private ExtendedSlider sliderGreenOuter;
    private ExtendedSlider sliderBlueOuter;

    private Map<ExtendedSlider, ShortConsumer> sliderMap = new HashMap<>();

    public MiningVisualsScreen(ItemStack gadget) {
        super(Component.literal("title"));
        this.gadget = gadget;
        CodecHelpers.LaserColor laserColor = MiningProperties.getColors(gadget);
        this.red = laserColor.red();
        this.green = laserColor.green();
        this.blue = laserColor.blue();
        this.red_inner = laserColor.innerRed();
        this.green_inner = laserColor.innerGreen();
        this.blue_inner = laserColor.innerBlue();
    }

    @Override
    protected void init() {
        int baseX = width / 2, baseY = height / 2;

        MutableComponent buttonText;
        if (MiningProperties.getBreakType(gadget) == MiningProperties.BreakTypes.SHRINK)
            buttonText = Component.translatable("mininggadgets.tooltip.screen.shrink");
        else
            buttonText = Component.translatable("mininggadgets.tooltip.screen.fade");

        blockBreakButton = Button.builder(
                buttonText,
                (button) -> {
                    if (blockBreakButton.getMessage().getString().contains("Shrink"))
                        button.setMessage(Component.translatable("mininggadgets.tooltip.screen.fade"));
                    else
                        button.setMessage(Component.translatable("mininggadgets.tooltip.screen.shrink"));

                    PacketDistributor.sendToServer(new ChangeBreakTypePayload());
                }
        )
                .pos(baseX - (150), baseY - 55)
                .size(150, 20)
                .build();

        addRenderableWidget(blockBreakButton);

        sliderRedInner = new ExtendedSlider(baseX - (150), baseY - 10, 150, 20, Component.translatable("mininggadgets.tooltip.screen.red").append(": "), Component.empty(), 0, 255, this.red_inner, true) {
            @Override
            protected void applyValue() {
                red_inner = (short) this.getValueInt();
            }
        };
        sliderGreenInner = new ExtendedSlider(baseX - (150), baseY + 15, 150, 20, Component.translatable("mininggadgets.tooltip.screen.green").append(": "), Component.empty(), 0, 255, this.green_inner, true) {
            @Override
            protected void applyValue() {
                green_inner = (short) this.getValueInt();
            }
        };
        sliderBlueInner = new ExtendedSlider(baseX - (150), baseY + 40, 150, 20, Component.translatable("mininggadgets.tooltip.screen.blue").append(": "), Component.empty(), 0, 255, this.blue_inner, true) {
            @Override
            protected void applyValue() {
                blue_inner = (short) this.getValueInt();
            }
        };

        sliderRedOuter = new ExtendedSlider(baseX + (25), baseY - 10, 150, 20, Component.translatable("mininggadgets.tooltip.screen.red").append(": "), Component.empty(), 0, 255, this.red, true) {
            @Override
            protected void applyValue() {
                red = (short) this.getValueInt();
            }
        };
        sliderGreenOuter = new ExtendedSlider(baseX + (25), baseY + 15, 150, 20, Component.translatable("mininggadgets.tooltip.screen.green").append(": "), Component.empty(), 0, 255, this.green, true) {
            @Override
            protected void applyValue() {
                green = (short) this.getValueInt();
            }
        };
        sliderBlueOuter = new ExtendedSlider(baseX + (25), baseY + 40, 150, 20, Component.translatable("mininggadgets.tooltip.screen.blue").append(": "), Component.empty(), 0, 255, this.blue, true) {
            @Override
            protected void applyValue() {
                blue = (short) this.getValueInt();
            }
        };

        addRenderableWidget(sliderRedInner);
        addRenderableWidget(sliderGreenInner);
        addRenderableWidget(sliderBlueInner);
        addRenderableWidget(sliderRedOuter);
        addRenderableWidget(sliderGreenOuter);
        addRenderableWidget(sliderBlueOuter);

        // Used for scroll action
        this.sliderMap = Map.of(
                sliderRedInner, (a) -> red_inner = a,
                sliderGreenInner, (a) -> green_inner = a,
                sliderBlueInner, (a) -> blue_inner = a,
                sliderRedOuter, (a) -> red = a,
                sliderGreenOuter, (a) -> green = a,
                sliderBlueOuter, (a) -> blue = a
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        //this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        guiGraphics.drawCenteredString(font, Component.translatable("mininggadgets.tooltip.screen.visual_settings"), (width / 2), (height / 2) - 95, 0xFFFFFF);
        guiGraphics.drawString(font, Component.translatable("mininggadgets.tooltip.screen.block_break_style"), (width / 2) - 150, (height / 2) - 70, 0xFFFFFF, false);
        guiGraphics.drawString(font, Component.translatable("mininggadgets.tooltip.screen.beam_preview"), (width / 2) + 25, (height / 2) - 70, 0xFFFFFF, false);
        guiGraphics.drawString(font, Component.translatable("mininggadgets.tooltip.screen.inner_color"), (width / 2) - 150, (height / 2) - 25, 0xFFFFFF, false);
        guiGraphics.drawString(font, Component.translatable("mininggadgets.tooltip.screen.outer_color"), (width / 2) + 25, (height / 2) - 25, 0xFFFFFF, false);

        guiGraphics.pose().pushPose();
        guiGraphics.fill((width / 2) + 25, (height / 2) - 55, ((width / 2) + 25) + 150, ((height / 2) - 55) + 20, this.rgbToInt(this.red, this.green, this.blue));
        guiGraphics.fill((width / 2) + 25, (height / 2) - 50, ((width / 2) + 25) + 150, ((height / 2) - 50) + 10, this.rgbToInt(this.red_inner, this.green_inner, this.blue_inner));
        guiGraphics.pose().popPose();
    }

    private int rgbToInt(int r, int g, int b) {
        int red = (r << 16) & 0x00FF0000;
        int green = (g << 8) & 0x0000FF00;
        int blue = b & 0x000000FF;

        return 0xFF000000 | red | green | blue;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta, double deltaY) {
        this.sliderMap.forEach((slider, consumer) -> {
            if (slider.isMouseOver(mouseX, mouseY)) {
                slider.setValue(slider.getValueInt() + (delta > 0 ? 1 : -1));
                consumer.accept(slider.getValueInt());
            }
        });

        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void removed() {
        super.removed();
    }

    private void syncColors() {
        PacketDistributor.sendToServer(new ChangeColorPayload(new CodecHelpers.LaserColor(this.red, this.green, this.blue, this.red_inner, this.green_inner, this.blue_inner)));
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        InputConstants.Key mouseKey = InputConstants.getKey(p_keyPressed_1_, p_keyPressed_2_);
        if (p_keyPressed_1_ == 256) {
            syncColors();
            ModScreens.openGadgetSettingsScreen(this.gadget);
            return true;
        }

        if (getMinecraft().options.keyInventory.isActiveAndMatches(mouseKey)) {
            syncColors();
            removed();
            return true;
        }


        return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }
}
