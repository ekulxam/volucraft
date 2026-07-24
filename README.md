# Volucraft

3D (Volumetric) Crafting in Minecraft!
I've been waiting for this for so long...

## How to Volucraft
To start, obtain an Amalgamation Table from the Creative Inventory (in Functional Blocks after the Crafting Table).
This currently doesn't have a recipe, but I'm thinking of adding one soon.

## Recipes
Due to the nature of 3D recipes, any orientation of ItemStacks that can be transformed rigidly* to match a recipe is a valid input.

*Rotations, reflections, and translations are all accepted as long as the recipe remains within the bounds of the Amalgamation Table.

Volucraft comes with one builtin datapack with four recipes:
1. Enchanted Golden Apple - 26 Gold Blocks surround 1 Apple in the center.
2. Trident - One Prismarine Shard is placed in the center, then another shard is placed in one of the corners. A bone is placed in the opposite corner of the shard, then the other two bones are placed in adjacent corners.
3. Crying Obsidian (x4) - 4 Obsidian are placed the bottom, then alternating Ghast Tears and Dragon's Breath are placed in the 4 tiles immediately above it, and then 4 more Obsidian are placed above those.
4. Diamond - 27 Coal Blocks

### Creating Your Own Recipe
Volucraft recipes are data-driven, like regular crafting recipes!
They go in the regular data/\<modid\>/recipe folder, but with a different type and pattern. The key and result fields remain identical to regular crafting recipes.
See the example (trident recipe) below:

```json
{
  "type": "volucraft:amalgamation",
  "key": {
    "/": "minecraft:prismarine_shard",
    "V": "minecraft:bone"
  },
  "pattern": [
    [
      "V V",
      "   ",
      "  V"
    ],
    [
      "   ",
      " / ",
      "   "
    ],
    [
      "   ",
      "   ",
      "/  "
    ]
  ],
  "result": {
    "id": "minecraft:trident"
  }
}
```

Note how the pattern consists of 3 regular 2D patterns stacked on top of each other.

As of 0.2.0, Shapeless Recipes (type `volucraft:amalgamation_shapeless`) are also supported.
They have the same format as `crafting_shapeless` recipes, but support up to 27 ingredients instead of the usual 9.

### Viewing Recipes
As of 0.0.3, Volucraft supports recipe viewing via Reliable Recipe Viewer integration!

### 2D Crafting Recipes
As of 0.2.0, the Amalgamation Table now accepts regular crafting recipes.
Currently, only Shaped and Shapeless recipes are supported (that means no special recipes that use components, i.e. fireworks and item durability combining).
2D Shaped and Shapeless recipes are automatically added as Shaped and Shapeless Amalgamation recipes, respectively. 
Note that the id of the extruded recipe is the original recipe's id appended with `_volucraft.autoextruded`.

Extruded recipes are automatically granted and revoked along with their 2D counterparts.

## Configuration
Volucraft can be configured on the client to match your preferences.

<details>
  <summary>Before 0.1.0</summary>

It comes with two builtin resource packs and an optional config powered by YACL.

The two resource packs modify the translucency of the slots.
1. All Translucent Slots - makes all slots render translucent by default, instead of only turning translucent when containing items
2. More Translucent Slots - slots that contain items will become even less opaque than usual

The YACL config controls the color of the background of the portion of the Amalgamation Table screen that contains the cube.

</details>

It comes with an optional config powered by YACL (you will need to install YACL to access the config).
The YACL config controls slot translucency and the background color of the portion of the Amalgamation Table screen that contains the slots.

## Background
This mod was originally made with the intention of bringing it to Modfest 26.<br>
However, after a lack of time and a few bad decisions, Modfest 26's deadline arrived and I didn't have anything to submit.<br>
However, Mod Garden: DIY was starting up, and seeing as I had an amazing mod concept lying around and barely any code to
show for it, I decided to write as much of the rest of the mod (I didn't have a lot) as I could.<br>
Thanks to this, I get to have the first mod (to my knowledge) that can display both ModFest and Mod Garden badges (technically) without lying!
How cool is that?

<a href="https://modfest.net/26"><img alt="Made for Modfest 26" src="https://raw.githubusercontent.com/ModFest/art/f81e87fd98f771c4839c06f34f1e75c092284b78/badge/svg/26/cozy.svg"></a>
<a href="https://modgarden.net/events/mod-garden/diy"><img alt="Made for Mod Garden: DIY" src="https://raw.githubusercontent.com/ModGardenEvent/art/0c0f5b5e0d21f53ad4f4667a73db4d8802944e1f/badge/svg/diy/cozy.svg"></a>

## Special Mentions
Thank you to [Phanastrae](https://github.com/Phanastrae), [Celeste](https://github.com/celdaemon), [Typho](https://github.com/TheTypholorian), and [Cassian](https://github.com/cassiancc) for your assistance!