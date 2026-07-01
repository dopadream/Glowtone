package net.frozenblock.glowtone.mixin.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(SingleQuadParticle.class)
public interface SingleQuadParticleAccessor {
	@Accessor("sprite")
	TextureAtlasSprite glowtone$getSprite();

	@Accessor("rCol")
	float glowtone$getRCol();

	@Accessor("rCol")
	void glowtone$setRCol(float value);

	@Accessor("gCol")
	float glowtone$getGCol();

	@Accessor("gCol")
	void glowtone$setGCol(float value);

	@Accessor("bCol")
	float glowtone$getBCol();

	@Accessor("bCol")
	void glowtone$setBCol(float value);
}
