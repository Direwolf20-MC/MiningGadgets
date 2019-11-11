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

    public MiningVisualsScreen(ItemStack gadget) {
        super(new StringTextComponent("title"));
        this.gadget = gadget;
        this.red = MiningProperties.getColor(gadget, MiningProperties.COLOR_RED);
        this.green = MiningProperties.getColor(gadget, MiningProperties.COLOR_GREEN);
        this.blue = MiningProperties.getColor(gadget, MiningProperties.COLOR_BLUE);
    }

    @Override
    protected void init() {
        int baseX = width / 2, baseY = height / 2;
        blockBreakButton = new Button(baseX - (150 / 2), baseY - 50, 150, 20, "Toggle Break Visual", (button) -> {
            PacketHandler.sendToServer(new PacketChangeBreakType());
        });
        addButton(blockBreakButton);

        addButton(new GuiSlider(baseX - (150 / 2), baseY - 25, 150, 20, "Red: ", "", 0, 255, this.red, false, true, s -> {
        }, this));
        addButton(new GuiSlider(baseX - (150 / 2), baseY + 5, 150, 20, "Green: ", "", 0, 255, this.green, false, true, s -> {
        }, this));
        addButton(new GuiSlider(baseX - (150 / 2), baseY + 35, 150, 20, "Blue: ", "", 0, 255, this.blue, false, true, s -> {
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
        PacketHandler.sendToServer(new PacketChangeColor(this.red, this.green, this.blue));
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        if (slider.dispString == "Red: ") {
            this.red = slider.getValueInt();
        } else if (slider.dispString == "Green: ") {
            this.green = slider.getValueInt();
        } else if (slider.dispString == "Blue: ") {
            this.blue = slider.getValueInt();
        }
    }
}
