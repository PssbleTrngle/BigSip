package com.possible_triangle.bigsip.compat.jei

import com.possible_triangle.bigsip.BigSip
import com.possible_triangle.bigsip.Registration
import com.possible_triangle.bigsip.modules.ModModule
import com.possible_triangle.bigsip.modules.ServerLoadingContext
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.constants.VanillaTypes
import mezz.jei.api.forge.ForgeTypes
import mezz.jei.api.ingredients.subtypes.UidContext
import mezz.jei.api.registration.IRecipeCatalystRegistration
import mezz.jei.api.registration.IRecipeCategoryRegistration
import mezz.jei.api.registration.IRecipeRegistration
import mezz.jei.api.registration.ISubtypeRegistration
import mezz.jei.api.runtime.IJeiRuntime
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.ItemStack
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.server.ServerLifecycleHooks


@JeiPlugin
class JEIPlugin : IModPlugin {

    override fun getPluginUid() = ResourceLocation(BigSip.MOD_ID, "jei")

    override fun registerCategories(registration: IRecipeCategoryRegistration) {
        registration.addRecipeCategories(MaturingCategory)
    }

    override fun registerRecipes(registration: IRecipeRegistration) {
        MaturingCategory.registerRecipes(registration)
    }

    override fun registerRecipeCatalysts(registration: IRecipeCatalystRegistration) {
        MaturingCategory.registerCatalysts(registration)
    }

    override fun registerItemSubtypes(registration: ISubtypeRegistration) {
        Registration.DRINKS.forEach {
            registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, it) { stack, ctx ->
                if (ctx == UidContext.Recipe) ""
                else stack.damageValue.toString()
            }
        }
    }

    override fun onRuntimeAvailable(runtime: IJeiRuntime) {
        val server = ServerLifecycleHooks.getCurrentServer()!!
        val context = ServerLoadingContext(server)

        val hiddenFluids = ModModule.hiddenFluids(context)
        val hiddenItems = ModModule.hiddenItems(context)

        hiddenItems.map(::ItemStack).takeIf { it.isNotEmpty() }?.let { hidden ->
            runtime.ingredientManager.removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, hidden)
        }

        val buckets = Registration.ITEMS.entries.map { it.get() }.filterIsInstance<BucketItem>()
        hiddenFluids.takeIf { it.isNotEmpty() }?.let { hidden ->
            val stacks = hidden.map { FluidStack(it, 1) }
            runtime.ingredientManager.removeIngredientsAtRuntime(ForgeTypes.FLUID_STACK, stacks)
        }

        val hiddenBuckets = buckets.filter { hiddenFluids.contains(it.fluid) }.map(::ItemStack)
        if (hiddenBuckets.isNotEmpty()) {
            runtime.ingredientManager.removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, hiddenBuckets)
        }
    }

}