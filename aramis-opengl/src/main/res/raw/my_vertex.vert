
attribute vec4 aPosition;
void main(){
    float pointSize=15.0;
    gl_PointSize=pointSize;

    gl_Position=aPosition;
}