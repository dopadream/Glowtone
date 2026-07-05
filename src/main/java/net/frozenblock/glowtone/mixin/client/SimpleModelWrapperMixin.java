package net.frozenblock.glowtone.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.glowtone.GlowtoneConstants;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(SimpleModelWrapper.class)
public class SimpleModelWrapperMixin {

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
		if (useAmbientOcclusion && GlowtoneConstants.GLOWTONE_SHADING) {
			List<BakedQuad> bakedQuads = quads.getAll();
			if (!bakedQuads.isEmpty()) {
				boolean allFullbright = true;
				for (BakedQuad quad : bakedQuads) {
					if (quad.materialInfo().lightEmission() != 15) {
						allFullbright = false;
						break;
					}
				}
				if (allFullbright)
					useAmbientOcclusion = false;
			}
		}

		return original.call(quads, useAmbientOcclusion, particleMaterial);
	}
}
