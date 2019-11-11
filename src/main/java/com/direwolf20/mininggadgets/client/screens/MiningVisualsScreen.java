package com.direwolf20.mininggadgets.client.screens;

import com.direwolf20.mininggadgets.common.network.PacketHandler;
import com.direwolf20.mininggadgets.common.network.packets.PacketChangeBreakType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.awt.*;

public class MiningVisualsScreen extends Screen implements GuiSlider.ISlider {
    private ItemStack gadget;
    private Button blockBreakButton;
    //private Button sizeButton;
    //private Button visualButton;

    public MiningVisualsScreen(ItemStack gadget) {
        super(new StringTextComponent("title"));
        this.gadget = gadget;
    }

    @Override
    protected void init() {
        int baseX = width / 2, baseY = height / 2;
        blockBreakButton = new Button(baseX - (150 / 2), baseY - 50, 150, 20, "Toggle Break Visual", (button) -> {
            PacketHandler.sendToServer(new PacketChangeBreakType());
        });
        addButton(blockBreakButton);
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
        //PacketHandler.sendToServer(new PacketChangeRange(this.beamRange));
    }

    @Override
    public void onChangeSliderValue(GuiSlider slider) {
        //this.beamRange = slider.getValueInt();
    }
}
