#version 330

in vec4 vsColor;

out vec4 fragColor;

void main() {
	vec2 t = gl_PointCoord - vec2(0.5);
	if (dot(t, t) > 0.25)
		discard;

	fragColor = vsColor;
}
