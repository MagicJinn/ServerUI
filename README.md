# ServerUI

# UI... on the server?

<div style="border: 2px solid #f00; padding-left: 15px; font-size: 1.5em;">
  <h3 style="font-size: 2.5em;">⚠ WARNING</h3>

<ul style="list-style-type: none; padding-left: 0;">
  <li style="margin: 0px 0; font-size: 1.5em;"><strong>Concept ⬅️ <span style="color: #f00;">YOU ARE HERE</span></strong> <span style="font-size: 0.5em;">(don't even think about using this mod)</span></li>
  <li style="margin: 0px 0; font-size: 1.5em;"><strong>Prototype</strong></li>
  <li style="margin: 0px 0; font-size: 1.5em;"><strong>Alpha</strong></li>
  <li style="margin: 0px 0; font-size: 1.5em;"><strong>Beta</strong></li>
  <li style="margin: 0px 0; font-size: 1.5em;"><strong>Release Candidate</strong></li>
  <li style="margin: 0px 0; font-size: 1.5em;"><strong>Release</strong></li>
</ul>
</div>

## What is ServerUI?

ServerUI intends to bring custom UI to fully vanilla clients, through fully serverside code. It will allow vanilla clients to see Custom modded UI, blocks, items, etc, without the need for them to install anything on their end. 

<sup> Note that ServerUI will primarily focus on "Machine Blocks", as the cost of rendering and tracking many serverside blocks can become costly very quickly, making it illsuited for, say, terrain blocks.</sup>

## How will it be done?

ServerUI completely takes over block and item registration, block processes, UI registration, etc. The mod will use a variety of tricks to properly display these new blocks, items and UIs to the client using server resource packs, datapacks, font trickery, serverside mixins, etc.

## The Work that needs to be done

### The Proofs

Before we can proceed, we need to gather proofs. The required proofs are as follows

- [ ] A display entity can be clicked through by the client (a "transparent" display entity)
- [ ] A display entity can be large enough to fully hide a placeholder block under it, but not too large that it looks visually different
- [ ] A custom block can be created
  - [ ] It can be displayed with any model and texture
  - [x] It can be clicked by the client without consequences (no UI opening, no sound playing)
  - [x] It can be mined by the client (correct sound plays, correct tool is used)
  - [ ] It can display a mining animation to the client
  - [ ] It can drop a custom item when mined
  - [ ] It can be placed by the client by a custom item
- [ ] A custom item can be created
  - [ ] It can be displayed with any model and texture
  - [ ] It can exist without wrongfully overwriting a vanilla item appearance
  - [ ] It can have a custom name without looking renamed (*italic* text)
- [ ] A custom block can exist in the world without wrongly interacting with said world
  - [ ] It cannot be incorrectly destroyed or placed in unintended ways (TNT, Pistons, mobs, Dispensers)
  - [ ] It cannot be incorrectly interacted with in unintended ways (redstone, Observers, Hoppers)
- [ ] A custom block can exist without wrongfully overwriting vanilla block behaviours
  - [ ] It can be mixined without applying the new behaviors to the vanilla block (only to the blocks owned by ServerUI)
  - [ ] It can have a custom appearance to the client without overwriting the vanilla block appearance
- [ ] Custom block behaviour can exist
  - [ ] A custom block can have custom behaviour without exposing the block entity to the client
  - [ ] A custom block can have custom behaviour without wrongfully overwriting vanilla block behaviours
  - [ ] A custom block entityclass can be saved and loaded to Chunk NBT
- [ ] A custom UI can be shown to the client
  - [ ] An existing UI can have a cosmetic change applied as a layer, without overwriting the original UI
- [ ] A custom UI can be created by putting custom characters in the UI's title
  - [ ] A custom font can be generated programatically on startup
  - [ ] A font of any arbitrary size with arbitrary characteristics can be generated and loaded
  - [ ] Custom characters in the UIs title can be kept at a consistent position on the UI on different resolutions and aspect ratios
- [ ] A custom sound can be played
  - [ ] Any custom sound can be added using a resource pack without overwriting any original vanilla sound

### Custom Blocks

Visually displaying custom blocks is quite simple when you have access to Display Entities, so we will return to this later, but this block will not have collision, right click detection, it cannot be placed or broken or interacted with. We need to somehow make the player able to interact with the block, and to do that, we need to make the block "clickable" and minable and collidable. This is where the server-client distinction becomes a problem. Block collision can be achieved by hiding a dummy block under the Display Entity. The right-click detection *can* easily be done using Display Entities, but the mining detection is a bit more tricky, so we'll need a solution that handles both. We need a dummy block that:

- Is either minable with a pickaxe or axe (preferably one of each)
- Has a clientside right-click action (otherwise, the client would perform their default right-click action, which usually places a block or uses an item)
- Does not open a UI when clicked, OR
- Has the potential to cancel the right-click action serverside (otherwise, a UI would open, or another action would take place that is undesirable)

Luckily, we have precisely 2 options that fulfill these criteria:

1. Shulker Boxes
2. Note Blocks

Both blocks are minable (Shulker Boxes with a pickaxe, Note Blocks with an axe) and have clientside right-click actions (opening a UI and playing a note, respectively). While these default actions are undesirable, both blocks only perform their action when there is no block above them. This allows us to cancel the right-click action arbitrarily serverside, preventing the UI from opening or the note from playing. (Of course we do not need to simulate a block being above our Interaction Blocks, but because of this behaviour we know for sure that the client waits for permission from the server before performing the undesired action.) We can use this to our advantage to make the block "clickable" and minable. With another mixin to the block dropping code, we can make the block drop any arbitrary item instead of itself, removing the risk of the block being obtained by the player. We can also use mixins to cancel all other special interactions with the block, such as a Shulker Box being destroyed by a Piston or placed by a Dispenser, and a Note Block interacting with redstone. Note that we can keep track of all our Interaction Blocks, and thus only perform our custom logic when the block is actually ours, so we don't need to worry about accidentally interfering with vanilla Shulker Boxes or Note Blocks. We can also use mixins to directly pass along all native block method calls to our new custom blocks, such as redstone signals, inventory interactions and block updates.

To make the Interaction Block actually look distinct from Shulker Boxes and Note Blocks, we can use a Display Entity. A Display Entity has several powerful properties for our purposes:

- It can display any resource, not just vanilla ones, making it perfect for custom blocks
- It can be made "transparent" to player clicks. This means it won't be in the way when a player tries to right or left click our Interaction Blocks.

Whenever our Interaction Blocks are created, we can immediately create a Display Entity to accompany it. Whenever the block is broken, we can immediately call for its despawning. This way we can have a custom block with a custom appearance.

#### TODO: More on custom processes

### Custom Items

Custom items are easier to implement than custom blocks, as Minecraft already has had very extensive suite of tools to customize items and their attributes for a while, and it has been expanded even more recently. We now have the ability to give any item a custom texture, so this will never be a problem for us. Most attributes of an item can also be altered as easily, but it should be noted that it is best to keep these 2 attributes the same as the original item, as Mojang may have not properly removed legacy code that is hardcoded to check for specific items. They are as follows:

1. Stack size: While stack size can be set to 1-99, the original item's stack size will sometimes still be used in certain scenarios. For example, bundles have hardcoded stack size checks, so even if a potion's stack size is set to 64, a single potion will still fill up a bundle (though only visually, causing a desync).
2. Items used in recipes or processes: Items will sometimes still be used in those processes (like fuel, furnace inputs, and potions), or will or won't be accepted into certain containers, if not properly accounted for in the code. For instance, potions may still be allowed into brewing stands even if their components have changed.

For non-stackable usable items, we need an item that has a right-click action that can be performed at any moment without consequences, does not have any consequences of holding it (excludes certain food items and fishing rod types), and cannot be used in any recipes (excludes items like potions). The perfect candidate for this is the Knowledge Book. It has no usage restriction, and is completely unused, allowing us to give it any custom behaviour we want. We don't even need to use it in the roundabout way datapacks use it (recipe unlock > advancement > mcfunction), as we can directly hook into the items usage code.

For stackable usable items, we can use an item like Firework Rockets. These also have no placing restriction, no uses in recipes or processes, can stack to 64, and we can disable the spawning of the actual fireworks and the effects and sounds that go with it for our special item.

There are no other items necessary to implement custom items, as all other item properties are already extremely customizable and do not cause any issues like the properties mentioned above.