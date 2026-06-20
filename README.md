# Volucraft

3D (Volumetric) Crafting in Minecraft!
I've been waiting for this for so long...

## How to Volucraft
To start, obtain an Amalgamation Table from the Creative Inventory (in Functional Blocks after the Crafting Table).
This currently doesn't have a recipe, but I'm thinking of adding one soon.

## Recipes
Due to the nature of 3D recipes, any orientation of ItemStacks that can be reflected or rotated to match a recipe is a valid input.

Volucraft comes with one builtin datapack with two recipes:
1. Enchanted Golden Apple - 26 Gold Blocks surrounding 1 Apple in the center
2. Trident - One Prismarine Shard is placed in the center, then another shard is placed in one of the corners. A bone is placed in the opposite corner of the shard, then the other two bones are placed in adjacent corners.

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

Note how the pattern is 3 regular 2D patterns stacked on top of each other.

## Configuration
Volucraft can be configured on the client to match your preferences.
It comes with two builtin resource packs and an optional config powered by YACL.

The two resource packs modify the translucency of the slots.
1. All Translucent Slots - makes all slots render translucent by default, instead of only turning translucent when containing items
2. More Translucent Slots - slots that contain items will become even less opaque than usual

The YACL config controls the color of the background of the portion of the Amalgamation Table screen that contains the cube.

## Special Mentions
Thank you to [Phanastrae](https://github.com/Phanastrae), [Celeste](https://github.com/celdaemon), and [Typho](https://github.com/TheTypholorian) for your assistance!