package net.frozenblock.glowtone.mixin.client;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(EnvType.CLIENT)
@Mixin(TextureAtlas.class)
public interface TextureAtlasAccessor {
	@Accessor("animatedTexturesStates")
	List<SpriteContents.AnimationState> glowtone$getAnimatedTexturesStates();

	@Invoker("uploadAnimationFrames")
	void glowtone$invokeUploadAnimationFrames();
}
