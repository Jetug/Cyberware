package com.nukateam.cyberware.common;

import com.nukateam.ntgl.Config;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class CyberwareConfig {

    public static final ForgeConfigSpec clientSpec;
    public static final ForgeConfigSpec serverSpec;

    public static final ClientConfig CLIENT;
    public static final ServerConfig SERVER;

    public static class ClientConfig {
        public final ForgeConfigSpec.IntValue HUDR;
        public final ForgeConfigSpec.IntValue HUDG;
        public final ForgeConfigSpec.IntValue HUDB;
        public final ForgeConfigSpec.BooleanValue ENABLE_FLOAT;
        public final ForgeConfigSpec.DoubleValue HUDLENS_FLOAT;
        public final ForgeConfigSpec.DoubleValue HUDJACK_FLOAT;

        ClientConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("Client Configuration Settings").push("client");

            HUDR = builder.defineInRange("HUDR", 76, 0, 255);
            HUDG = builder.defineInRange("HUDG", 255, 0, 255);
            HUDB = builder.defineInRange("HUDB", 0, 0, 255);

            ENABLE_FLOAT = builder.define("ENABLE_FLOAT", false);
            HUDLENS_FLOAT = builder.defineInRange("HUDLENS_FLOAT", 0.1, 0, 100);
            HUDJACK_FLOAT = builder.defineInRange("HUDJACK_FLOAT", 0.05, 0, 100);

            builder.pop();
        }
    }

    public static class ServerConfig {
        public final ForgeConfigSpec.IntValue ESSENCE;
        public final ForgeConfigSpec.IntValue CRITICAL_ESSENCE;
        public final ForgeConfigSpec.BooleanValue MOBS_ENABLE_CYBER_ZOMBIES;
        public final ForgeConfigSpec.IntValue MOBS_CYBER_ZOMBIE_WEIGHT;
        public final ForgeConfigSpec.IntValue MOBS_CYBER_ZOMBIE_MIN_PACK;
        public final ForgeConfigSpec.IntValue MOBS_CYBER_ZOMBIE_MAX_PACK;
        public final ForgeConfigSpec.ConfigValue<List<? extends Integer>> MOBS_DIMENSION_IDS;
        public final ForgeConfigSpec.BooleanValue MOBS_IS_DIMENSION_BLACKLIST;
        public final ForgeConfigSpec.BooleanValue MOBS_APPLY_DIMENSION_TO_SPAWNING;
        public final ForgeConfigSpec.BooleanValue MOBS_APPLY_DIMENSION_TO_BEACON;
        public final ForgeConfigSpec.BooleanValue MOBS_ADD_CLOTHES;
        public final ForgeConfigSpec.DoubleValue MOBS_CYBER_ZOMBIE_DROP_RARITY;
        public final ForgeConfigSpec.DoubleValue MOBS_CLOTH_DROP_RARITY;
        public final ForgeConfigSpec.BooleanValue SURGERY_CRAFTING;
        public final ForgeConfigSpec.IntValue TESLA_PER_POWER;
        public final ForgeConfigSpec.BooleanValue DEFAULT_DROP;
        public final ForgeConfigSpec.BooleanValue DEFAULT_KEEP;
        public final ForgeConfigSpec.DoubleValue DROP_CHANCE;
        public final ForgeConfigSpec.BooleanValue ENABLE_KATANA;
        public final ForgeConfigSpec.BooleanValue ENABLE_CLOTHES;
        public final ForgeConfigSpec.BooleanValue ENABLE_CUSTOM_PLAYER_MODEL;
        public final ForgeConfigSpec.DoubleValue ENGINEERING_CHANCE;
        public final ForgeConfigSpec.DoubleValue SCANNER_CHANCE;
        public final ForgeConfigSpec.DoubleValue SCANNER_CHANCE_ADDL;
        public final ForgeConfigSpec.IntValue SCANNER_TIME;
        public final ForgeConfigSpec.ConfigValue<String> FIST_MINING_TOOL_NAME;
        public final ForgeConfigSpec.BooleanValue INT_ENDER_IO;
        public final ForgeConfigSpec.BooleanValue INT_TOUGH_AS_NAILS;
        public final ForgeConfigSpec.BooleanValue INT_BOTANIA;
        public final ForgeConfigSpec.BooleanValue INT_MATTER_OVERDRIVE;

        ServerConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("Server Configuration Settings").push("server");

            ESSENCE = builder.defineInRange("ESSENCE", 100, 0, Integer.MAX_VALUE);
            CRITICAL_ESSENCE = builder.defineInRange("CRITICAL_ESSENCE", 25, 0, Integer.MAX_VALUE);

            builder.push("mobs");
            MOBS_ENABLE_CYBER_ZOMBIES = builder.define("MOBS_ENABLE_CYBER_ZOMBIES", true);
            MOBS_CYBER_ZOMBIE_WEIGHT = builder.defineInRange("MOBS_CYBER_ZOMBIE_WEIGHT", 15, 0, Integer.MAX_VALUE);
            MOBS_CYBER_ZOMBIE_MIN_PACK = builder.defineInRange("MOBS_CYBER_ZOMBIE_MIN_PACK", 1, 0, Integer.MAX_VALUE);
            MOBS_CYBER_ZOMBIE_MAX_PACK = builder.defineInRange("MOBS_CYBER_ZOMBIE_MAX_PACK", 1, 0, Integer.MAX_VALUE);
            MOBS_DIMENSION_IDS = builder.defineList("MOBS_DIMENSION_IDS", Collections.emptyList(), o -> true);
            MOBS_IS_DIMENSION_BLACKLIST = builder.define("MOBS_IS_DIMENSION_BLACKLIST", true);
            MOBS_APPLY_DIMENSION_TO_SPAWNING = builder.define("MOBS_APPLY_DIMENSION_TO_SPAWNING", true);
            MOBS_APPLY_DIMENSION_TO_BEACON = builder.define("MOBS_APPLY_DIMENSION_TO_BEACON", true);
            MOBS_ADD_CLOTHES = builder.define("MOBS_ADD_CLOTHES", true);
            MOBS_CYBER_ZOMBIE_DROP_RARITY = builder.defineInRange("MOBS_CYBER_ZOMBIE_DROP_RARITY", 50.0, 0, 100);
            MOBS_CLOTH_DROP_RARITY = builder.defineInRange("MOBS_CLOTH_DROP_RARITY", 50.0, 0, 100);
            builder.pop();

            SURGERY_CRAFTING = builder.define("SURGERY_CRAFTING", false);
            TESLA_PER_POWER = builder.defineInRange("TESLA_PER_POWER", 1, 0, Integer.MAX_VALUE);

            builder.push("gamerules");
            DEFAULT_DROP = builder.define("DEFAULT_DROP", false);
            DEFAULT_KEEP = builder.define("DEFAULT_KEEP", false);
            DROP_CHANCE = builder.defineInRange("DROP_CHANCE", 100.0, 0, 100);
            builder.pop();

            ENABLE_KATANA = builder.define("ENABLE_KATANA", true);
            ENABLE_CLOTHES = builder.define("ENABLE_CLOTHES", true);
            ENABLE_CUSTOM_PLAYER_MODEL = builder.define("ENABLE_CUSTOM_PLAYER_MODEL", true);

            builder.push("machines");
            ENGINEERING_CHANCE = builder.defineInRange("ENGINEERING_CHANCE", 15.0, 0, 100);
            SCANNER_CHANCE = builder.defineInRange("SCANNER_CHANCE", 10.0, 0, 50);
            SCANNER_CHANCE_ADDL = builder.defineInRange("SCANNER_CHANCE_ADDL", 10.0, 0, 100);
            SCANNER_TIME = builder.defineInRange("SCANNER_TIME", 24000, 0, Integer.MAX_VALUE);
            builder.pop();

            FIST_MINING_TOOL_NAME = builder.define("FIST_MINING_TOOL_NAME", "minecraft:iron_pickaxe");

            builder.push("integration");
            INT_ENDER_IO = builder.define("INT_ENDER_IO", true);
            INT_TOUGH_AS_NAILS = builder.define("INT_TOUGH_AS_NAILS", true);
            INT_BOTANIA = builder.define("INT_BOTANIA", true);
            INT_MATTER_OVERDRIVE = builder.define("INT_MATTER_OVERDRIVE", true);
            builder.pop();

            builder.pop();
        }
    }

    static  {
        final var clientSpecPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        clientSpec = clientSpecPair.getRight();
        CLIENT = clientSpecPair.getLeft();

        final var serverSpecPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        serverSpec = serverSpecPair.getRight();
        SERVER = serverSpecPair.getLeft();
    }
}