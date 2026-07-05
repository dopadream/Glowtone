package net.frozenblock.glowtone.mixin.client.block;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.glowtone.client.render.model.GlowtoneFullbrightModel;
import net.frozenblock.glowtone.mixin.client.SingleVariantAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {

	@ModifyReturnValue(
		method = "getShadeBrightness(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F",
		at = @At("RETURN")
	)
	private float glowtone$noShadowForFullbright(float original, @Local(argsOnly = true) BlockState state) {
		BlockStateModel model = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(state);
		if (model instanceof SingleVariant singleVariant) {
			BlockStateModelPart part = ((SingleVariantAccessor) singleVariant).glowtone$getModel();
			if (part instanceof GlowtoneFullbrightModel fullbrightModel && fullbrightModel.glowtone$isFullbright()) {
				return 1.0F;
			}
		}

		return original;
	}
}
