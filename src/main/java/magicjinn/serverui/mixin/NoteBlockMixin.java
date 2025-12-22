package magicjinn.serverui.mixin;

import magicjinn.serverui.ServerUI;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Prevents NoteBlock sounds from playing on clients by preventing the block
 * event
 * from being sent. When blockEvent() is not called, clients never receive the
 * event,
 * so triggerEvent() is never called on the client, and no sound plays.
 */
@Mixin(NoteBlock.class)
public class NoteBlockMixin {

    /**
     * Intercepts the playNote method on the server to prevent block events from
     * being sent to clients. This prevents the sound from playing on the client.
     * 
     * Since this mod is server-only, this mixin only runs on the server.
     * By canceling playNote(), we prevent blockEvent() from being called,
     * which means no block event packet is sent to clients. Without the packet,
     * clients never call triggerEvent(), so no sound plays.
     */
    @Inject(at = @At("HEAD"), method = "playNote(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V", cancellable = true)
    private void preventNoteBlockSound(Entity entity, BlockState blockState, Level level, BlockPos blockPos,
            CallbackInfo ci) {
        // This mod is server-only, so this mixin only runs on the server
        // Prevent the block event from being sent to clients
        // This stops triggerEvent() from being called on clients, preventing the sound
        ServerUI.LOGGER.info("Prevented NoteBlock sound at {} (block event not sent to clients)", blockPos);
        ci.cancel();
    }
}
