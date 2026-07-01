package net.frozenblock.glowtone.mixin.client.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(EnvType.CLIENT)
@Mixin(RenderType.class)
public interface RenderTypeInvokerMixin {
	@Invoker("create")
	static RenderType glowtone$create(String name, RenderSetup state) {
		throw new AssertionError("Glowtone RenderType injection failed");
	}
}
