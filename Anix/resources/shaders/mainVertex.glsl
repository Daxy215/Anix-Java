#version 460 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textureCoord;
layout(location = 2) in vec3 normal;

out vec2 passTextureCoord;
out float var_lightIntensity;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

uniform vec3 lightPosition;

void main() {
	passTextureCoord = textureCoord;
	vec4 worldPos =  model * vec4(position.xyz, 1.0);
	
	vec3 toLightVector = normalize(lightPosition - worldPos.xyz);
	var_lightIntensity = max(0.2, dot(normal, toLightVector));
	
	gl_Position = projection * view * worldPos;
}