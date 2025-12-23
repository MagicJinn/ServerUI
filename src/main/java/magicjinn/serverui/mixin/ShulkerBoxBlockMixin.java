package magicjinn.serverui.mixin;

import magicjinn.serverui.ServerUI;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Prevents shulker boxes from opening their UI while still allowing the hand
 * animation to play. The method returns SUCCESS (for animation) but the
 * openMenu
 * call is redirected to do nothing.
 * 
 * Since this mod is server-only, this mixin only runs on the server.
 */
@Mixin(ShulkerBoxBlock.class)
public class ShulkerBoxBlockMixin {

    /**
     * Redirects the player.openMenu() call to prevent the shulker box UI from
     * opening.
     * This allows the method to return SUCCESS (keeping the hand animation) while
     * preventing the menu from opening.
     */
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;openMenu(Lnet/minecraft/world/MenuProvider;)Ljava/util/OptionalInt;"), method = "useWithoutItem(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;")
    private java.util.OptionalInt preventOpenMenu(Player player, net.minecraft.world.MenuProvider menuProvider) {
        // Log the prevention
        ServerUI.LOGGER.info("Prevented shulker box UI from opening (player: {})", player.getName().getString());
        // Return empty OptionalInt to prevent the menu from opening
        return java.util.OptionalInt.empty();
    }

    private boolean isOwnedByServerUI() {
        // TEMPORARY: Always return true
        // In the future, this will check whether this block is owned by ServerUI, using
        // a global registry of ServerUI blocks
        return true;
    }
}
