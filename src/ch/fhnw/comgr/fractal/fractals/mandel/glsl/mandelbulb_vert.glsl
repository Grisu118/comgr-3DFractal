#version 330

#include <view_block.glsl>

uniform bool useVertexColors;

uniform vec4 materialColor;
uniform float vertexPointSize;
uniform float maxDistance;

in vec4 vertexPosition;
in vec4 vertexColor;

out vec4 vsColor;

/*
 * GLSL HSV to RGB+A conversion. Useful for many effects and shader debugging.
 *
 * Copyright (c) 2012 Corey Tabaka
 *
 * Hue is in the range [0.0, 1.0] instead of degrees or radians.
 * Alpha is simply passed through for convenience.
 */

vec4 hsv_to_rgb(float h, float s, float v, float a)
{
	float c = v * s;
	h = mod((h * 6.0), 6.0);
	float x = c * (1.0 - abs(mod(h, 2.0) - 1.0));
	vec4 color;

	if (0.0 <= h && h < 1.0) {
		color = vec4(c, x, 0.0, a);
	} else if (1.0 <= h && h < 2.0) {
		color = vec4(x, c, 0.0, a);
	} else if (2.0 <= h && h < 3.0) {
		color = vec4(0.0, c, x, a);
	} else if (3.0 <= h && h < 4.0) {
		color = vec4(0.0, x, c, a);
	} else if (4.0 <= h && h < 5.0) {
		color = vec4(x, 0.0, c, a);
	} else if (5.0 <= h && h < 6.0) {
		color = vec4(c, 0.0, x, a);
	} else {
		color = vec4(0.0, 0.0, 0.0, a);
	}

	color.rgb += v - c;

	return color;
}

void main() {
    vec4 vertex = view.viewProjMatrix * vertexPosition;
	vsColor = materialColor;
	if (useVertexColors) {
	    float dist = length(vertexPosition);
		vsColor *= hsv_to_rgb(dist/maxDistance, 1, 1, 1);
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

