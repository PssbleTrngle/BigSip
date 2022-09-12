package com.possible_triangle.bigsip.modules

import com.mojang.datafixers.util.Either
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Lifecycle
import com.possible_triangle.bigsip.BigSip
import com.possible_triangle.bigsip.config.Configs
import com.possible_triangle.bigsip.data.generation.LootBuilder
import com.possible_triangle.bigsip.data.generation.conditions.ConfigLootCondition
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap
import net.minecraft.core.WritableRegistry
import net.minecraft.data.BuiltinRegistries
import net.minecraft.data.worldgen.ProcessorLists
import net.minecraft.data.worldgen.VillagePools
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Items
import net.minecraft.world.level.levelgen.structure.pools.LegacySinglePoolElement
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool.Projection
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator
import java.util.*

class VillageHouse(id: String) :
    LegacySinglePoolElement(Either.left(ResourceLocation(BigSip.MOD_ID, id)), ProcessorLists.EMPTY, Projection.RIGID)

object StructureModule : ModModule {

    private enum class InjectedBiome(val cellar: Boolean = true, val pub: Boolean = true) {
        TAIGA,
        PLAINS,
        SNOWY(cellar = false)
    }

    fun registerVillageHouses() {
        VillagePools.bootstrap()

        val pools = BuiltinRegistries.TEMPLATE_POOL as WritableRegistry<StructureTemplatePool>

        InjectedBiome.values().forEach { inject ->
            val biome = inject.name.lowercase()
            val pool = pools.get(ResourceLocation("village/$biome/houses"))!!
            val id = pools.getId(pool)

            val structures = pool.getShuffledTemplates(Random(0L))
            val pieces = Object2IntLinkedOpenHashMap<StructurePoolElement>()
            structures.forEach {
                pieces.computeInt(it) { _, i -> (i ?: 0) + 1 }
            }

            if (inject.pub) pieces[VillageHouse("village/$biome/pub")] = Configs.SERVER.PUB_CHANCE.get()
            if (inject.cellar) pieces[VillageHouse("village/$biome/wine_cellar")] =
                Configs.SERVER.WINE_CELLAR_CHANCE.get()

            val newPool = StructureTemplatePool(
                pool.name,
                pool.fallback,
                pieces.object2IntEntrySet().filter { it.intValue > 0 }.map { Pair(it.key, it.intValue) })
            pools.registerOrOverride(
                OptionalInt.of(id),
                ResourceKey.create(pools.key(), pool.name),
                newPool,
                Lifecycle.stable()
            )
        }
    }

    override fun generateLoot(builder: LootBuilder) {
        builder.add(
            "chests/village/wine_cellar",
            LootTable.lootTable().withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(3F))
                    .name("grapes")
                    .`when`(ConfigLootCondition.forOption(Configs.SERVER.ENABLE_GRAPES))
                    .add(
                        LootItem.lootTableItem(GrapesModule.GRAPES)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1F, 3F)))
                    )
            ).withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(6F))
                    .name("beverages")
                    .add(LootItem.lootTableItem(Items.GLASS_BOTTLE).setWeight(2))
                    .add(
                        LootItem.lootTableItem(AlcoholModule.WINE_BOTTLE)
                            .`when`(ConfigLootCondition.forOption(Configs.SERVER.ENABLE_ALCOHOL))
                    ).add(
                        LootItem.lootTableItem(JuiceModule.GRAPE_JUICE)
                            .`when`(InvertedLootItemCondition.invert(ConfigLootCondition.forOption(Configs.SERVER.ENABLE_ALCOHOL)))
                    )
            ),
            LootContextParamSets.CHEST
        )

        builder.add(
            "chests/village/pub",
            LootTable.lootTable().withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(2F))
                    .name("food")
                    .add(LootItem.lootTableItem(Items.RABBIT_STEW).setWeight(2))
                    .add(
                        LootItem.lootTableItem(Items.BAKED_POTATO)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1F, 5F)))
                    )
                    .add(LootItem.lootTableItem(Items.SUSPICIOUS_STEW))
            ).withPool(
                LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(6F))
                    .name("beverages")
                    .add(LootItem.lootTableItem(Items.GLASS_BOTTLE).setWeight(6))
                    .add(
                        LootItem.lootTableItem(AlcoholModule.APPLE_WINE)
                            .`when`(ConfigLootCondition.forOption(Configs.SERVER.ENABLE_ALCOHOL))
                    )
                    .add(
                        LootItem.lootTableItem(AlcoholModule.BEER).setWeight(3)
                            .`when`(ConfigLootCondition.forOption(Configs.SERVER.ENABLE_ALCOHOL))
                    )
                    .add(
                        LootItem.lootTableItem(AlcoholModule.DARK_BEER).setWeight(3)
                            .`when`(ConfigLootCondition.forOption(Configs.SERVER.ENABLE_ALCOHOL))
                    ).add(
                        LootItem.lootTableItem(JuiceModule.APPLE_JUICE)
                            .`when`(InvertedLootItemCondition.invert(ConfigLootCondition.forOption(Configs.SERVER.ENABLE_ALCOHOL)))
                    )
            ),
            LootContextParamSets.CHEST
        )
    }

}