
void main(){
    float pointSize=15.0;
    gl_PointSize=pointSize;

    vec3 xyz;
    xyz=vec3(0.0,0.0,0.0);
    vec4 position;
    position=vec4(xyz[0],xyz[1],xyz[2],1);
    gl_Position=position;
}