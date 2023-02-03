#version 460 core

//Vertex Shader

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textureCoord;
layout(location = 2) in vec3 normal;

out vec2 passTextureCoord;
out vec3 surfaceNormal;
out vec3 toLightVector;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform vec3 lightPosition;

void main() {
	vec4 worldPosition = model * vec4(position, 1.0);
	gl_Position = projection * view * worldPosition;
	passTextureCoord = textureCoord;
	
	surfaceNormal = (vec4(position, 0) * vec4(0, 0, -1, 1)).xyz;
	toLightVector = lightPosition - worldPosition.xyz;
}

#version 330 core

//Fragment Shader

in vec2 passTextureCoord;
in vec3 surfaceNormal;
in vec3 toLightVector;

out vec4 outColor;

uniform vec4 color;
uniform sampler2D tex;
uniform vec3 lightColor;
uniform float strength;

void main() {
	vec3 unitNormal = normalize(surfaceNormal);
	vec3 unitLightVector = normalize(toLightVector);
	
	float nDotl = dot(unitNormal, unitLightVector);
	float brightness = max(nDotl, 0.2);
	brightness = brightness * strength;
	
	vec3 diffuse = brightness * lightColor;
	float colorValue = color.w;
	
	outColor = vec4(diffuse, 1.0) * texture(tex, passTextureCoord);
	//outColor = mix(vec4(color.x, color.y, color.z, colorValue), outColor, colorValue);
	
	if(colorValue < 1.0) {
		outColor.x -= color.x;
		outColor.y -= color.y;
		outColor.z -= color.z;
		outColor.w -= (colorValue * 0.2);
	} else {
		if(colorValue != 1.0) {
			outColor = mix(vec4(color.x, color.y, color.z, 0.0), outColor, (colorValue - 1.1));
		}
	}
}