package com.possible_triangle.bigsip.alcohol

import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.INBT
import net.minecraft.util.Direction
import net.minecraftforge.common.capabilities.Capability

class AlcoholLevel() {

    var current: Int = 0
        internal set

    var persistent: Float = 0F
        internal set

    companion object : Capability.IStorage<AlcoholLevel> {

        override fun writeNBT(capability: Capability<AlcoholLevel>, instance: AlcoholLevel, side: Direction): INBT {
            return with(CompoundNBT()) {
                putInt("current", instance.current)
                putFloat("persistent", instance.persistent)
                this
            }
        }

        override fun readNBT(capability: Capability<AlcoholLevel>, instance: AlcoholLevel, side: Direction, nbt: INBT) {
            if (nbt !is CompoundNBT) throw IllegalArgumentException("Alcohol level must be a CompoundNBT")

            with(nbt) {
                instance.current = if (contains("current", 99)) getInt("current") else 0
                instance.persistent = if (contains("persistent", 99)) getFloat("persistent") else 0F
            }
        }

    }

}