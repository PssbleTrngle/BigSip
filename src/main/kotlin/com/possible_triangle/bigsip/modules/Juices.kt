package com.possible_triangle.bigsip.modules

import com.possible_triangle.bigsip.Registration
import com.possible_triangle.bigsip.item.Drink

object Juices : Module {

    val APPLE_JUICE by Registration.withFluid("apple_juice") { Drink(it, 4, 0.5F) }
    val CARROT_JUICE by Registration.withFluid("carrot_juice") { Drink(it, 4, 0.5F) }
    val GRAPE_JUICE by Registration.withFluid("grape_juice") { Drink(it, 4, 0.5F) }

}