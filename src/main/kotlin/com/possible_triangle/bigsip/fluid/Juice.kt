package com.possible_triangle.bigsip.fluid

import com.possible_triangle.bigsip.BigSip
import net.minecraft.fluid.Fluid
import net.minecraft.fluid.FluidState
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.FluidAttributes
import net.minecraftforge.fluids.ForgeFlowingFluid
import java.util.function.Supplier

class Juice(
    name: String,
    private val source: Boolean,
    bucket: () -> Item,
    flowing: Supplier<Fluid>,
    still: Supplier<Fluid>,
    viscosity: Int = 300
) : ForgeFlowingFluid(
    Properties(
        still, flowing, FluidAttributes.builder(
            ResourceLocation(BigSip.MOD_ID, "${name}_still"),
            ResourceLocation(BigSip.MOD_ID, "${name}_flowing"),
        ).viscosity(viscosity)
    ).explosionResistance(100F).bucket(bucket)
) {

    override fun getAmount(state: FluidState): Int {
        return state.amount
    }

    override fun isSource(state: FluidState): Boolean {
        return source
    }

}