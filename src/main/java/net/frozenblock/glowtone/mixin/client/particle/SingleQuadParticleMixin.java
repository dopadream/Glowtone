package net.frozenblock.glowtone.mixin.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.glowtone.particle.impl.GlowtoneEmissiveParticle;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.lighting.LightEngine;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(SingleQuadParticle.class)
public abstract class SingleQuadParticleMixin {

	@Shadow
	protected float alpha;

	@Shadow
	public abstract float getQuadSize(float partialTickTime);

	@Inject(
		method = "extractRotatedQuad(Lnet/minecraft/client/renderer/state/level/QuadParticleRenderState;Lorg/joml/Quaternionf;FFFF)V",
		at = @At("TAIL")
	)
	private void glowtone$extractEmissiveOverlay(
		QuadParticleRenderState particleTypeRenderState, Quaternionf rotation, float x, float y, float z, float partialTickTime, CallbackInfo ci
	) {
		if (!(this instanceof GlowtoneEmissiveParticle emissiveParticle) || !emissiveParticle.glowtone$hasEmissiveOverlay()) return;

		final int color = ARGB.colorFromFloat(
			this.alpha, emissiveParticle.glowtone$emissiveRCol(), emissiveParticle.glowtone$emissiveGCol(), emissiveParticle.glowtone$emissiveBCol()
		);

		final int baseLightCoords = ((ParticleInvokerMixin) this).glowtone$getLightCoords(partialTickTime);
		final int emissiveLightEmission = emissiveParticle.glowtone$emissiveLightEmission();
		final int emissiveLightCoords = emissiveLightEmission == 0
			? baseLightCoords
			: LightCoordsUtil.addSmoothBlockEmission(baseLightCoords, (float) emissiveLightEmission / LightEngine.MAX_LEVEL);

		particleTypeRenderState.add(
			emissiveParticle.glowtone$emissiveLayer(),
			x,
			y,
			z,
			rotation.x,
			rotation.y,
			rotation.z,
			rotation.w,
			this.getQuadSize(partialTickTime),
			emissiveParticle.glowtone$emissiveU0(),
			emissiveParticle.glowtone$emissiveU1(),
			emissiveParticle.glowtone$emissiveV0(),
			emissiveParticle.glowtone$emissiveV1(),
			color,
			emissiveLightCoords
		);
	}
}
