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

## How will it be done?

ServerUI completely takes over block and item registration, block processes, UI registration, etc. The mod will use a variety of tricks to properly display these new blocks, items and UIs to the client using server resource packs, datapacks, font trickery, serverside mixins, etc.

## The Work that needs to be done

### Custom Blocks

Displaying custom blocks is relatively easy, but that is only the first step. A block by itself will be relatively useless. We need to somehow make the player able to interact with the block, and to do that, we need to make the block "clickable" and minable. This is where the server-client distinction becomes a problem. The right-click detection could easily be done using display entities, but the mining detection is a bit more tricky, so we'll need a solution that handles both. We need a dummy block that:

- Is either minable with a pickaxe or axe (preferably one of each)
- Has a clientside right-click action (otherwise, the client would perform their default right-click action, which usually places a block or uses an item)
- Does not open a UI when clicked, OR
- Has the potential to cancel the right-click action serverside (otherwise, a UI would open, or another action would take place that is undesirable)

Luckily, we have precisely 2 options that fulfill these criteria:

1. Shulker boxes
2. Note blocks

Both blocks are minable (shulker boxes with a pickaxe, note blocks with an axe) and have clientside right-click actions (opening a UI and playing a note, respectively). While these default actions are undesirable, both blocks only perform their action when there is no block above them. This allows us to cancel the right-click action arbitrarily serverside, preventing the UI from opening or the note from playing. We can use this to our advantage to make the block "clickable" and minable. With another mixin to the block dropping code, we can make the block drop any arbitrary item instead of itself, removing the risk of the block being obtained by the player. We can also use mixins to cancel all other special interactions with the block, such as a shulker box being destroyed by a piston, and a note block interacting with redstone. Note that we can keep track of all our serverside blocks, and thus only perform our custom logic when the block is actually ours, so we don't need to worry about accidentally interfering with vanilla shulker boxes or note blocks.