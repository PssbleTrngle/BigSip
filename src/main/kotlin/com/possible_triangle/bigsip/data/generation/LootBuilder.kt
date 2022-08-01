package com.possible_triangle.bigsip.data.generation

import com.mojang.datafixers.util.Pair
import com.possible_triangle.bigsip.BigSip.MOD_ID
import com.possible_triangle.bigsip.modules.ModModule
import net.minecraft.data.DataGenerator
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.ValidationContext
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Supplier

class LootBuilder(generator: DataGenerator) : LootTableProvider(generator) {

    private val tables = hashMapOf<ResourceLocation, kotlin.Pair<LootTable.Builder, LootContextParamSet>>()

    fun add(id: ResourceLocation, table: LootTable.Builder, context: LootContextParamSet) {
        tables[id] = table to context
    }

    fun add(id: String, table: LootTable.Builder, context: LootContextParamSet) =
        add(ResourceLocation(MOD_ID, id), table, context)

    override fun getTables(): MutableList<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> {
        ModModule.forEach { it.generateLoot(this) }

        return tables.map { (id, pair) ->
            val (table, context) = pair
            val register = Consumer { event: BiConsumer<ResourceLocation, LootTable.Builder> ->
                event.accept(id, table)
            }
            Pair(Supplier { register }, context)
        }.toMutableList()
    }

    override fun validate(map: MutableMap<ResourceLocation, LootTable>, validationtracker: ValidationContext) {
        // No validation
    }

}