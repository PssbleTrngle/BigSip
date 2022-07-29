package com.possible_triangle.bigsip.modules

import com.jozufozu.flywheel.core.PartialModel
import com.possible_triangle.bigsip.BigSip
import com.possible_triangle.bigsip.Registration
import com.possible_triangle.bigsip.block.MaturingBarrelBlock
import com.possible_triangle.bigsip.block.MaturingBarrelCT
import com.possible_triangle.bigsip.block.tile.MaturingBarrelTile
import com.possible_triangle.bigsip.config.Configs
import com.possible_triangle.bigsip.data.generation.recipes.RecipeBuilder
import com.possible_triangle.bigsip.item.MaturingBarrelItem
import com.possible_triangle.bigsip.recipe.ConfigCondition
import com.possible_triangle.bigsip.recipe.MaturingRecipe
import com.simibubi.create.AllBlocks
import com.simibubi.create.content.contraptions.fluids.tank.FluidTankTileEntity
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeSerializer
import com.simibubi.create.foundation.block.connected.AllCTTypes
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry
import com.simibubi.create.foundation.block.connected.CTSpriteShifter
import com.simibubi.create.foundation.data.CreateRegistrate
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.common.Tags
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction
import thedarkcolour.kotlinforforge.forge.registerObject

object MaturingModule : ModModule {

    val BARREL by Registration.BLOCKS.registerObject("maturing_barrel") { MaturingBarrelBlock() }
    val BARREL_ITEM by Registration.ITEMS.registerObject("maturing_barrel") {
        MaturingBarrelItem(
            BARREL,
            Registration.Properties
        )
    }
    val BARREL_TILE by Registration.TILES.registerObject("maturing_barrel") {
        BlockEntityType.Builder.of(::MaturingBarrelTile, BARREL).build(null)
    }

    val BARREL_CT_FRONT = createBarrelCT("front")
    val BARREL_CT_SIDE = createBarrelCT("side")
    val BARREL_CT_TOP = createBarrelCT("top")
    val BARREL_CT_BOTTOM = createBarrelCT("bottom")
    val BARREL_MULTIBLOCK = PartialModel(ResourceLocation(BigSip.MOD_ID, "block/multiblock_maturing_barrel"))

    private fun createBarrelCT(side: String): CTSpriteShiftEntry {
        return CTSpriteShifter.getCT(
            AllCTTypes.CROSS,
            ResourceLocation(BigSip.MOD_ID, "block/maturing_barrel_$side"),
            ResourceLocation(
                BigSip.MOD_ID, "block/maturing_barrel_${side}_connected"
            )
        );
    }

    val MATURING_RECIPE = Registration.RECIPES.register(MaturingRecipe.ID) {
        Registration.createRecipeType<MaturingRecipe>(MaturingRecipe.ID)
    }

    val MATURING_RECIPE_SERIALIZER by Registration.RECIPE_SERIALIZERS.registerObject(MaturingRecipe.ID) {
        ProcessingRecipeSerializer(::MaturingRecipe)
    }

    val isEnabled = ConfigCondition(Configs.SERVER.ENABLE_MATURING)

    override fun addConditions(builder: IConditionBuilder) {
        builder.register(BARREL_ITEM) { isEnabled.test() }
    }

    override fun generateRecipes(builder: RecipeBuilder) {
        builder.shapeless(BARREL_ITEM) {
            requires(Tags.Items.BARRELS_WOODEN)
            requires(Items.IRON_BARS)
            unlockedBy("has_hops", builder.hasItem(AlcoholModule.HOPS_TAG))
            unlockedBy("has_grapes", builder.hasItem(GrapesModule.GRAPES_TAG))
        }
    }

    override fun registerCTM() {
        CreateRegistrate.connectedTextures<Block> { MaturingBarrelCT { it.`is`(BARREL) } }.accept(BARREL)
    }

    override fun registerPonders() {
        Registration.PONDER_HELPER
            .addStoryBoard(BARREL.registryName, "barrel/construction", { scene, util ->
                scene.title("maturing_barrel_construction", "Filling the maturing barrel")
                scene.configureBasePlate(0, 0, 6)
                scene.showBasePlate()
                scene.idle(5)

                val smallBarrelBase = util.grid.at(1, 1, 2)

                scene.world.showSection(util.select.fromTo(smallBarrelBase, smallBarrelBase.above()), Direction.DOWN)

                scene.idle(20)
                scene.world.showSection(
                    util.select.fromTo(smallBarrelBase.south(), smallBarrelBase.south().above()),
                    Direction.DOWN
                )
                scene.overlay.showText(60)
                    .text("The Maturing barrel is a multiblock structure")
                    .placeNearTarget()
                    .pointAt(util.vector.blockSurface(smallBarrelBase.above(), Direction.WEST))

                scene.idleSeconds(3)

                val bigBarrelBase = util.grid.at(3, 1, 1)

                scene.world.showSection(
                    util.select.fromTo(bigBarrelBase, bigBarrelBase.offset(1, 2, 2)),
                    Direction.DOWN
                )
                scene.overlay.showText(60)
                    .text("It can be up to 2x2x3 blocks big")
                    .attachKeyFrame()
                    .placeNearTarget()
                    .pointAt(util.vector.blockSurface(bigBarrelBase.above(), Direction.NORTH))

                scene.idleSeconds(2)
            })

        Registration.PONDER_HELPER
            .addStoryBoard(BARREL.registryName, "barrel/usage", { scene, util ->
                scene.title("maturing_barrel_usage", "Filling the maturing barrel")
                scene.configureBasePlate(0, 0, 5)
                scene.showBasePlate()
                scene.idle(5)

                val barrel = util.select.fromTo(2, 1, 1, 3, 3, 3)
                val pumpInPos = util.grid.at(4, 2, 1)
                val pumpIn = util.select.position(pumpInPos)
                val inputPipe = util.select.fromTo(5, 0, 1, 5, 2, 1).add(pumpIn)
                val juiceSource = util.grid.at(5, -1, 1)
                val outputTank = util.grid.at(2, 2, 2)

                val smallCog = util.select.position(1, 2, 4)
                val bigCog = util.select.position(1, 3, 3)
                val outputCogs = smallCog.add(bigCog)
                val output = util.select.fromTo(0, 1, 2, 1, 2, 2).add(outputCogs)
                val pumpOutPos = util.grid.at(1, 2, 2)
                val pumpOut = util.select.position(pumpOutPos)

                scene.world.setKineticSpeed(pumpIn, 0F)
                scene.world.setKineticSpeed(pumpOut, 0F)
                scene.world.showSection(barrel, Direction.DOWN)

                scene.idleSeconds(1)

                scene.world.setBlock(juiceSource, AllBlocks.FLUID_TANK.defaultState, false)
                scene.world.modifyTileEntity(juiceSource, FluidTankTileEntity::class.java) {
                    val juice = FluidStack(JuiceModule.GRAPE_JUICE.getFluid(), 1000)
                    it.tankInventory.fill(juice, FluidAction.EXECUTE)
                }

                scene.world.showSection(inputPipe, Direction.NORTH)
                scene.idleSeconds(1)
                scene.world.setKineticSpeed(pumpIn, 128F)
                scene.world.propagatePipeChange(pumpInPos)

                scene.overlay.showText(60)
                    .text("Liquids like grape juice can be pumped in to be fermented")
                    .attachKeyFrame()
                    .placeNearTarget()
                    .pointAt(util.vector.blockSurface(outputTank.north(), Direction.NORTH))

                scene.idleSeconds(5)
                scene.addKeyframe()

                scene.world.showSection(output, Direction.EAST)
                scene.idleSeconds(1)
                scene.world.setKineticSpeed(pumpOut, 256F)
                scene.world.setKineticSpeed(smallCog, -256F)
                scene.world.setKineticSpeed(bigCog, 128F)

                scene.world.modifyTileEntity(outputTank, MaturingBarrelTile::class.java) {
                    val wine = FluidStack(AlcoholModule.WINE_BOTTLE.getFluid(), 12000)
                    val controller = it.getControllerTE<MaturingBarrelTile>() ?: return@modifyTileEntity
                    val tank = controller.getTank(0)
                    tank.drain(tank.fluidAmount, FluidAction.EXECUTE)
                    tank.fill(wine, FluidAction.EXECUTE)
                }
                scene.world.propagatePipeChange(pumpOutPos)

                scene.idleSeconds(2)
            })
    }

}