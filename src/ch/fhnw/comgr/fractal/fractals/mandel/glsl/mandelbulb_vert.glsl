#version 330

#include <view_block.glsl>

uniform bool useVertexColors;

uniform vec4 materialColor;
uniform float vertexPointSize;

in vec4 vertexPosition;
in vec4 vertexColor;

out vec4 vsColor;

void main() {
    vec4 vertex = view.viewProjMatrix * vertexPosition;
	vsColor = materialColor;
	if (useVertexColors) {
		vsColor *= vertexColor;
	}
	vec4 whiteColor = vec4(1,1,1,1);
    float dFog = 0.3;

    vec4 fogColor = vec4(0.1f, 0.2f, 0.3f, 1.0f);
    float x = dFog * vertex.z;
    float s = exp(-x*x);
    vec4 interplatedColor = s*vsColor + (1-s)*fogColor;
    vsColor.rgb = min(interplatedColor.rgb, whiteColor.rgb);

	gl_PointSize = 3; //TODO form java code
	gl_Position = vertex;
}