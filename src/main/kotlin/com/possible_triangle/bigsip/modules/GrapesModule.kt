package com.possible_triangle.bigsip.modules

import com.possible_triangle.bigsip.Registration
import com.possible_triangle.bigsip.block.GrapeCrop
import com.possible_triangle.bigsip.config.Configs
import com.possible_triangle.bigsip.data.generation.LootBuilder
import com.possible_triangle.bigsip.data.generation.TagBuilder
import com.possible_triangle.bigsip.data.generation.recipes.RecipeBuilder
import net.minecraft.advancements.critereon.StatePropertiesPredicate
import net.minecraft.world.food.Foods
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemNameBlockItem
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator
import net.minecraftforge.common.Tags
import thedarkcolour.kotlinforforge.forge.registerObject

object GrapesModule : ModModule {

    val GRAPES by Registration.ITEMS.registerObject("grapes") { Item(Registration.Properties.food(Foods.APPLE)) }
    val GRAPES_TAG = Registration.fruitTag("grapes")
    val GRAPE_CROP by Registration.BLOCKS.registerObject("grapes") { GrapeCrop() }
    val GRAPE_SAPLING by Registration.ITEMS.registerObject("grape_sapling") {
        ItemNameBlockItem(
            GRAPE_CROP,
            Registration.Properties
        )
    }

    private val CONDITION = Configs.SERVER.ENABLE_GRAPES

    override fun addConditions(builder: IConditionBuilder) {
        listOf(GRAPES, GRAPE_SAPLING).forEach {
            builder.register(it) { CONDITION.get() }
        }
    }

    override fun generateLoot(builder: LootBuilder) {
        fun poolFor(age: Int): LootPool.Builder {
            val min = age - 3F
            val max = min + 1F
            return LootPool.lootPool()
                .add(
                    LootItem.lootTableItem(GRAPES)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                )
                .`when`(
                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(GRAPE_CROP)
                        .setProperties(
                            StatePropertiesPredicate.Builder.properties()
                                .hasProperty(GRAPE_CROP.ageProperty, age)
                        )
                )
        }

        builder.add(
            "blocks/grapes", LootTable.lootTable()
                .withPool(
                    LootPool.lootPool()
                        .add(LootItem.lootTableItem(GRAPE_CROP))
                )
                .withPool(poolFor(5))
                .withPool(poolFor(6))
                .withPool(poolFor(7)),
            LootContextParamSets.BLOCK
        )
    }

    override fun generateRecipes(builder: RecipeBuilder) {
        builder.shapeless(GRAPE_SAPLING) {
            requires(Tags.Items.RODS_WOODEN)
            requires(GRAPES_TAG)
            unlockedBy("has_grapes", builder.hasItem(GRAPES))
        }

        builder.thermal("grapes", "insolator") {
            output(GRAPES, chance = 2.5F)
            require(GRAPES)
        }
    }

    override fun generateTags(builder: TagBuilder) {
        builder.items.create(GRAPES_TAG) {
            add(GRAPES)
        }
    }

}