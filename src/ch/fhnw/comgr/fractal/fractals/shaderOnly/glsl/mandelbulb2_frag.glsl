#version 330

uniform vec3 outputSize = vec3(500, 500, 0);
uniform float time = 12;
uniform float power;

uniform vec3 cameraPosition = vec3(0,0,8.5);

uniform vec3 color1 = vec3(0.4, 0.82, 0.91);
uniform vec3 color2 = vec3(0,0.31,1);
uniform vec3 color3 = vec3(0.15,0.12,0.49);
uniform vec3 color4 = vec3(0.44,0.12,0.76);
uniform vec3 color5 = vec3(0.05,0.06,0.18);

uniform int iterations = 6;

float surfacefunction(vec3 hitpoint) {
    vec3 z = vec3(0);
    float r = 0.0;
    for (int count=0; count<iterations; count+=1) {
        vec3 z2 = z*z;
        r = sqrt(dot(z,z));
        if (r>2.0) {
            break;
        }
        float order = power;

        float theta = atan(sqrt(z2.x + z2.y), z.z);
        float phi = atan(z.y, z.x);
        float powr = pow(r, order);

        float sinThetaOrder = sin(theta * order);
        z.x = (powr * sinThetaOrder * cos(phi * order));
        z.y = (powr * sinThetaOrder * sin(phi * order));
        z.z = (powr * cos(theta * order));

        z += hitpoint;
    }
    return r - 2.0;
}

void main(void) {
    int numlights = 5;
    vec3 lightsposition[5];
    lightsposition[0] = vec3( 2.0,-2.0,-2.0);
    lightsposition[1] = vec3( 2.0,-2.0, 2.0);
    lightsposition[2] = vec3(-2.0,-2.0, 2.0);
    lightsposition[3] = vec3(-2.0,-2.0,-2.0);
    lightsposition[4] = vec3( 2.0, 2.0,-2.0);
    vec3 lightsdiffuse[5];
    lightsdiffuse[0] = color1;
    lightsdiffuse[1] = color2;
    lightsdiffuse[2] = color3;
    lightsdiffuse[3] = color4;
    lightsdiffuse[4] = color5;

    const vec3 materialdiffuse = vec3(1.0, 1.0, 1.0);
    const float materialspecularexponent = 256.0;

    //interpolate eye position from billboard fragment coordinates
    vec2 position = -1.0+2.0*gl_FragCoord.xy/outputSize.xy;
    position.x *= outputSize.x/outputSize.y;

    //define camera position and target
    vec3 target = vec3(position,0.0) + vec3(0.0, 0.0,1.5);

    //do the matrix calculations by hand X-)
    //as mat4 constructor and arithmetic assignments are
    //currently broken (2010-09-21) on ATI cards i found
    //a workaround using vec4 constructors wich works on
    //both NVIDIA+ATI --- MAGIC. DO NOT TOUCH! -=#:-)
    float rubberfactor = 0.0025;
    float phi = 0.50*(time+(gl_FragCoord.y*rubberfactor));
    mat4 xrot = mat4(
        vec4(1.0,       0.0,      0.0, 0.0),
        vec4(0.0,  cos(phi), sin(phi), 0.0),
        vec4(0.0, -sin(phi), cos(phi), 0.0),
        vec4(0.0,       0.0,      0.0, 1.0)
    );
    float theta = 0.75*(time+(gl_FragCoord.y*rubberfactor));
    mat4 yrot = mat4(
        vec4(cos(theta), 0.0, -sin(theta), 0.0),
        vec4(       0.0, 1.0,         0.0, 0.0),
        vec4(sin(theta), 0.0,  cos(theta), 0.0),
        vec4(       0.0, 0.0,         0.0, 1.0)
    );
    float psi = 0.15*(time+(gl_FragCoord.y*rubberfactor));
    mat4 zrot = mat4(
        vec4( cos (psi), sin (psi), 0.0, 0.0),
        vec4(-sin (psi), cos (psi), 0.0, 0.0),
        vec4(       0.0,       0.0, 1.0, 0.0),
        vec4(       0.0,       0.0, 0.0, 1.0)        
    );

    vec3 camera = vec3(yrot*xrot*zrot*vec4(cameraPosition,1.0));
    target = vec3(yrot*xrot*zrot*vec4(target,1.0));

    vec3 ray = normalize(target-camera);
    //config raymarching bound parameters
    const float baseaccuracy = 0.01;
    const float marchingstepsize = baseaccuracy;
    const float marchingaccuracy = baseaccuracy/100.0;
    const float marchingmaxraylength = 10.0;
    const float epsilon = baseaccuracy/1000.0;
    //fixed step raymarching with simple bisection refinement
    float currentstep = marchingstepsize;
    float currentpos = currentstep;
    vec3 hitpoint = camera+currentpos*ray;
    bool currentevaluation = (surfacefunction(hitpoint)<0.0);
    currentpos += currentstep;
    bool startevaluation = currentevaluation;
    //core raymarching loop
    while (currentpos<marchingmaxraylength) {
        hitpoint = camera+currentpos*ray;
        currentevaluation = (surfacefunction(hitpoint)<0.0);
        //bisection inner loop
        if (currentevaluation!=startevaluation) {
            float temppos = currentpos-marchingstepsize;
            while (currentstep>marchingaccuracy) {
                currentstep *= 0.5;
                currentpos = temppos+currentstep;
                hitpoint = camera+currentpos*ray;
                currentevaluation = (surfacefunction(hitpoint)<0.0);
                if (currentevaluation==startevaluation) {
                    temppos = currentpos;
                }
            }
            //found an intersection :-) calculate the normal
            vec3 normal;
            //seems inefficient to me - there must be a better way to do this ?-)
            //single finite difference shot to approximate the normal derivate
            normal.x = surfacefunction(hitpoint+vec3(epsilon,0.0,0.0));
            normal.y = surfacefunction(hitpoint+vec3(0.0,epsilon,0.0));
            normal.z = surfacefunction(hitpoint+vec3(0.0,0.0,epsilon));
            //---
            normal -= surfacefunction(hitpoint);
            normal = normalize(normal);
            //phong shading calculations for surface hitpoint
            vec3 color = vec3(0, 0, 0);
            vec3 camera_direction = normalize(camera-hitpoint);
            for (int i=0; i<numlights; i++) {
                vec3 light_dir = normalize(lightsposition[i]-hitpoint);
                float diffuse = max(dot(light_dir, normal),0.0);
                float specular = max(dot(reflect(-light_dir, normal), camera_direction),0.0);
                color += (lightsdiffuse[i]*diffuse).xyz*materialdiffuse.xyz;
                //implicit white specular
                color += pow(specular, materialspecularexponent);
            }
            gl_FragColor = vec4(color, 1.0);
            return;
        }
        currentpos += currentstep;
    }
    //no intersection found
    gl_FragColor = vec4(0.0,0.0,0.0,1.0);
    return;
}