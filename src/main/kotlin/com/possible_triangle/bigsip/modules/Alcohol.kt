package com.possible_triangle.bigsip.modules

import com.possible_triangle.bigsip.Registration
import com.possible_triangle.bigsip.config.Configs
import com.possible_triangle.bigsip.effect.DizzinessEffect
import com.possible_triangle.bigsip.item.Alcohol
import net.minecraft.world.item.Item
import thedarkcolour.kotlinforforge.forge.registerObject

object Alcohol : Module {

    val WINE_BOTTLE by Registration.withFluid("wine", "wine_bottle") { Alcohol(it, 4, 0F, 5, uses = 3) }
    val BEER by Registration.withFluid("beer") { Alcohol(it, 4, 0.2F, 6, uses = 2) }
    val DARK_BEER by Registration.withFluid("dark_beer") { Alcohol(it, 4, 0.2F, 12, uses = 2) }

    val DIZZYNESS by Registration.EFFECTS.registerObject("dizziness") { DizzinessEffect() }

    val HOP by Registration.ITEMS.registerObject("hop") { Item(Registration.Properties) }
    val MASH = Registration.createFluid("mash")

    override fun addConditions(builder: IConditionBuilder) {
        listOf(WINE_BOTTLE, BEER, DARK_BEER).forEach {
            builder.register(it, Configs.SERVER.ENABLE_ALCOHOL::get)
            builder.register(it.getFluid(),  Configs.SERVER.ENABLE_ALCOHOL::get)
        }
    }
}