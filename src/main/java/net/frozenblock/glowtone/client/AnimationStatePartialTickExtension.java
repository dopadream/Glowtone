package net.frozenblock.glowtone.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface AnimationStatePartialTickExtension {
	void glowtone$setPartialTick(float partialTick);
}
