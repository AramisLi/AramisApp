package ara.learn.ipc.binderpool;

import android.os.RemoteException;

/**
 * Created by Aramis
 * Date:2018/12/12
 * Description:
 */
public class ComputeImpl extends ICompute.Stub {
    @Override
    public int add(int a, int b) throws RemoteException {
        return a + b;
    }
}
