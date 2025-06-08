import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class RemoteListImpl extends UnicastRemoteObject implements RemoteListInterface {
    private final ConcurrentHashMap<String, List<Integer>> lists;

    public RemoteListImpl() throws RemoteException {
        super();
        this.lists = new ConcurrentHashMap<>();
        System.out.println("RemoteListImpl inicializado!");
    }

    @Override
    public boolean append(String listId, int value) throws RemoteException {
        lists.computeIfAbsent(listId, k -> new ArrayList<>()).add(value);
        System.out.printf("APPEND: Lista '%s' = %s%n", listId, lists.get(listId));
        return true;
    }

    @Override
    public int get(String listId, int index) throws RemoteException {
        List<Integer> list = lists.get(listId);
        if (list == null || index < 0 || index >= list.size()) {
            throw new RemoteException("Índice inválido ou lista não existe");
        }
        return list.get(index);
    }

    @Override
    public int remove(String listId) throws RemoteException {
        List<Integer> list = lists.get(listId);
        if (list == null || list.isEmpty()) {
            throw new RemoteException("Lista não existe ou está vazia");
        }
        return list.remove(list.size() - 1);
    }

    @Override
    public int size(String listId) throws RemoteException {
        List<Integer> list = lists.get(listId);
        return list != null ? list.size() : 0;
    }

    @Override
    public String[] listIds() throws RemoteException {
        return lists.keySet().toArray(new String[0]);
    }

    @Override
    public void shutdown() throws RemoteException {
        System.out.println("Encerrando RemoteListImpl...");
        // Limpar recursos se necessário
    }
}