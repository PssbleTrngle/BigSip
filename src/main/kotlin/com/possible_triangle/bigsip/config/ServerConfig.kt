package com.possible_triangle.bigsip.config

import net.minecraftforge.common.ForgeConfigSpec

class ServerConfig(builder: ForgeConfigSpec.Builder) {

    val ENABLE_ALCOHOL = builder.define("enable_alcohol", true)
    val CAN_ALWAYS_DRINK_ALCOHOL = builder.define("can_always_drink_alcohol", false)

    val ENCHANTABLE_DRINKS = builder.define("enchantable_drinks", false)

    val TAN_JUICE_COMPAT = builder.define("compat.toughasnails.juice_emptying", true)
    val TAN_PURE_WATER_COMPAT = builder.define("compat.toughasnails.pure_water", true)


}