package net.frozenblock.glowtone.mixin.client.particle;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.glowtone.GlowtoneConstants;
import net.frozenblock.glowtone.particle.impl.GlowtoneParticle;
import net.frozenblock.glowtone.resources.metadata.EmissiveMetadataSection;
import net.frozenblock.glowtone.particle.impl.GlowtoneEmissiveParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(TerrainParticle.class)
public class TerrainParticleMixin implements GlowtoneEmissiveParticle, GlowtoneParticle {

	@Unique
	private static final float glowtone$BASE_DIM = 0.6F;

	@Shadow
	@Final
	private float uo;

	@Shadow
	@Final
	private float vo;

	@Unique
	private TextureAtlasSprite glowtone$emissiveSprite;

	@Unique
	private SingleQuadParticle.Layer glowtone$emissiveLayer;

	@Unique
	private int glowtone$lightEmission;

	@Unique
	private float glowtone$emissiveRCol;

	@Unique
	private float glowtone$emissiveGCol;

	@Unique
	private float glowtone$emissiveBCol;

	@Unique
	private int glowtone$emissiveLightEmission;

	@Inject(
		method = "<init>(Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDLnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)V",
		at = @At("TAIL")
	)
	private void glowtone$findEmissiveSprite(
		ClientLevel level, double x, double y, double z, double xa, double ya, double za, BlockState blockState, BlockPos pos, CallbackInfo ci
	) {
		if (!GlowtoneConstants.GLOWTONE_REPLACEMENTS) return;

		final SingleQuadParticleAccessor accessor = (SingleQuadParticleAccessor) this;
		final TextureAtlasSprite sprite = accessor.glowtone$getSprite();

		final float tintR = accessor.glowtone$getRCol() / glowtone$BASE_DIM;
		final float tintG = accessor.glowtone$getGCol() / glowtone$BASE_DIM;
		final float tintB = accessor.glowtone$getBCol() / glowtone$BASE_DIM;

		final int[] base = glowtone$computeShadeAndEmission(sprite);
		final boolean baseShade = base[0] != 0;

		this.glowtone$lightEmission = base[1];

		final Identifier location = sprite.contents().name();
		final Identifier emissiveLocation = location.withSuffix("_glowtone_emissive");
		final TextureAtlas atlas = (TextureAtlas) Minecraft.getInstance().getTextureManager().getTexture(sprite.atlasLocation());
		final TextureAtlasSprite candidate = atlas.getSprite(emissiveLocation);

		if (!candidate.contents().name().equals(MissingTextureAtlasSprite.getLocation())) {
			this.glowtone$emissiveSprite = candidate;
			this.glowtone$emissiveLayer = SingleQuadParticle.Layer.bySprite(candidate);

			final int[] overlay = glowtone$computeShadeAndEmission(candidate);
			final boolean overlayShade = overlay[0] != 0;
			this.glowtone$emissiveLightEmission = overlay[1];

			if (!overlayShade) {
				this.glowtone$emissiveRCol = tintR;
				this.glowtone$emissiveGCol = tintG;
				this.glowtone$emissiveBCol = tintB;
			} else {
				this.glowtone$emissiveRCol = tintR / glowtone$BASE_DIM;
				this.glowtone$emissiveGCol = tintG / glowtone$BASE_DIM;
				this.glowtone$emissiveBCol = tintB / glowtone$BASE_DIM;
			}
		}

		if (!baseShade) {
			accessor.glowtone$setRCol(tintR);
			accessor.glowtone$setGCol(tintG);
			accessor.glowtone$setBCol(tintB);
		}
	}

	@Unique
	private static int[] glowtone$computeShadeAndEmission(TextureAtlasSprite candidateSprite) {
		boolean shade = true;
		int lightEmission;

		final SpriteContents contents = candidateSprite.contents();
		final Optional<EmissiveMetadataSection> optionalEmissiveMetadata = contents.getAdditionalMetadata(EmissiveMetadataSection.TYPE);
		if (optionalEmissiveMetadata.isPresent()) {
			final EmissiveMetadataSection emissiveMetadata = optionalEmissiveMetadata.get();
			shade = emissiveMetadata.shade().orElse(shade);
			lightEmission = emissiveMetadata.lightEmission();
		} else if (contents.name().getPath().endsWith("_glowtone_emissive")) {
			lightEmission = 15;
		} else {
			lightEmission = 0;
		}

		if (GlowtoneConstants.GLOWTONE_SHADING) {
			shade = shade && lightEmission != 15;
		}

		return new int[]{shade ? 1 : 0, lightEmission};
	}

	@Override
	public boolean glowtone$hasEmissiveOverlay() {
		return this.glowtone$emissiveSprite != null;
	}

	@Unique
	@Override
	public void glowtone$setLightEmission(int lightEmission) {
		this.glowtone$lightEmission = lightEmission;
	}

	@Unique
	@Override
	public int glowtone$getLightEmission() {
		return this.glowtone$lightEmission;
	}

	@Override
	public SingleQuadParticle.Layer glowtone$emissiveLayer() {
		return this.glowtone$emissiveLayer;
	}

	@Override
	public float glowtone$emissiveU0() {
		return this.glowtone$emissiveSprite.getU((this.uo + 1.0F) / 4.0F);
	}

	@Override
	public float glowtone$emissiveU1() {
		return this.glowtone$emissiveSprite.getU(this.uo / 4.0F);
	}

	@Override
	public float glowtone$emissiveV0() {
		return this.glowtone$emissiveSprite.getV(this.vo / 4.0F);
	}

	@Override
	public float glowtone$emissiveV1() {
		return this.glowtone$emissiveSprite.getV((this.vo + 1.0F) / 4.0F);
	}

	@Override
	public float glowtone$emissiveRCol() {
		return this.glowtone$emissiveRCol;
	}

	@Override
	public float glowtone$emissiveGCol() {
		return this.glowtone$emissiveGCol;
	}

	@Override
	public float glowtone$emissiveBCol() {
		return this.glowtone$emissiveBCol;
	}

	@Override
	public int glowtone$emissiveLightEmission() {
		return this.glowtone$emissiveLightEmission;
	}
}
