package com.possible_triangle.bigsip.alcohol

import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import net.minecraftforge.common.util.LazyOptional

class EntityAlcoholLevel : IAlcoholLevel, ICapabilitySerializable<CompoundTag> {

    override var current: Float = 0F

    override var persistent: Float = 0F

    private val optional = LazyOptional.of { this }

    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        return if (cap == AlcoholHelper.ALCOHOL_LEVEL) optional.cast()
        else LazyOptional.empty()
    }

    override fun serializeNBT(): CompoundTag {
        return CompoundTag().apply {
            putFloat("current", current)
            putFloat("persistent", persistent)
        }
    }

    override fun deserializeNBT(nbt: CompoundTag) {
        if (nbt.contains("current", 99)) current = nbt.getFloat("current")
        if (nbt.contains("persistent", 99)) persistent = nbt.getFloat("persistent")
    }
}