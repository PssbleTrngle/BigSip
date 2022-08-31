[JEI]: https://www.curseforge.com/minecraft/mc-mods/jei
[CREATE]: https://www.curseforge.com/minecraft/mc-mods/create
[THERMAL_CULTIVATION]: https://www.curseforge.com/minecraft/mc-mods/jei
[TAN]: https://www.curseforge.com/minecraft/mc-mods/tough-as-nails
[ISSUES]: https://github.com/PssbleTrngle/BigSip/issues
[DOWNLOAD]: https://www.curseforge.com/minecraft/mc-mods/big-sip/files
[CURSEFORGE]: https://www.curseforge.com/minecraft/mc-mods/big-sip
[MODRINTH]: https://modrinth.com/mod/big-sip

<!-- modrinth_exclude.start -->
# Big Sip
[![Release](https://img.shields.io/github/v/release/PssbleTrngle/SliceAndDice?label=Version&sort=semver)][DOWNLOAD]
[![Downloads](http://cf.way2muchnoise.eu/full_slice-and-dice_downloads.svg)][CURSEFORGE]
[![Version](http://cf.way2muchnoise.eu/versions/slice-and-dice.svg)][DOWNLOAD]
[![Issues](https://img.shields.io/github/issues/PssbleTrngle/SliceAndDice?label=Issues)][ISSUES]
[![Modrinth](https://modrinth-utils.vercel.app/api/badge/downloads?id=GmjmRQ0A&logo=true)][MODRINTH]
<!-- modrinth_exclude.end -->

This is an addon for the [Create Mod][CREATE] and introduces various drinks and other content surrounding them.

<br>
<h3 style='background: #d9391133; border-radius: 1em; border: 1px dashed #d93911; padding: 1em; padding-bottom: 0.2em' align='center'>
⚠️ This mod is not meant to approve of the excessive consumption of alcoholic drinks.

All content referring to alcohol can be disabled in the config.
</h3>
<br>

## Drinks

Some fruits can be turned into juices by compacting them in a basin together with water & sugar
![](https://raw.githubusercontent.com/PssbleTrngle/BigSip/1.18.x/src/main/resources/assets/bigsip/docu/juices.png)

Alcoholic beverages, like wine & beer can be fermented using a [maturing barrel](#maturing-barrel)
![](https://raw.githubusercontent.com/PssbleTrngle/BigSip/1.18.x/src/main/resources/assets/bigsip/docu/alcoholic_drinks.png)


## Maturing Barrel

The maturing barrel is a new multiblock structure, created similar to the fluid tank or the item vault.
It can store liquids and will slowly ferment some of them.

## Structures

This mod adds two village structures, where you can find the new materials and drinks

### Crops

Grapes can be found in the new [village structures](#structures)
![](https://raw.githubusercontent.com/PssbleTrngle/BigSip/1.18.x/src/main/resources/assets/bigsip/docu/grapes.png)

## Mod Compatibility

There is integration for a few mods if they are present:

- All disabled content is automatically hidden from [JEI][JEI]
- All recipes including the [maturing barrel](#maturing-barrel) can be viewed in [JEI][JEI]
- If present, the hops from [thermal cultivation][THERMAL_CULTIVATION] can be used to brew beer
- Any mod that adds barley will enable the recipe for the darker guinness beer
- All drinks work together with the [Tough as Nails][TAN]
- If [Tough as Nails][TAN] is loaded, purified water is needed to create juices instead of normal water (can be disabled)

_Want to suggest compatibility with another mod? [Post it on the tracker][ISSUES]_
