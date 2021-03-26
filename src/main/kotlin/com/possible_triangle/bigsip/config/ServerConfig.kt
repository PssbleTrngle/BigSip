package com.possible_triangle.bigsip.config

import net.minecraftforge.common.ForgeConfigSpec

class ServerConfig(private val builder: ForgeConfigSpec.Builder) {

    val ENABLE_ALCOHOL = builder.define("enable_alcohol", true)
    val CAN_ALWAYS_DRINK_ALCOHOL = builder.define("can_always_drink_alcohol", false)

    val ENCHANTABLE_DRINKS = builder.define("enchantable_drinks", false)

}