package com.possible_triangle.bigsip.compat

import com.possible_triangle.bigsip.BigSip
import com.possible_triangle.bigsip.Content
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.ingredients.subtypes.UidContext
import mezz.jei.api.registration.ISubtypeRegistration
import net.minecraft.resources.ResourceLocation

@JeiPlugin
class JEICompat : IModPlugin {

    override fun getPluginUid(): ResourceLocation {
        return ResourceLocation(BigSip.MOD_ID, BigSip.MOD_ID)
    }

    override fun registerItemSubtypes(registration: ISubtypeRegistration) {
        Content.DRINKS.forEach {
            registration.registerSubtypeInterpreter(it) { stack, ctx ->
                if (ctx == UidContext.Recipe) ""
                else stack.damageValue.toString()
            }
        }
    }

}