
attribute vec4 vPosition;//把顶点坐标给这个变量，确定画画的形状
attribute vec2 vCoord;//接收纹理坐标，接收采样器采样图片的坐标
varying vec2 aCoord;//传给片元着色器->>像素点

//入口函数
void main(){
    //gl_Position是内置变量，把顶点数据赋值给这个变量 opengl就知道它要画什么形状了
    gl_Position=vPosition;
    aCoord=vCoord;
}