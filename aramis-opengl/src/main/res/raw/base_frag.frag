
//指明float是什么精度(中等)
precision mediump float;

//采样点的坐标-->要与顶点着色器中的变量名一致
varying vec2 aCoord;

//采样器-->正常是smapler2D
uniform sampler2D vTexture;

void main(){
    //内置变量接受像素值
    //texture2D:采样器采集aCoord像素
    //赋值给gl_FragColor
    gl_FragColor=texture2D(vTexture,aCoord);
}