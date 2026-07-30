package net.frozenblock.glowtone.client.render.item;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.glowtone.GlowtoneConstants;
import net.frozenblock.glowtone.mixin.client.item.RenderTypeInvokerMixin;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.oit.OitPipelineSet;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

// i cheated.. this is technically shadered shaders

@Environment(EnvType.CLIENT)
public final class GlowtoneItemRenderTypes {
	private static final RenderPipeline.Snippet ITEM_UNSHADED_SNIPPET = RenderPipeline.builder()
		.withBindGroupLayout(BindGroupLayouts.GLOBALS)
		.withBindGroupLayout(BindGroupLayouts.PROJECTION)
		.withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS)
		.withBindGroupLayout(BindGroupLayouts.FOG)
		.withBindGroupLayout(BindGroupLayouts.LIGHTING)
		.withBindGroupLayout(BindGroupLayouts.SAMPLER0_SAMPLER1_SAMPLER2)
		.withVertexShader("core/item")
		.withFragmentShader("core/item")
		.withShaderDefine("NO_CARDINAL_LIGHTING")
		.withShaderDefine("ALPHA_CUTOUT", 0.1F)
		.withVertexBinding(0, DefaultVertexFormat.ENTITY)
		.withPrimitiveTopology(PrimitiveTopology.QUADS)
		.withDepthStencilState(DepthStencilState.DEFAULT)
		.buildSnippet();

	public static final RenderPipeline ITEM_CUTOUT_UNSHADED = RenderPipeline.builder(ITEM_UNSHADED_SNIPPET)
		.withLocation(GlowtoneConstants.id("pipeline/item_cutout_unshaded"))
		.withColorTargetState(ColorTargetState.DEFAULT)
		.build();

	public static final RenderPipeline ITEM_CUTOUT_GLINT_UNSHADED = RenderPipeline.builder(ITEM_UNSHADED_SNIPPET)
		.withLocation(GlowtoneConstants.id("pipeline/item_cutout_glint_unshaded"))
		.withShaderDefine("GLINT")
		.withBindGroupLayout(BindGroupLayouts.GLINT_SAMPLER)
		.withColorTargetState(ColorTargetState.DEFAULT)
		.build();

	public static final RenderPipeline ITEM_CUTOUT_GLINT_SPECIAL_UNSHADED = RenderPipeline.builder(ITEM_UNSHADED_SNIPPET)
		.withLocation(GlowtoneConstants.id("pipeline/item_cutout_glint_special_unshaded"))
		.withShaderDefine("GLINT")
		.withShaderDefine("GLINT_SPECIAL")
		.withBindGroupLayout(BindGroupLayouts.GLINT_SAMPLER)
		.withVertexBinding(0, DefaultVertexFormat.ENTITY_GLINT_SPECIAL)
		.withColorTargetState(ColorTargetState.DEFAULT)
		.build();

	public static final RenderPipeline ITEM_TRANSLUCENT_UNSHADED = RenderPipeline.builder(ITEM_UNSHADED_SNIPPET)
		.withLocation(GlowtoneConstants.id("pipeline/item_translucent_unshaded"))
		.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
		.build();

	public static final RenderPipeline ITEM_TRANSLUCENT_GLINT_UNSHADED = RenderPipeline.builder(ITEM_UNSHADED_SNIPPET)
		.withLocation(GlowtoneConstants.id("pipeline/item_translucent_glint_unshaded"))
		.withShaderDefine("GLINT")
		.withBindGroupLayout(BindGroupLayouts.GLINT_SAMPLER)
		.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
		.build();

	public static final RenderPipeline ITEM_TRANSLUCENT_GLINT_SPECIAL_UNSHADED = RenderPipeline.builder(ITEM_UNSHADED_SNIPPET)
		.withLocation(GlowtoneConstants.id("pipeline/item_translucent_glint_special_unshaded"))
		.withShaderDefine("GLINT")
		.withShaderDefine("GLINT_SPECIAL")
		.withBindGroupLayout(BindGroupLayouts.GLINT_SAMPLER)
		.withVertexBinding(0, DefaultVertexFormat.ENTITY_GLINT_SPECIAL)
		.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
		.build();

	private static final OitPipelineSet ITEM_TRANSLUCENT_UNSHADED_OIT = OitPipelineSet.builder(
			"glowtone_item_translucent_unshaded", RenderPipeline.builder(RenderPipelines.OIT_ITEM_SNIPPET).withShaderDefine("NO_CARDINAL_LIGHTING")
		)
		.withAccumulateModifier(accumulate -> accumulate.withBindGroupLayout(BindGroupLayouts.SAMPLER1).withBindGroupLayout(BindGroupLayouts.SAMPLER2))
		.build();

	private static final Function<Identifier, RenderType> ITEM_CUTOUT_UNSHADED_FN = Util.memoize(
		texture -> RenderTypeInvokerMixin.glowtone$create(
			"glowtone_item_cutout_unshaded",
			RenderSetup.builder(ITEM_CUTOUT_UNSHADED)
				.withTexture("Sampler0", texture)
				.useLightmap()
				.useOverlay()
				.affectsCrumbling()
				.setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
				.createRenderSetup()
		)
	);

	private static final Function<Identifier, RenderType> ITEM_CUTOUT_GLINT_UNSHADED_FN = Util.memoize(
		texture -> RenderTypeInvokerMixin.glowtone$create(
			"glowtone_item_cutout_glint_unshaded",
			RenderSetup.builder(ITEM_CUTOUT_GLINT_UNSHADED)
				.withTexture("Sampler0", texture)
				.withTexture("GlintSampler", ItemFeatureRenderer.ENCHANTED_GLINT_ITEM)
				.setTextureTransform(TextureTransform.GLINT_TEXTURING)
				.useLightmap()
				.useOverlay()
				.affectsCrumbling()
				.setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
				.createRenderSetup()
		)
	);

	private static final Function<Identifier, RenderType> ITEM_CUTOUT_GLINT_SPECIAL_UNSHADED_FN = Util.memoize(
		texture -> RenderTypeInvokerMixin.glowtone$create(
			"glowtone_item_cutout_glint_special_unshaded",
			RenderSetup.builder(ITEM_CUTOUT_GLINT_SPECIAL_UNSHADED)
				.withTexture("Sampler0", texture)
				.withTexture("GlintSampler", ItemFeatureRenderer.ENCHANTED_GLINT_ITEM)
				.setTextureTransform(TextureTransform.GLINT_TEXTURING)
				.useLightmap()
				.useOverlay()
				.affectsCrumbling()
				.setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
				.createRenderSetup()
		)
	);

	private static final Function<Identifier, RenderType> ITEM_TRANSLUCENT_UNSHADED_FN = Util.memoize(
		texture -> RenderTypeInvokerMixin.glowtone$create(
			"glowtone_item_translucent_unshaded",
			RenderSetup.builder(ITEM_TRANSLUCENT_UNSHADED)
				.withTexture("Sampler0", texture)
				.setOitPipelines(ITEM_TRANSLUCENT_UNSHADED_OIT)
				.useLightmap()
				.useOverlay()
				.affectsCrumbling()
				.sortOnUpload()
				.setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
				.createRenderSetup()
		)
	);

	private static final Function<Identifier, RenderType> ITEM_TRANSLUCENT_GLINT_UNSHADED_FN = Util.memoize(
		texture -> RenderTypeInvokerMixin.glowtone$create(
			"glowtone_item_translucent_glint_unshaded",
			RenderSetup.builder(ITEM_TRANSLUCENT_GLINT_UNSHADED)
				.withTexture("Sampler0", texture)
				.withTexture("GlintSampler", ItemFeatureRenderer.ENCHANTED_GLINT_ITEM)
				.setTextureTransform(TextureTransform.GLINT_TEXTURING)
				.useLightmap()
				.useOverlay()
				.affectsCrumbling()
				.sortOnUpload()
				.setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
				.createRenderSetup()
		)
	);

	private static final Function<Identifier, RenderType> ITEM_TRANSLUCENT_GLINT_SPECIAL_UNSHADED_FN = Util.memoize(
		texture -> RenderTypeInvokerMixin.glowtone$create(
			"glowtone_item_translucent_glint_special_unshaded",
			RenderSetup.builder(ITEM_TRANSLUCENT_GLINT_SPECIAL_UNSHADED)
				.withTexture("Sampler0", texture)
				.withTexture("GlintSampler", ItemFeatureRenderer.ENCHANTED_GLINT_ITEM)
				.setTextureTransform(TextureTransform.GLINT_TEXTURING)
				.useLightmap()
				.useOverlay()
				.affectsCrumbling()
				.sortOnUpload()
				.setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
				.createRenderSetup()
		)
	);

	public static RenderType itemCutoutUnshaded(Identifier texture) {
		return ITEM_CUTOUT_UNSHADED_FN.apply(texture);
	}

	public static RenderType itemCutoutGlintUnshaded(Identifier texture) {
		return ITEM_CUTOUT_GLINT_UNSHADED_FN.apply(texture);
	}

	public static RenderType itemCutoutGlintSpecialUnshaded(Identifier texture) {
		return ITEM_CUTOUT_GLINT_SPECIAL_UNSHADED_FN.apply(texture);
	}

	public static RenderType itemTranslucentUnshaded(Identifier texture) {
		return ITEM_TRANSLUCENT_UNSHADED_FN.apply(texture);
	}

	public static RenderType itemTranslucentGlintUnshaded(Identifier texture) {
		return ITEM_TRANSLUCENT_GLINT_UNSHADED_FN.apply(texture);
	}

	public static RenderType itemTranslucentGlintSpecialUnshaded(Identifier texture) {
		return ITEM_TRANSLUCENT_GLINT_SPECIAL_UNSHADED_FN.apply(texture);
	}

	private GlowtoneItemRenderTypes() {}
}
