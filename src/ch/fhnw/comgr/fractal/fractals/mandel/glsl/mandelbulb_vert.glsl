#version 330

#include <view_block.glsl>

uniform bool useVertexColors;

uniform vec4 materialColor;
uniform float vertexPointSize;

in vec4 vertexPosition;
in vec4 vertexColor;

out vec4 vsColor;

void main() {
	vsColor = materialColor;
	if (useVertexColors)
		vsColor *= vertexColor;

	gl_PointSize = vertexPointSize;
	gl_Position = view.viewProjMatrix * vertexPosition;
}