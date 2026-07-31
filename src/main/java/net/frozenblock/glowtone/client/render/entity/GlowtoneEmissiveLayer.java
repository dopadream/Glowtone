package net.frozenblock.glowtone.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.glowtone.GlowtoneClient;
import net.frozenblock.glowtone.GlowtoneConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class GlowtoneEmissiveLayer<S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends RenderLayer<S, M> {
	private static final Identifier NO_EMISSIVE_TEXTURE = GlowtoneConstants.id("no_emissive_texture");
	private static final Map<Identifier, Identifier> RESOLVED_TEXTURES = new ConcurrentHashMap<>();

	private final LivingEntityRenderer<?, S, M> renderer;

	public GlowtoneEmissiveLayer(LivingEntityRenderer<?, S, M> renderer) {
		super(renderer);
		this.renderer = renderer;
	}

	public static void clearCache() {
		RESOLVED_TEXTURES.clear();
	}

	@Override
	public void submit(
		PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, S state, float yRot, float xRot
	) {
		if (!GlowtoneConstants.GLOWTONE_REPLACEMENTS) return;

		final Identifier emissiveTexture = resolveGlowtoneEmissives(this.renderer.getTextureLocation(state));
		if (emissiveTexture == null) return;

		submitNodeCollector.order(1)
			.submitModel(this.getParentModel(), state, poseStack, RenderTypes.eyes(emissiveTexture), lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
	}

	@Nullable
	private static Identifier resolveGlowtoneEmissives(Identifier baseTexture) {
		final Identifier cached = RESOLVED_TEXTURES.get(baseTexture);
		if (cached != null) return cached == NO_EMISSIVE_TEXTURE ? null : cached;

		final ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
		final Identifier resolved = GlowtoneConstants.findEmissiveVariant(
			baseTexture,
			candidate -> candidate,
			candidate -> resourceManager.getResource(candidate).isPresent()
		);

		RESOLVED_TEXTURES.put(baseTexture, resolved == null ? NO_EMISSIVE_TEXTURE : resolved);

		return resolved;
	}
}
