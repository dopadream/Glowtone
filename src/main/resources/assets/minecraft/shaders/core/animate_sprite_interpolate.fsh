#version 330
#extension GL_ARB_separate_shader_objects : require

#include <minecraft:animation_sprite.glsl>

uniform sampler2D CurrentSprite;
uniform sampler2D NextSprite;

layout(location = 0) in float fAnimationProgress;
layout(location = 1) in vec2 texCoord0;

layout(location = 0) out vec4 fragColor;

void main() {
    vec4 currentColor = textureLod(CurrentSprite, texCoord0, MipMapLevel);
    vec4 nextColor = textureLod(NextSprite, texCoord0, MipMapLevel);

    // blend premultiplied so a pixel fading to alpha 0 stays the same color instead of becoming white
    float alpha = mix(currentColor.a, nextColor.a, fAnimationProgress);
    vec3 premultiplied = mix(currentColor.rgb * currentColor.a, nextColor.rgb * nextColor.a, fAnimationProgress);
    vec3 color = premultiplied / max(alpha, 1e-5);

    fragColor = vec4(color, alpha);
}
