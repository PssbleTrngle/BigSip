package com.possible_triangle.bigsip.fluid

import com.possible_triangle.bigsip.BigSip
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.FluidState
import net.minecraftforge.fluids.FluidAttributes
import net.minecraftforge.fluids.ForgeFlowingFluid
import java.util.function.Supplier

class ModFluid(
    name: String,
    private val source: Boolean,
    bucket: (() -> Item)?,
    flowing: Supplier<Fluid>,
    still: Supplier<Fluid>,
    viscosity: Int = 300
) : ForgeFlowingFluid(
    Properties(
        still, flowing, FluidAttributes.builder(
            ResourceLocation(BigSip.MOD_ID, "fluid/${name}_still"),
            ResourceLocation(BigSip.MOD_ID, "fluid/${name}_flow"),
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