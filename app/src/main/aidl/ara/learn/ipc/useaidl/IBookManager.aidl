// IBookManager.aidl
package ara.learn.ipc.useaidl;

import ara.learn.ipc.useaidl.Book;
import ara.learn.ipc.useaidl.IOnNewBookArrivedListener;

interface IBookManager {

    List<Book> getBookList();

    void addBook(in Book book);

    //添加被动回调。AIDL只能使用同为AIDL的接口对象
    void registerListener(IOnNewBookArrivedListener listener);
    void unregisterListener(IOnNewBookArrivedListener listener);
}
