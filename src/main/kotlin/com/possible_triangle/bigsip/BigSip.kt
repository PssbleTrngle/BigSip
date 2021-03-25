package com.possible_triangle.bigsip

import com.possible_triangle.bigsip.alcohol.AlcoholHelper
import net.minecraftforge.fml.common.Mod

@Mod(BigSip.MOD_ID)
object BigSip {

    const val MOD_ID: String = "bigsip"

    init {
        AlcoholHelper.init()
        Content.register()
    }


}