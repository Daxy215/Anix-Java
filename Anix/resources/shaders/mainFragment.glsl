#version 330 core

in vec2 passTextureCoord;
in float var_lightIntensity;

out vec4 outColor;

uniform vec3 color;
uniform sampler2D tex;

void main() {
	//vec4 light = clamp(vec4(var_lightIntensity, 1.0), 0.0, 1.0);
	
	vec4 texture = vec4(texture(tex, passTextureCoord).rgb * color.xyz * var_lightIntensity, 1.0);
	//outColor = light * texture(tex, passTextureCoord) * vec4(color.xyz, 1);
	outColor =  texture;
}