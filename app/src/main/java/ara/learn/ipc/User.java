package ara.learn.ipc;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Aramis
 * Date:2018/12/10
 * Description:
 */
public class User implements Parcelable,Serializable {
    private int id;
    private String userName;
    private boolean isMan;

    public User(int id, String userName, boolean isMan) {
        this.id = id;
        this.userName = userName;
        this.isMan = isMan;
    }

    protected User(Parcel in) {
        id = in.readInt();
        userName = in.readString();
        isMan = in.readByte() != 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isMan() {
        return isMan;
    }

    public void setMan(boolean man) {
        isMan = man;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", isMan=" + isMan +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(userName);
        dest.writeByte((byte) (isMan ? 1 : 0));
    }
}
