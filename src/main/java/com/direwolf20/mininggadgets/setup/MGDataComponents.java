package com.direwolf20.mininggadgets.setup;

import com.direwolf20.mininggadgets.common.MiningGadgets;
import com.direwolf20.mininggadgets.common.containers.handlers.DireItemContainerContents;
import com.direwolf20.mininggadgets.common.util.CodecHelpers;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MGDataComponents {
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.createDataComponents(MiningGadgets.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DireItemContainerContents>> ITEMSTACK_HANDLER = COMPONENTS.register("itemstack_handler", () -> DataComponentType.<DireItemContainerContents>builder().persistent(DireItemContainerContents.CODEC).networkSynchronized(DireItemContainerContents.STREAM_CODEC).cacheEncoding().build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FORGE_ENERGY = COMPONENTS.register("forge_energy", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FORGE_ENERGY_MAX_ENERGY = COMPONENTS.register("forge_energy_max_energy", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<CodecHelpers.UpgradeData>>> UPGRADE_DATA = COMPONENTS.register("upgrade_data", () -> DataComponentType.<List<CodecHelpers.UpgradeData>>builder().persistent(CodecHelpers.UpgradeData.LIST_CODEC).networkSynchronized(CodecHelpers.UpgradeData.STREAM_CODEC.apply(ByteBufCodecs.list())).build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CodecHelpers.LaserColor>> LASER_COLOR = COMPONENTS.register("laser_color", () -> DataComponentType.<CodecHelpers.LaserColor>builder().persistent(CodecHelpers.LaserColor.CODEC).networkSynchronized(CodecHelpers.LaserColor.STREAM_CODEC).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Byte>> BREAK_TYPE = COMPONENTS.register("break_type", () -> DataComponentType.<Byte>builder().persistent(Codec.BYTE).networkSynchronized(ByteBufCodecs.BYTE).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SPEED = COMPONENTS.register("speed", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> RANGE = COMPONENTS.register("range", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> BEAM_RANGE = COMPONENTS.register("beam_range", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MAX_BEAM_RANGE = COMPONENTS.register("max_beam_range", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MAX_MINING_RANGE = COMPONENTS.register("max_mining_range", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> WHITELIST = COMPONENTS.register("whitelist", () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> CAN_MINE = COMPONENTS.register("can_mine", () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> PRECISION_MODE = COMPONENTS.register("precision_mode", () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Byte>> SIZE_MODE = COMPONENTS.register("size_mode", () -> DataComponentType.<Byte>builder().persistent(Codec.BYTE).networkSynchronized(ByteBufCodecs.BYTE).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> VOLUME = COMPONENTS.register("volume", () -> DataComponentType.<Float>builder().persistent(Codec.FLOAT).networkSynchronized(ByteBufCodecs.FLOAT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FREEZE_DELAY = COMPONENTS.register("freeze_delay", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> BATTERY_TIER = COMPONENTS.register("battery_tier", () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT).build());

    //public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ItemStack>>> FILTER_LIST = COMPONENTS.register("filter_list", () -> DataComponentType.<List<ItemStack>>builder().persistent(ItemStack.CODEC.listOf()).networkSynchronized(ItemStack.LIST_STREAM_CODEC).build());

    private static @NotNull <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, final Codec<T> codec) {
        return register(name, codec, null);
    }

    private static @NotNull <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, final Codec<T> codec, @Nullable final StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        if (streamCodec == null) {
            return COMPONENTS.register(name, () -> DataComponentType.<T>builder().persistent(codec).build());
        } else {
            return COMPONENTS.register(name, () -> DataComponentType.<T>builder().persistent(codec).networkSynchronized(streamCodec).build());
        }
    }

}
