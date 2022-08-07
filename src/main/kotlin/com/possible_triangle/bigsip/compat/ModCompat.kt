package com.possible_triangle.bigsip.compat

import net.minecraftforge.fml.ModList

object ModCompat {

    enum class Mod(val id: String) {
        TAN("toughasnails"),
        TOP("theoneprobe");

        override fun toString() = id
    }


    fun <T> runIfLoaded(mod: Mod, runner: () -> T): T? {
        if (ModList.get().isLoaded(mod.id)) return runner()
        return null
    }

}