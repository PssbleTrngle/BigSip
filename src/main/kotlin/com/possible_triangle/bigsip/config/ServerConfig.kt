package com.possible_triangle.bigsip.config

import net.minecraftforge.common.ForgeConfigSpec

class ServerConfig(builder: ForgeConfigSpec.Builder) {

    val ENABLE_ALCOHOL = builder.define("alcohol.enable", true)
    val CAN_ALWAYS_DRINK_ALCOHOL = builder.define("alcohol.can_always_drink", false)
    val ALCOHOL_COOLDOWN = builder.defineInRange("alcohol.cooldown", 20, 0, 20 * 60 * 60)

    val ENCHANTABLE_DRINKS = builder.define("enchantable_drinks", false)

    val ENABLE_MATURING = builder.define("maturing.enabled", true)

    val ENABLE_GRAPES = builder.define("grapes.enabled", true)

    val WINE_CELLAR_CHANCE = builder.defineInRange("grapes.structures.wine_cellar_chance", 50, 0, 2048)
    val PUB_CHANCE = builder.defineInRange("grapes.structures.pub_chance", 5, 0, 2048)

    val TAN_JUICE_COMPAT = builder.define("compat.toughasnails.juice_emptying", true)
    val TAN_JUICE_PURE_WATER = builder.define("compat.toughasnails.juices_use_pure_water", true)
    val TAN_PURE_WATER_COMPAT = builder.define("compat.toughasnails.pure_water", true)


}