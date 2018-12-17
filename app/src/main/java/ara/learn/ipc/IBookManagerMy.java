package ara.learn.ipc;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

import java.util.List;

import ara.learn.ipc.useaidl.Book;

/**
 * Created by Aramis
 * Date:2018/12/10
 * Description:手写Binder
 */
public interface IBookManagerMy extends IInterface {
    static final String DESCRIPTOR = "ara.learn.ipc.IBookManagerMy";

    //IBinder.FIRST_CALL_TRANSACTION + 0
    static final int TRANSACTION_getBookList = IBinder.FIRST_CALL_TRANSACTION;

    static final int TRANSACTION_addBook = IBinder.FIRST_CALL_TRANSACTION + 1;

    public List<Book> getBookList() throws RemoteException;

    public void addBook(Book book) throws RemoteException;
}
