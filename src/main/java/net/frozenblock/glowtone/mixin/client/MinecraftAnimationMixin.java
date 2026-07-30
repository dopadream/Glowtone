package net.frozenblock.glowtone.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.glowtone.GlowtoneConstants;
import net.frozenblock.glowtone.client.AnimationStatePartialTickExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public abstract class MinecraftAnimationMixin {

	@Inject(
		method = "renderFrame",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/GameRenderer;extract(Lnet/minecraft/client/DeltaTracker;Z)V"
		)
	)
	private void glowtone$smoothInterpolatedAnimations(boolean tick, CallbackInfo ci) {
		if (!GlowtoneConstants.GLOWTONE_REPLACEMENTS) return;

		Minecraft minecraft = (Minecraft) (Object) this;
		float partialTick = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
		glowtone$refreshAtlas(minecraft, TextureAtlas.LOCATION_BLOCKS, partialTick);
		glowtone$refreshAtlas(minecraft, TextureAtlas.LOCATION_ITEMS, partialTick);
	}

	@Unique
	private static void glowtone$refreshAtlas(Minecraft minecraft, Identifier location, float partialTick) {
		AbstractTexture texture = minecraft.getTextureManager().getTexture(location);
		if (!(texture instanceof TextureAtlas atlas)) return;

		TextureAtlasAccessor accessor = (TextureAtlasAccessor) atlas;
		for (SpriteContents.AnimationState state : accessor.glowtone$getAnimatedTexturesStates()) {
			((AnimationStatePartialTickExtension) state).glowtone$setPartialTick(partialTick);
		}

		accessor.glowtone$invokeUploadAnimationFrames();
	}
}
