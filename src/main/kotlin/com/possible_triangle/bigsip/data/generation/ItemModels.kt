package com.possible_triangle.bigsip.data.generation

import com.possible_triangle.bigsip.BigSip
import com.possible_triangle.bigsip.Content
import net.minecraft.data.DataGenerator
import net.minecraft.item.BucketItem
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.generators.ItemModelProvider
import net.minecraftforge.client.model.generators.loaders.DynamicBucketModelBuilder
import net.minecraftforge.common.data.ExistingFileHelper

class ItemModels(generator: DataGenerator, fileHelper: ExistingFileHelper) :
    ItemModelProvider(generator, BigSip.MOD_ID, fileHelper) {

    private fun loc(name: ResourceLocation, map: (String) -> String): ResourceLocation {
        return ResourceLocation(name.namespace, map(name.path))
    }

    override fun registerModels() {

        val base = mcLoc("item/generated")

        Content.ITEMS.getEntries()
            .mapNotNull { it.get() }
            .forEach { item ->
                val name = item.registryName ?: return
                val stack = ItemStack(item)

                when {
                    item is BucketItem -> {
                        withExistingParent(name.path, ResourceLocation("forge:item/bucket_drip"))
                            .customLoader { a, b -> DynamicBucketModelBuilder.begin(a, b).fluid(item.fluid) }
                    }
                    stack.isDamageableItem -> {

                        val damages = 0 until stack.maxDamage
                        val models = damages.map { level ->
                            singleTexture("${name.path}_$level", base, "layer0", loc(name) { "item/${it}_$level" })
                        }

                        models.foldIndexed(
                            singleTexture(
                                name.path,
                                base,
                                "layer0",
                                loc(name) { "item/${it}_0" })
                        ) { level, base, model ->
                            base.override()
                                .predicate(modLoc("level"), level.toFloat())
                                .model(model)
                                .end()
                        }

                    }
                    else -> singleTexture(name.path, base, "layer0", loc(name) { "item/${it}" })
                }
            }

    }

}