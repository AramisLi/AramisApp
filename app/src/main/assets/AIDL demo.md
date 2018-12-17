## AIDL 创建步骤

### 1.新建一个javaBean，并实现Parcelable接口。如Book.java
```java
package ara.learn.ipc;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Aramis
 * Date:2018/12/10
 * Description:
 */
public class Book implements Parcelable {
    private int bookId;
    private String bookName;

    protected Book(Parcel in) {
        bookId = in.readInt();
        bookName = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", bookName='" + bookName + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(bookId);
        dest.writeString(bookName);
    }
}
```

### 2.创建一个实体类的aidl文件
1. 右键 new->file。Book.aidl
2. 写入代码，不论这个aidl文件写在哪里，包名一定要和javaBean包名的一致
```aidl
package ara.learn.ipc;

parcelable Book;
```
### 3.创建服务端接口aidl
1. 右键 new->AIDL->AIDL File。IBookManager.aidl
2. 引入实体类
3. 添加想要的方法(输入类型前要加in，输出类型前要加out，既是输入又是输出加inout)
```aidl
// IBookManager.aidl
package ara.learn.ipc;

// Declare any non-default types here with import statements
import  ara.learn.ipc.Book;


interface IBookManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    List<Book> getBookList();
    void addBook(in Book book);
}
```
### 4.build project以后，就会在app/build/generated/source/aidl/debug/包名/下找到IDE为我们创建的IBookManager.java了

### 5.分析

1. DESCRIPTOR:Binder的唯一标识，一般用当前Binder的类名表示。如"ara.learn.ipc.IBookManager"
2. asInterface(android.os.IBinder obj):用于将服务端的Binder对象转换成客户端所需要的AIDL接口类型的对象，这种  
转换过程是区分进程的，如果客户端和服务端位于同一进程，那么此方法返回的就是服务端的Stub对象本身，否则返回的是系统封装后的Stub.proxy对象。
3. asBinder:此方法用于返回当前Binder对象。
4. onTransact:这个方法运行在服务端中的Binder线程池中，当客户端发起跨进程请求时，远程请求会通过系统底层封装后交由此方法来处理。  
该方法的原型为 public Boolean OnTransact(int code,android.os.Parcel data,android.os.Parcel reply,int flags)。  
服务端通过code可以确定客户端所请求的目标方法是什么，接着从data中取出目标方法所需的参数（如果目标方法有参数的话），然后执行  
目标方法。当目标方法执行完毕后，就想reply中写入返回值（如果目标方法有返回值的话），onTransact方法的执行过程就是这样的。需  
要注意的是，如果方法返回false，那么客户端的请求会失败，因此我们可以利用这个特性来做权限验证，毕竟我们也不希望随便一个进程  
都能远程调用我们的服务。
