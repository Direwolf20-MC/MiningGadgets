package com.direwolf20.mininggadgets.client.screens;

import com.direwolf20.mininggadgets.common.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.network.PacketHandler;
import com.direwolf20.mininggadgets.common.network.packets.PacketChangeBreakType;
import com.direwolf20.mininggadgets.common.network.packets.PacketChangeColor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.awt.*;

public class MiningVisualsScreen extends Screen implements GuiSlider.ISlider {
    private ItemStack gadget;
    private Button blockBreakButton;
    private int red;
    private int green;
    private int blue;
    private int red_inner;
    private int green_inner;
    private int blue_inner;
    private GuiSlider sliderRedInner;
    private GuiSlider sliderGreenInner;
    private GuiSlider sliderBlueInner;
    private GuiSlider sliderRedOuter;
    private GuiSlider sliderGreenOuter;
    private GuiSlider sliderBlueOuter;

    public MiningVisualsScreen(ItemStack gadget) {
        super(new StringTextComponent("title"));
        this.gadget = gadget;
        this.red = MiningProperties.getColor(gadget, MiningProperties.COLOR_RED);
        this.green = MiningProperties.getColor(gadget, MiningProperties.COLOR_GREEN);
        this.blue = MiningProperties.getColor(gadget, MiningProperties.COLOR_BLUE);
        this.red_inner = MiningProperties.getColor(gadget, MiningProperties.COLOR_RED_INNER);
        this.green_inner = MiningProperties.getColor(gadget, MiningProperties.COLOR_GREEN_INNER);
        this.blue_inner = MiningProperties.getColor(gadget, MiningProperties.COLOR_BLUE_INNER);
    }

    @Override
    protected void init() {
        int baseX = width / 2, baseY = height / 2;
        String buttonText;
        if (MiningProperties.getBreakType(gadget) == MiningProperties.BreakTypes.SHRINK)
            buttonText = new TranslationTextComponent("mininggadgets.tooltip.screen.shrink").getUnformattedComponentText();
        else
            buttonText = new TranslationTextComponent("mininggadgets.tooltip.screen.fade").getUnformattedComponentText();
        blockBreakButton = new Button(baseX - (150 / 2), baseY - 50, 150, 20, buttonText, (button) -> {
            if (blockBreakButton.getMessage().contains("Shrink"))
                button.setMessage(new TranslationTextComponent("mininggadgets.tooltip.screen.fade").getUnformattedComponentText());
            else
                button.setMessage(new TranslationTextComponent("mininggadgets.tooltip.screen.shrink").getUnformattedComponentText());

            PacketHandler.sendToServer(new PacketChangeBreakType());
        });
        addButton(blockBreakButton);

        sliderRedInner = new GuiSlider(baseX - (150), baseY - 25, 150, 20, new TranslationTextComponent("mininggadgets.tooltip.screen.red_outer").getUnformattedComponentText() + ": ", "", 0, 255, this.red, false, true, s -> {
        }, this);
        sliderGreenInner = new GuiSlider(baseX - (150), baseY + 5, 150, 20, new TranslationTextComponent("mininggadgets.tooltip.screen.green_outer").getUnformattedComponentText() + ": ", "", 0, 255, this.green, false, true, s -> {
        }, this);
        sliderBlueInner = new GuiSlider(baseX - (150), baseY + 35, 150, 20, new TranslationTextComponent("mininggadgets.tooltip.screen.blue_outer").getUnformattedComponentText() + ": ", "", 0, 255, this.blue, false, true, s -> {
        }, this);

        sliderRedOuter = new GuiSlider(baseX + (25), baseY - 25, 150, 20, new TranslationTextComponent("mininggadgets.tooltip.screen.red_inner").getUnformattedComponentText() + ": ", "", 0, 255, this.red_inner, false, true, s -> {
        }, this);
        sliderGreenOuter = new GuiSlider(baseX + (25), baseY + 5, 150, 20, new TranslationTextComponent("mininggadgets.tooltip.screen.green_inner").getUnformattedComponentText() + ": ", "", 0, 255, this.green_inner, false, true, s -> {
        }, this);
        sliderBlueOuter = new GuiSlider(baseX + (25), baseY + 35, 150, 20, new TranslationTextComponent("mininggadgets.tooltip.screen.blue_inner").getUnformattedComponentText() + ": ", "", 0, 255, this.blue_inner, false, true, s -> {
        }, this);

        addButton(sliderRedInner);
        addButton(sliderGreenInner);
        addButton(sliderBlueInner);
        addButton(sliderRedOuter);
        addButton(sliderGreenOuter);
        addButton(sliderBlueOuter);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);

        drawCenteredString(getMinecraft().fontRenderer, new TranslationTextComponent("mininggadgets.tooltip.screen.visual_settings").getUnformattedComponentText(), (width / 2), (height / 2) - 70, Color.WHITE.getRGB());
        //drawCenteredString(getMinecraft().fontRenderer, "Toggle Upgrades", (width / 2), (height / 2) + 20, Color.WHITE.getRGB());

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        super.onClose();
        PacketHandler.sendToServer(new PacketChangeColor(this.red, this.green, this.blue, this.red_inner, this.green_inner, this.blue_inner));
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        if (slider.equals(sliderRedInner)) {
            this.red = slider.getValueInt();
        } else if (slider.equals(sliderGreenInner)) {
            this.green = slider.getValueInt();
        } else if (slider.equals(sliderBlueInner)) {
            this.blue = slider.getValueInt();
        } else if (slider.equals(sliderRedOuter)) {
            this.red_inner = slider.getValueInt();
        } else if (slider.equals(sliderGreenOuter)) {
            this.green_inner = slider.getValueInt();
        } else if (slider.equals(sliderBlueOuter)) {
            this.blue_inner = slider.getValueInt();
        }
    }

    public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
        sliderRedInner.dragging = false;
        sliderGreenInner.dragging = false;
        sliderBlueInner.dragging = false;
        sliderRedOuter.dragging = false;
        sliderGreenOuter.dragging = false;
        sliderBlueOuter.dragging = false;
        return false;
    }
}
