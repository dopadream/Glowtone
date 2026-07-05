package net.frozenblock.glowtone.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.glowtone.GlowtoneConstants;
import net.frozenblock.glowtone.client.render.model.GlowtoneFullbrightModel;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(SimpleModelWrapper.class)
public class SimpleModelWrapperMixin implements GlowtoneFullbrightModel {

	@Unique
	private boolean glowtone$fullbright;

	@Override
	public boolean glowtone$isFullbright() {
		return this.glowtone$fullbright;
	}

	@WrapOperation(
		method = "bake",
		at = @At(
			value = "NEW",
			target = "Lnet/minecraft/client/resources/model/SimpleModelWrapper;"
		)
	)
	private static SimpleModelWrapper glowtone$skipAmbientOcclusionForFullbright(
		QuadCollection quads, boolean useAmbientOcclusion, Material.Baked particleMaterial, Operation<SimpleModelWrapper> original
	) {
		boolean glowtoneShading = GlowtoneConstants.GLOWTONE_SHADING;
		if (glowtoneShading) {
			List<BakedQuad> bakedQuads = quads.getAll();
			glowtoneShading = !bakedQuads.isEmpty();
			for (BakedQuad quad : bakedQuads) {
				if (quad.materialInfo().lightEmission() != 15) {
					glowtoneShading = false;
					break;
				}
			}
		}

		if (glowtoneShading)
			useAmbientOcclusion = false;

		SimpleModelWrapper result = original.call(quads, useAmbientOcclusion, particleMaterial);
		((SimpleModelWrapperMixin) (Object) result).glowtone$fullbright = glowtoneShading;
		return result;
	}
}
