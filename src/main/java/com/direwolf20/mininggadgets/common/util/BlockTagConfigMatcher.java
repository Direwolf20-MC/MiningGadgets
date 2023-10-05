package com.direwolf20.mininggadgets.common.util;

import com.direwolf20.mininggadgets.common.Config;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.regex.Pattern;

// Borrowed from FTB Ulitmine by LatvianModder
// original code found here https://github.com/FTBTeam/FTB-Ultimine/blob/b4b7a11db0adfd7296fb17ac01f9088636188764/common/src/main/java/dev/ftb/mods/ftbultimine/config/FTBUltimineServerConfig.java#L135-L198
/**
 * @author LatvianModder
 */
public class BlockTagConfigMatcher {

    public static final BlockTagConfigMatcher MERGE_TAGS_SHAPELESS = new BlockTagConfigMatcher(Config.MERGE_TAGS_SHAPELESS);
    public static final BlockTagConfigMatcher MERGE_TAGS_SHAPED = new BlockTagConfigMatcher(Config.MERGE_TAGS_SHAPED);

    private final ForgeConfigSpec.ConfigValue<List<? extends String>> value;

    private Set<TagKey<Block>> tags = null;
    private boolean matchAny = false;

    private BlockTagConfigMatcher(ForgeConfigSpec.ConfigValue<List<? extends String>> value) {
        this.value = value;
    }

    public boolean match(BlockState original, BlockState toTest) {
        var tags = getTags();
        return matchAny && !toTest.isAir() && !(toTest.getBlock() instanceof LiquidBlock) && !(original.getBlock() instanceof EntityBlock)
                || tags.stream().filter(original::is).anyMatch(toTest::is);
    }

    public Collection<TagKey<Block>> getTags() {
        if (tags == null) {
            if (value.get().contains("*")) {
                // special-case: this makes for far faster matching when we just want to match everything
                matchAny = true;
                tags = Collections.emptySet();
            } else {
                tags = new HashSet<>();
                value.get().forEach(s -> {
                    ResourceLocation rl = ResourceLocation.tryParse(s);
                    if (rl != null) {
                        tags.add(TagKey.create(Registries.BLOCK, rl));
                    } else {
                        Pattern pattern = regexFromGlobString(s);
                        ForgeRegistries.BLOCKS.tags().forEach((tag) -> {
                            if (pattern.asPredicate().test(tag.toString())) {
                                tags.add(tag.getKey());
                            }
                        });
                    }
                });
            }
        }
        return tags;
    }

    private static Pattern regexFromGlobString(String glob) {
        StringBuilder sb = new StringBuilder();
        sb.append("^");
        for (int i = 0; i < glob.length(); i++) {
            char c = glob.charAt(i);
            if (c == '*') {
                sb.append(".*");
            } else if (c == '?') {
                sb.append(".");
            } else if (c == '.') {
                sb.append("\\.");
            } else if (c == '\\') {
                sb.append("\\\\");
            } else {
                sb.append(c);
            }
        }
        sb.append("$");
        return Pattern.compile(sb.toString());
    }
}
