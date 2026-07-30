/*
 * Copyright 2025-2026 FrozenBlock
 * This file is part of Glowtone.
 *
 * This program is free software; you can modify it under
 * the terms of version 1 of the FrozenBlock Modding Oasis License
 * as published by FrozenBlock Modding Oasis.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * FrozenBlock Modding Oasis License for more details.
 *
 * You should have received a copy of the FrozenBlock Modding Oasis License
 * along with this program; if not, see <https://github.com/FrozenBlock/Licenses>.
 */

package net.frozenblock.glowtone.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.glowtone.GlowtoneConstants;
import net.frozenblock.glowtone.client.render.item.GlowtoneItemRenderTypes;
import net.frozenblock.glowtone.resources.metadata.EmissiveMetadataSection;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.cuboid.FaceBakery;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(FaceBakery.class)
public class FaceBakeryMixin {

	@Inject(
		method = "bakeQuad(Lnet/minecraft/client/resources/model/ModelBaker$Interner;Lorg/joml/Vector3fc;Lorg/joml/Vector3fc;Lnet/minecraft/client/resources/model/cuboid/CuboidFace$UVs;Lcom/mojang/math/Quadrant;Lnet/minecraft/client/resources/model/geometry/BakedQuad$MaterialInfo;Lnet/minecraft/core/Direction;Lnet/minecraft/client/renderer/block/dispatch/ModelState;Lnet/minecraft/client/resources/model/cuboid/CuboidRotation;)Lnet/minecraft/client/resources/model/geometry/BakedQuad;",
		at = @At("HEAD")
	)
	private static void glowtone$bakeWithEmission(
		CallbackInfoReturnable<BakedQuad> info,
		@Local(argsOnly = true) LocalRef<BakedQuad.MaterialInfo> materialInfoRef
	) {
		final BakedQuad.MaterialInfo materialInfo = materialInfoRef.get();
		boolean shade = materialInfo.shadeDirectionOverride() == null;
		int lightEmission = materialInfo.lightEmission();
		boolean isModified = false;

		if (GlowtoneConstants.GLOWTONE_REPLACEMENTS) {
			final SpriteContents contents = materialInfo.sprite().contents();

			final Optional<EmissiveMetadataSection> optionalEmissiveMetadata = contents.getAdditionalMetadata(EmissiveMetadataSection.TYPE);
			if (optionalEmissiveMetadata.isPresent()) {
				final EmissiveMetadataSection emissiveMetadata = optionalEmissiveMetadata.get();
				shade = emissiveMetadata.shade().orElse(shade);
				lightEmission = emissiveMetadata.lightEmission();
				isModified = true;
			} else if (contents.name().getPath().endsWith("_glowtone_emissive")) {
				lightEmission = 15;
				isModified = true;
			}
		}

		if (GlowtoneConstants.GLOWTONE_SHADING) {
			final boolean wasShaded = shade;
			shade = shade && lightEmission != 15;
			isModified = isModified || wasShaded != shade;
		}

		RenderType itemRenderType = materialInfo.itemRenderType();
		RenderType itemGlintRenderType = materialInfo.itemGlintRenderType();
		RenderType itemGlintSpecialRenderType = materialInfo.itemGlintSpecialRenderType();

		if (GlowtoneConstants.GLOWTONE_SHADING && lightEmission == 15) {
			final RenderType unshadedItemRenderType = glowtone$unshadedItemRenderType(itemRenderType);
			if (unshadedItemRenderType != null && unshadedItemRenderType != itemRenderType) {
				itemRenderType = unshadedItemRenderType;
				isModified = true;
			}

			final RenderType unshadedItemGlintRenderType = glowtone$unshadedItemGlintRenderType(itemGlintRenderType);
			if (unshadedItemGlintRenderType != null && unshadedItemGlintRenderType != itemGlintRenderType) {
				itemGlintRenderType = unshadedItemGlintRenderType;
				isModified = true;
			}

			final RenderType unshadedItemGlintSpecialRenderType = glowtone$unshadedItemGlintSpecialRenderType(itemGlintSpecialRenderType);
			if (unshadedItemGlintSpecialRenderType != null && unshadedItemGlintSpecialRenderType != itemGlintSpecialRenderType) {
				itemGlintSpecialRenderType = unshadedItemGlintSpecialRenderType;
				isModified = true;
			}
		}

		if (!isModified) return;

		materialInfoRef.set(
			new BakedQuad.MaterialInfo(
				materialInfo.sprite(),
				materialInfo.layer(),
				itemRenderType,
				itemGlintRenderType,
				itemGlintSpecialRenderType,
				materialInfo.tintIndex(),
				shade ? null : Direction.UP,
				lightEmission
			)
		);
	}

	@Unique
	private static RenderType glowtone$unshadedItemRenderType(RenderType original) {
		if (original == Sheets.cutoutBlockItemSheet()) return GlowtoneItemRenderTypes.itemCutoutUnshaded(TextureAtlas.LOCATION_BLOCKS);
		if (original == Sheets.translucentBlockItemSheet()) return GlowtoneItemRenderTypes.itemTranslucentUnshaded(TextureAtlas.LOCATION_BLOCKS);
		if (original == Sheets.cutoutItemSheet()) return GlowtoneItemRenderTypes.itemCutoutUnshaded(TextureAtlas.LOCATION_ITEMS);
		if (original == Sheets.translucentItemSheet()) return GlowtoneItemRenderTypes.itemTranslucentUnshaded(TextureAtlas.LOCATION_ITEMS);
		return null;
	}

	@Unique
	private static RenderType glowtone$unshadedItemGlintRenderType(RenderType original) {
		if (original == Sheets.cutoutBlockItemGlintSheet()) return GlowtoneItemRenderTypes.itemCutoutGlintUnshaded(TextureAtlas.LOCATION_BLOCKS);
		if (original == Sheets.translucentBlockItemGlintSheet()) return GlowtoneItemRenderTypes.itemTranslucentGlintUnshaded(TextureAtlas.LOCATION_BLOCKS);
		if (original == Sheets.cutoutItemGlintSheet()) return GlowtoneItemRenderTypes.itemCutoutGlintUnshaded(TextureAtlas.LOCATION_ITEMS);
		if (original == Sheets.translucentItemGlintSheet()) return GlowtoneItemRenderTypes.itemTranslucentGlintUnshaded(TextureAtlas.LOCATION_ITEMS);
		return null;
	}

	@Unique
	private static RenderType glowtone$unshadedItemGlintSpecialRenderType(RenderType original) {
		if (original == Sheets.cutoutBlockItemGlintSpecialSheet()) return GlowtoneItemRenderTypes.itemCutoutGlintSpecialUnshaded(TextureAtlas.LOCATION_BLOCKS);
		if (original == Sheets.translucentBlockItemGlintSpecialSheet()) return GlowtoneItemRenderTypes.itemTranslucentGlintSpecialUnshaded(TextureAtlas.LOCATION_BLOCKS);
		if (original == Sheets.cutoutItemGlintSpecialSheet()) return GlowtoneItemRenderTypes.itemCutoutGlintSpecialUnshaded(TextureAtlas.LOCATION_ITEMS);
		if (original == Sheets.translucentItemGlintSpecialSheet()) return GlowtoneItemRenderTypes.itemTranslucentGlintSpecialUnshaded(TextureAtlas.LOCATION_ITEMS);
		return null;
	}
}
