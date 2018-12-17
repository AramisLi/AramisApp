// IOnNewBookArrivedListener.aidl
package ara.learn.ipc.useaidl;

import ara.learn.ipc.useaidl.Book;
interface IOnNewBookArrivedListener {

    void onNewBookArrived(in Book book);
}
