package com.possible_triangle.bigsip.compat.jei

import com.possible_triangle.bigsip.BigSip
import com.possible_triangle.bigsip.Registration
import com.possible_triangle.bigsip.modules.MaturingModule
import com.possible_triangle.bigsip.modules.Module
import com.possible_triangle.bigsip.modules.ServerLoadingContext
import com.possible_triangle.bigsip.recipe.MaturingRecipe
import com.simibubi.create.compat.jei.category.CreateRecipeCategory
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.constants.VanillaTypes
import mezz.jei.api.ingredients.subtypes.UidContext
import mezz.jei.api.registration.IRecipeCatalystRegistration
import mezz.jei.api.registration.IRecipeCategoryRegistration
import mezz.jei.api.registration.IRecipeRegistration
import mezz.jei.api.registration.ISubtypeRegistration
import mezz.jei.api.runtime.IJeiRuntime
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.Container
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.server.ServerLifecycleHooks


@JeiPlugin
class JEIPlugin : IModPlugin {

    override fun getPluginUid(): ResourceLocation {
        return ResourceLocation(BigSip.MOD_ID, BigSip.MOD_ID)
    }

    override fun registerCategories(registration: IRecipeCategoryRegistration) {
        registration.addRecipeCategories(MaturingCategory)
    }

    override fun registerRecipes(registration: IRecipeRegistration) {
        val minecraft = Minecraft.getInstance()
        val world = minecraft.level!!

        fun <C : Container, T : Recipe<C>> IRecipeRegistration.addRecipes(
            category: CreateRecipeCategory<T>,
            type: RecipeType<T>,
        ) {
            val recipes = world.recipeManager.getAllRecipesFor(type)
            addRecipes(category.recipeType, recipes)
        }

        registration.addRecipes(MaturingCategory, MaturingRecipe.INFO.getType())

    }

    override fun registerRecipeCatalysts(registration: IRecipeCatalystRegistration) {
        registration.addRecipeCatalyst(ItemStack(MaturingModule.BARREL_ITEM), MaturingCategory.recipeType)
    }

    override fun registerItemSubtypes(registration: ISubtypeRegistration) {
        Registration.DRINKS.forEach {
            registration.registerSubtypeInterpreter(it) { stack, ctx ->
                if (ctx == UidContext.Recipe) ""
                else stack.damageValue.toString()
            }
        }
    }

    override fun onRuntimeAvailable(runtime: IJeiRuntime) {
        val server = ServerLifecycleHooks.getCurrentServer()!!
        val context = ServerLoadingContext(server)

        val hiddenFluids = Module.hiddenFluids(context)
        val hiddenItems = Module.hiddenItems(context)

        hiddenItems.map(::ItemStack).takeIf { it.isNotEmpty() }?.let { hidden ->
            runtime.ingredientManager.removeIngredientsAtRuntime(VanillaTypes.ITEM, hidden)
        }

        val buckets = Registration.ITEMS.entries.map { it.get() }.filterIsInstance<BucketItem>()
        hiddenFluids.takeIf { it.isNotEmpty() }?.let { hidden ->
            val stacks = hidden.map { FluidStack(it, 1) }
            runtime.ingredientManager.removeIngredientsAtRuntime(VanillaTypes.FLUID, stacks)
        }

        val hiddenBuckets = buckets.filter {  hiddenFluids.contains(it.fluid) }.map(::ItemStack)
        if (hiddenBuckets.isNotEmpty()) {
            runtime.ingredientManager.removeIngredientsAtRuntime(VanillaTypes.ITEM, hiddenBuckets)
        }
    }

}