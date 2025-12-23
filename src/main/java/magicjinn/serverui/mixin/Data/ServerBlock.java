package magicjinn.serverui.mixin.Data;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import net.minecraft.world.level.block.entity.BlockEntity;

public class ServerBlock {

    public enum SBlockType {
        STONE_METAL, // Shulker Box
        WOOD, // Note Block
        CUSTOM // YOU are responsible for handling this block type
    }

    private SBlockType type;
    private Class<? extends BlockEntity> blockEntityClass;
    private String id; // eg "some_block"
    private String namespace; // eg "somemod"
    private Map<String, String> localizedNames; // Redo. Provide/fetch language file on startup independent from blocks.

    public ServerBlock(SBlockType type, Class<? extends BlockEntity> blockEntityClass, String id, String namespace,
            Map<String, String> localizedNames) {
        this.type = type;
        this.blockEntityClass = blockEntityClass;
        this.id = id;
        this.namespace = namespace;
        this.localizedNames = localizedNames;
    }

    /**
     * Gets the type of the block.
     * 
     * @return The type of the block.
     */
    public SBlockType getType() {
        return type;
    }

    /**
     * Gets the class of the block entity.
     * 
     * @return The class of the block entity.
     */
    public Class<? extends BlockEntity> getBlockEntityClass() {
        return blockEntityClass;
    }

    /**
     * Provides a new instance of the block entity class.
     * This is used to create a new block entity instance for the block.
     * 
     * @return A new instance of the block entity class.
     */
    public BlockEntity provideBlockEntity() {
        try {
            return getBlockEntityClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create block entity", e);
        }
    }

    public Map<String, String> getLocalizedNames() {
        return localizedNames;
    }

    public String getId() {
        return id;
    }

    public String getFullId() {
        return getNamespace() + ":" + getId();
    }

    public String getNamespace() {
        return namespace;
    }
}
