package ara.learn.ipc.usecontentp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Aramis
 * Date:2018/12/11
 * Description:
 */
public class BookProvider extends ContentProvider {

    private static final String TAG = "BookProvider";
    public static final String AUTHORITY = "ara.learn.ipc.usecontentp.BookProvider";
    public static final Uri BOOK_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/book");
    public static final Uri USER_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/user");

    public static final int BOOK_URI_CODE = 0;
    public static final int USER_URI_CODE = 1;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY, "book", BOOK_URI_CODE);
        sUriMatcher.addURI(AUTHORITY, "user", USER_URI_CODE);
    }

    private Context context;
    private SQLiteDatabase mDB;

    private String getTableName(Uri uri) {
        String tableName = null;
        switch (sUriMatcher.match(uri)) {
            case BOOK_URI_CODE:
                tableName = DBOpenHelper.BOOK_TABLE_NAME;
                break;
            case USER_URI_CODE:
                tableName = DBOpenHelper.USER_TABLE_NAME;
                break;
        }
        return tableName;
    }

    @Override
    public boolean onCreate() {
        loge("onCreate");

        context = getContext();
        //ContentProvider创建时是运行在UI线程中的，在实际开发中不推荐在这里进行数据库操作
        initProviderData();
        return true;
    }

    private void initProviderData() {
        mDB = new DBOpenHelper(context).getWritableDatabase();
        //清除这两个表的所有数据
        mDB.execSQL("delete from " + DBOpenHelper.BOOK_TABLE_NAME);
        mDB.execSQL("delete from " + DBOpenHelper.USER_TABLE_NAME);
        //初始数据
        mDB.execSQL("insert into book values(3,'Android');");
        mDB.execSQL("insert into book values(4,'iOS');");
        mDB.execSQL("insert into book values(5,'Python');");
        mDB.execSQL("insert into user values(1,'jake',1);");
        mDB.execSQL("insert into user values(2,'jasmine',0);");
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        loge("query");

        String table = getTableName(uri);
        if (table == null) {
            throw new IllegalArgumentException("unsupported Uri:" + uri);
        }
        return mDB.query(table, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        loge("getType");
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        loge("insert");
        String table = getTableName(uri);
        if (table == null) {
            throw new IllegalArgumentException("unsupported Uri:" + uri);
        }
        mDB.insert(table, null, values);
        //update,insert和delete会引起数据源的改变，这个时候我们需要通过ContentResolver的notifyChange方法来
        //通知外界当前ContentProvider中的数据已经发生改变。
        //可以通过ContentResolver的registerContentObserver注册，unregisterContentObserver注销
        context.getContentResolver().notifyChange(uri, null);
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        loge("delete");
        String table = getTableName(uri);
        if (table == null) {
            throw new IllegalArgumentException("unsupported Uri:" + uri);
        }
        int count = mDB.delete(table, selection, selectionArgs);
        if (count > 0) {
            context.getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        loge("update");
        String table = getTableName(uri);
        if (table == null) {
            throw new IllegalArgumentException("unsupported Uri:" + uri);
        }
        int row = mDB.update(table, values, selection, selectionArgs);
        if (row > 0) {
            context.getContentResolver().notifyChange(uri, null);
        }
        return row;
    }

    private void loge(String msg) {
        Log.e("BookProvider", msg + ",currentThread:" + Thread.currentThread());
    }
}
