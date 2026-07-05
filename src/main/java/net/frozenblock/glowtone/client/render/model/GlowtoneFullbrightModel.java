package net.frozenblock.glowtone.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface GlowtoneFullbrightModel {
	boolean glowtone$isFullbright();
}
