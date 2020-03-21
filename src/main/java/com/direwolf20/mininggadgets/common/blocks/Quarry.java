package com.direwolf20.mininggadgets.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class Quarry extends Block {
    public Quarry() {
        super(
                Properties.create(Material.IRON).hardnessAndResistance(2.0f)
        );
    }

}
