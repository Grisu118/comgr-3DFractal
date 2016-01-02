#version 330

#include <view_block.glsl>

in vec4 vertexPosition;

void main() {
    vec4 vertex = view.viewProjMatrix * vertexPosition;
	gl_Position = vertex;
}

