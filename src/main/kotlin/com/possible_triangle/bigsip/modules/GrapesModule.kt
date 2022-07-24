package com.possible_triangle.bigsip.modules

import com.possible_triangle.bigsip.Registration
import com.possible_triangle.bigsip.block.GrapeCrop
import com.possible_triangle.bigsip.data.generation.TagBuilder
import com.possible_triangle.bigsip.data.generation.recipes.RecipeBuilder
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemNameBlockItem
import net.minecraftforge.common.Tags
import thedarkcolour.kotlinforforge.forge.registerObject

object GrapesModule : Module {

    val GRAPES by Registration.ITEMS.registerObject("grapes") { Item(Registration.Properties) }
    val GRAPES_TAGS = TagKey.create(Registry.ITEM_REGISTRY, ResourceLocation("forge", "fruits/grapes"))
    val GRAPE_CROP by Registration.BLOCKS.registerObject("grapes") { GrapeCrop() }
    val GRAPE_SAPLING by Registration.ITEMS.registerObject("grape_sapling") {
        ItemNameBlockItem(GRAPE_CROP,
            Registration.Properties)
    }

    override fun generateRecipes(builder: RecipeBuilder) {
        builder.shapeless(GRAPE_SAPLING) {
            requires(Tags.Items.RODS_WOODEN)
            requires(GRAPES_TAGS)
            unlockedBy("has_grapes", builder.hasItem(GRAPES))
        }

        builder.thermal("grapes", "insolator") {
            output(GRAPES, chance = 2.5F)
            require(GRAPES)
        }
    }

    override fun generateTags(builder: TagBuilder) {
        builder.items.create(GRAPES_TAGS) {
            add(GRAPES)
        }
    }

}