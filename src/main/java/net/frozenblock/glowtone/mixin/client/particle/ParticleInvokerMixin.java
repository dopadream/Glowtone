package net.frozenblock.glowtone.mixin.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(EnvType.CLIENT)
@Mixin(Particle.class)
public interface ParticleInvokerMixin {
	@Invoker("getLightCoords")
	int glowtone$getLightCoords(float partialTickTime);
}
