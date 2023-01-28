#version 330 core

in vec2 passTextureCoord;

out vec4 FragColor;

uniform vec3 color;
uniform sampler2D tex;

bool isEmpty(sampler2D sampler) {
  return texelFetch(sampler, ivec2(0, 0), 0) == vec4(0, 0, 0, 0);
}

void main() {
  //vec4 texColor;
  
  //if(isEmpty(tex)) {
  //  texColor = vec4(color, 1.0);
  //} else {
    //texColor = mix(vec4(color, 1.0), texture(tex, passTextureCoord), 0.5);
  //}
  
  FragColor = mix(vec4(color, 1.0), texture(tex, passTextureCoord), 0.5);//texColor;
}