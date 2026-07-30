package net.frozenblock.glowtone.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.glowtone.GlowtoneConstants;
import net.frozenblock.glowtone.client.AnimationStatePartialTickExtension;
import net.minecraft.client.renderer.texture.SpriteContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(SpriteContents.AnimationState.class)
public abstract class AnimationStateMixin implements AnimationStatePartialTickExtension {

	@Unique
	private float glowtone$partialTick = 0.0F;

	@Unique
	private boolean glowtone$interpolating = false;

	@Unique
	private int glowtone$frameTime = 1;

	@Override
	@Unique
	public void glowtone$setPartialTick(float partialTick) {
		this.glowtone$partialTick = partialTick;
	}

	@ModifyExpressionValue(
		method = "needsToDraw",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/renderer/texture/SpriteContents$AnimatedTexture;interpolateFrames:Z"
		)
	)
	private boolean glowtone$captureInterpolating(boolean interpolateFrames) {
		this.glowtone$interpolating = interpolateFrames;
		return interpolateFrames;
	}

	@ModifyExpressionValue(
		method = "drawToAtlas(Lcom/mojang/renderpearl/api/commands/RenderPass;Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;)V",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/renderer/texture/SpriteContents$FrameInfo;time:I"
		)
	)
	private int glowtone$captureFrameTime(int time) {
		this.glowtone$frameTime = time;
		return time;
	}

	@ModifyVariable(
		method = "drawToAtlas(Lcom/mojang/renderpearl/api/commands/RenderPass;Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;)V",
		at = @At("STORE"),
		ordinal = 0
	)
	private float glowtone$smoothFrameProgress(float frameProgress) {
		if (!GlowtoneConstants.GLOWTONE_REPLACEMENTS || !this.glowtone$interpolating || this.glowtone$frameTime <= 0) {
			return frameProgress;
		}

		return Math.min(1.0F, frameProgress + this.glowtone$partialTick / this.glowtone$frameTime);
	}
}
