package com.possible_triangle.bigsip.modules

import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.material.Fluid

interface IConditionBuilder {
    fun register(item: ItemLike, predicate: () -> Boolean)
    fun register(fluid: Fluid, predicate: () -> Boolean)
}

class FilteredList<T> {
    private val _values = arrayListOf<Pair<T, () -> Boolean>>()
    fun add(value: T, predicate: () -> Boolean) = _values.add(value to predicate)
    fun clear() = _values.clear()
    val values get() = _values.filterNot { it.second() }.map { it.first }.toList()
}

interface Module {

    companion object {
        private val MODULES = arrayListOf<Module>()
        fun register(module: Module) = MODULES.add(module)

        private val HIDDEN_ITEM_CONDITIONS = FilteredList<ItemLike>()
        val HIDDEN_ITEMS get() = HIDDEN_ITEM_CONDITIONS.values

        private val HIDDEN_FLUID_CONDITIONS = FilteredList<Fluid>()
        val HIDDEN_FLUIDS get() = HIDDEN_FLUID_CONDITIONS.values

        fun registerAll() {
            HIDDEN_ITEM_CONDITIONS.clear()
            HIDDEN_FLUID_CONDITIONS.clear()

            val builder = object : IConditionBuilder {
                override fun register(item: ItemLike, predicate: () -> Boolean) {
                    HIDDEN_ITEM_CONDITIONS.add(item, predicate)
                }

                override fun register(fluid: Fluid, predicate: () -> Boolean) {
                    HIDDEN_FLUID_CONDITIONS.add(fluid, predicate)
                }
            }

            MODULES.forEach { it.addConditions(builder) }
        }
    }

    fun addConditions(builder: IConditionBuilder) {}

}