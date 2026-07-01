package net.frozenblock.glowtone.particle.impl;

import net.minecraft.client.particle.SingleQuadParticle;

public interface GlowtoneEmissiveParticle {
	boolean glowtone$hasEmissiveOverlay();

	SingleQuadParticle.Layer glowtone$emissiveLayer();

	float glowtone$emissiveU0();

	float glowtone$emissiveU1();

	float glowtone$emissiveV0();

	float glowtone$emissiveV1();

	float glowtone$emissiveRCol();

	float glowtone$emissiveGCol();

	float glowtone$emissiveBCol();

	int glowtone$emissiveLightEmission();
}
