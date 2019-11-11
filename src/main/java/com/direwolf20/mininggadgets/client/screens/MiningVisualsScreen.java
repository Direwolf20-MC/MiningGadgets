package com.direwolf20.mininggadgets.client.screens;

import com.direwolf20.mininggadgets.common.gadget.MiningProperties;
import com.direwolf20.mininggadgets.common.network.PacketHandler;
import com.direwolf20.mininggadgets.common.network.packets.PacketChangeBreakType;
import com.direwolf20.mininggadgets.common.network.packets.PacketChangeColor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
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
        blockBreakButton = new Button(baseX - (150 / 2), baseY - 50, 150, 20, "Toggle Break Visual", (button) -> {
            PacketHandler.sendToServer(new PacketChangeBreakType());
        });
        addButton(blockBreakButton);

        addButton(new GuiSlider(baseX - (150), baseY - 25, 150, 20, "Red Outer: ", "", 0, 255, this.red, false, true, s -> {
        }, this));
        addButton(new GuiSlider(baseX - (150), baseY + 5, 150, 20, "Green Outer: ", "", 0, 255, this.green, false, true, s -> {
        }, this));
        addButton(new GuiSlider(baseX - (150), baseY + 35, 150, 20, "Blue Outer: ", "", 0, 255, this.blue, false, true, s -> {
        }, this));

        addButton(new GuiSlider(baseX + (25), baseY - 25, 150, 20, "Red Inner: ", "", 0, 255, this.red_inner, false, true, s -> {
        }, this));
        addButton(new GuiSlider(baseX + (25), baseY + 5, 150, 20, "Green Inner: ", "", 0, 255, this.green_inner, false, true, s -> {
        }, this));
        addButton(new GuiSlider(baseX + (25), baseY + 35, 150, 20, "Blue Inner: ", "", 0, 255, this.blue_inner, false, true, s -> {
        }, this));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);

        drawCenteredString(getMinecraft().fontRenderer, "Visual Settings", (width / 2), (height / 2) - 70, Color.WHITE.getRGB());
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
        if (slider.dispString == "Red Outer: ") {
            this.red = slider.getValueInt();
        } else if (slider.dispString == "Green Outer: ") {
            this.green = slider.getValueInt();
        } else if (slider.dispString == "Blue Outer: ") {
            this.blue = slider.getValueInt();
        } else if (slider.dispString == "Red Inner: ") {
            this.red_inner = slider.getValueInt();
        } else if (slider.dispString == "Green Inner: ") {
            this.green_inner = slider.getValueInt();
        } else if (slider.dispString == "Blue Inner: ") {
            this.blue_inner = slider.getValueInt();
        }
    }
}
