package magicjinn.serverui;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerUI implements ModInitializer {
	public static final String MOD_ID = "serverui";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		// Load serverui entrypoints from all mods
		loadServerUIEntrypoints();
	}

	private void loadServerUIEntrypoints() {
		FabricLoader loader = FabricLoader.getInstance();

		// Get all entrypoint containers for the "serverui" entrypoint
		// Using Object.class as the type since we don't know what interface these
		// classes implement
		// This will return an empty list if no mods have the "serverui" entrypoint
		var entrypointContainers = loader.getEntrypointContainers("serverui", Object.class);

		for (EntrypointContainer<Object> container : entrypointContainers) {
			String modId = container.getProvider().getMetadata().getId();

			// Skip our own mod to avoid circular references
			if (modId.equals(MOD_ID)) {
				continue;
			}

			try {
				Object entrypoint = container.getEntrypoint();
				LOGGER.info("Successfully loaded serverui entrypoint from mod {}: {}", modId,
						entrypoint.getClass().getName());

				// TODO: Store or register the entrypoint instance as needed
				// For now, we just load it

			} catch (Exception e) {
				LOGGER.error("Failed to load serverui entrypoint from mod {}: {}", modId, e.getMessage(), e);
			}
		}
	}
}