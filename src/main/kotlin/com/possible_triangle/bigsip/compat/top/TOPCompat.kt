package com.possible_triangle.bigsip.compat.top

import mcjty.theoneprobe.api.ITheOneProbe
import java.util.function.Function

object TOPCompat : Function<ITheOneProbe, Void?> {

    override fun apply(top: ITheOneProbe): Void? {
        top.registerProvider(TOPProgressProvider)
        return null
    }

}