#version 330 core

in vec2 passTextureCoord;

out vec4 outColor;

uniform vec3 color;
uniform sampler2D tex;

void main() {
	vec4 texture = vec4(texture(tex, passTextureCoord).rgb * color.xyz, 1.0);
	outColor =  texture;
}