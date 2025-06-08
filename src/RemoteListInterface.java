import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteListInterface extends Remote{
    boolean append(String listId, int value) throws RemoteException;
    int get(String listId, int index) throws RemoteException;
    int remove(String listId) throws RemoteException;
    int size(String listId) throws RemoteException;
    String[] listIds() throws RemoteException;
    void shutdown() throws RemoteException;  // Adicionado método shutdown()
}
