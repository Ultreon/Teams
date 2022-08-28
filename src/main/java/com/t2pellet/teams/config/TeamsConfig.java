package com.t2pellet.teams.config;

import com.t2pellet.teams.TeamsMod;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class TeamsConfig {
    public static final ForgeConfigSpec.Builder builder;

    public static final ForgeConfigSpec.BooleanValue SHOW_INVISIBLE_TEAMMATES;
    public static final ForgeConfigSpec.BooleanValue FRIENDLY_FIRE_ENABLED;
    public static final ForgeConfigSpec.EnumValue<Team.Visible> NAME_TAG_VISIBILITY;
    public static final ForgeConfigSpec.EnumValue<TextFormatting> COLOUR;
    public static final ForgeConfigSpec.EnumValue<Team.Visible> DEATH_MESSAGE_VISIBILITY;
    public static final ForgeConfigSpec.EnumValue<Team.CollisionRule> COLLISION_RULE;
    public static final ForgeConfigSpec.BooleanValue ENABLE_COMPASS_HUD;
    public static final ForgeConfigSpec.BooleanValue ENABLE_STATUS_HUD;
    public static final ForgeConfigSpec.IntValue TOAST_DURATION;

    static {
        builder = new ForgeConfigSpec.Builder();

        builder.push("teamDefaults");
        SHOW_INVISIBLE_TEAMMATES = builder.comment("Show Invisible Teammates").define("showInvisibleTeammates", true);
        FRIENDLY_FIRE_ENABLED = builder.comment("Friendly Fire Enabled").define("friendlyFireEnabled", false);
        NAME_TAG_VISIBILITY = builder.comment("Name Tag Visibility").defineEnum("nameTagVisibility", Team.Visible.ALWAYS);
        COLOUR = builder.comment("Colour").defineEnum("colour", TextFormatting.BOLD);
        DEATH_MESSAGE_VISIBILITY = builder.comment("Death Message Visibility").defineEnum("deathMessageVisibility", Team.Visible.ALWAYS);
        COLLISION_RULE = builder.comment("Collision Rule", "Note that 'push own team' and 'push other teams' are swapped.").defineEnum("collisionRule", Team.CollisionRule.PUSH_OWN_TEAM);
        builder.pop();

        builder.push("Visual");
        ENABLE_COMPASS_HUD = builder.comment("Enable Compass HUD").define("enableCompassHUD", true);
        ENABLE_STATUS_HUD = builder.comment("Enable Status HUD").define("enableStatusHUD", true);
        TOAST_DURATION = builder.comment("Toast Duration").defineInRange("toastDuration", 5, 0, Integer.MAX_VALUE);
        builder.pop();
    }

    public static void register(ModLoadingContext ctx) {
        ctx.registerConfig(ModConfig.Type.CLIENT, builder.build(), TeamsMod.MODID + ".toml");
    }
}
