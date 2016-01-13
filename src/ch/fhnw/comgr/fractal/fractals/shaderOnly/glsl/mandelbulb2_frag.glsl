/**
 **   __ __|_  ___________________________________________________________________________  ___|__ __
 **  //    /\                                           _                                  /\    \\  
 ** //____/  \__     __ _____ _____ _____ _____ _____  | |     __ _____ _____ __        __/  \____\\ 
 **  \    \  / /  __|  |     |   __|  _  |     |  _  | | |  __|  |     |   __|  |      /\ \  /    /  
 **   \____\/_/  |  |  |  |  |  |  |     | | | |   __| | | |  |  |  |  |  |  |  |__   "  \_\/____/   
 **  /\    \     |_____|_____|_____|__|__|_|_|_|__|    | | |_____|_____|_____|_____|  _  /    /\     
 ** /  \____\                       http://jogamp.org  |_|                              /____/  \    
 ** \  /   "' _________________________________________________________________________ `"   \  /    
 **  \/____.                                                                             .____\/     
 **
 ** Advanced fragment shader implementing a GPGPU raymarcher to render a high order Mandelbulb Julia set.
 ** The raymarching process is bound by "marchingmaxraylength". Raymarching accuracy can be configured 
 ** with "marchingstepsize" and "marchingaccuracy". Surface normal approximation can be controlled with 
 ** "epsilon". Uses phong based lighting with specular (implicit white). Currently 8 lightsources are 
 ** configured in a cube-layout around the worlds origin. Lightsource colors a obtained from a 1D LUT 
 ** configured as sampler uniform. The Matrix setup and calculation is done by hand as using the buildin
 ** OpenGL matrices would be quite inconvenient handling wise.
 **
 ** On Windows consider to disable/reconfigure this "feature" when using a slow GPU: 
 ** http://www.blog-gpgpu.com/ 
 ** http://www.microsoft.com/whdc/device/display/wddm_timeout.mspx
 **
 ** Inspired by the following sources:
 ** IQ's Terrain Raymarching http://iquilezles.org/www/articles/terrainmarching/terrainmarching.htm
 ** Potatro, RayMarching and DistanceFields - A story of SphereTracing: http://code4k.blogspot.com/2009/10/potatro-and-raymarching-story-of.html
 ** Algebraic surfaces: http://www.freigeist.cc/gallery.html
 ** Raymarching discussion on pouet.net: http://www.pouet.net/topic.php?which=6675&page=1&x=13&y=12
 ** Bisection and isosurface normal approximation: http://sizecoding.blogspot.com/2008/08/isosurfaces-in-glsl.html
 ** Raytracing on the GPU (in german): http://www.uninformativ.de/?section=news&ndo=single&newsid=108
 ** WebGL Quaternionic Julia Set raymarching: http://www.iquilezles.org/apps/shadertoy/
 ** Ray Tracing Quaternion Julia Sets on the GPU: http://www.devmaster.net/forums/showthread.php?t=4448
 ** GPU Gems 3 - Chapter 30 - Real-Time Simulation and Rendering of 3D Fluids: http://http.developer.nvidia.com/GPUGems3/gpugems3_ch30.html
 ** GPU Raycasting Tutorial: http://cg.alexandra.dk/2009/04/28/gpu-raycasting-tutorial/
 ** A Simple and Flexible Volume Rendering Framework for Graphics-Hardware-based Raycasting: http://www.vis.uni-stuttgart.de/ger/research/fields/current/spvolren/
 **/

#version 330

uniform vec3 outputSize = vec3(500, 500, 0);
uniform float power = 10;

uniform vec3 cameraPosition = vec3(0,0,8.5);

float time = power;

float surfacefunction(vec3 hitpoint) {
    vec3 z = hitpoint;
    vec3 c = vec3(sin(time/15.0)*0.66,sin(time/15.0)*0.1,sin(time/15.0));
    float r = 0.0;
    for (float count=0.0; count<5.0-1.0; count+=1.0) {
        vec3 z2 = z*z;
        r = sqrt(dot(z,z));
        if (r>2.0) {
            break;
        }
        float planeXY = sqrt(z2.x+z2.y)+0.0000001;
        r += 0.0000001;
        float sinPhi = z.y/planeXY;
        float cosPhi = z.x/planeXY;
        float sinThe = planeXY/r;
        float cosThe = z.z/r;
        //level 1
        sinPhi = 2.0*sinPhi*cosPhi;
        cosPhi = 2.0*cosPhi*cosPhi-1.0;
        sinThe = 2.0*sinThe*cosThe;
        cosThe = 2.0*cosThe*cosThe-1.0;
        //level 2.
        sinPhi = 2.0*sinPhi*cosPhi;
        cosPhi = 2.0*cosPhi*cosPhi-1.0;
        sinThe = 2.0*sinThe*cosThe;
        cosThe = 2.0*cosThe*cosThe-1.0;
        //level 3.
        sinPhi = 2.0*sinPhi*cosPhi;
        cosPhi = 2.0*cosPhi*cosPhi-1.0;
        sinThe = 2.0*sinThe*cosThe;
        cosThe = 2.0*cosThe*cosThe-1.0;
        float rPow = pow(r, 8.0);
        z.x = sinThe*cosPhi;
        z.y = sinThe*sinPhi;
        z.z = cosThe;
        z *= rPow;
        z += c;
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
    lightsdiffuse[0] = vec3(0.4, 0.82, 0.91);
    lightsdiffuse[1] = vec3(0,0.31,1);
    lightsdiffuse[2] = vec3(0.15,0.12,0.49);
    lightsdiffuse[3] = vec3(0.44,0.12,0.76);
    lightsdiffuse[4] = vec3(0.05,0.06,0.18);

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