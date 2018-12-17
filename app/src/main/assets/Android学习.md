## Android IPC  
IPC是Inter-Process Communication的缩写，含义为进程间通信或者跨进程通信，是指两个进程之间进行数据交换的过程。

1. 在Menifest中使用android:process来创建一个进程，并不是常用的方式
```
#可以使用adb shell ps 来查看设备中运行的进程
<SecondActivity
    ...
    android:process="learn.cc.process1"
></SecondActivity>
<ThirdActivity
    ...
    android:process="learn.cc.process2"
></ThirdActivity>
    
```
Android系统回味每个引用分配一个唯一的UID，具有相同UID的应用才能共享数据。这里要说明的是，两个应用通过ShareUID泡在同一个  
进程中是有要求的，需要这两个应用有相同的ShareUID并且签名相同才可以。在这种情况下，他们可以互相访问对方的私有数据，比如data  
目录、组件信息等，不管它们是否泡在同一个进程中。当然如果它们跑在同一个进程中，那么除了能共享data目录、组件信息，还可以共享  
内存数据，或者说它们看起来就像是一个应用的两个部分。

2. 使用android:process来创建进程，这时创建出来的进程是和主进程无法数据共享的，继而带来以下问题
    1. 静态成员和单例模式完全失败。
    2. 线程同步机制完全失败。
    3. SharedPreferences的可靠性下降。
    4. Application会多次创建。

3. Binder。Android跨进程通信的方式之一。  
直观来说，Binder是Android中的一个雷，它实现了IBinder接口。从IPC角度来说，Binder是Android中的一种跨进程通信方式，Binder  
还可以理解为一种虚拟的屋里设备，它的设备驱动是/dev/binder，该通信方式在Linux中没有。从Android Framework角度来说，Binder  
是ServiceManager连接各种Manager（ActivityManager、WindowManager等等）和相应ManagerService的桥梁。从Android应用层  
来说，Binder是客户端和服务端进行通信的媒介，当bindService的时候，服务端会返回一个包含了服务端业务调用的Binder对象  
通过这个Binder对象，客户端就可以获取服务端提供的服务或者数据，合理的服务包括普通服务和基于AIDL的服务。
