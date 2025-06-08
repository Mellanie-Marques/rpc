import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class RemoteListServer {
    private static RemoteListImpl remoteList;

    public static void main(String[] args) {
        try {
            System.out.println("=== INICIANDO SERVIDOR REMOTELIST ===");

            // Cria o registry RMI na porta 1099 (porta padrão)
            System.out.println("Criando RMI Registry...");
            LocateRegistry.createRegistry(1099);

            // Cria uma instância do serviço
            System.out.println("Inicializando RemoteList...");
            remoteList = new RemoteListImpl();

            // Registra o serviço no registry
            System.out.println("Registrando serviço...");
            Naming.rebind("//localhost/RemoteList", remoteList);

            System.out.println("\n✅ Servidor RemoteList iniciado com sucesso!");
            System.out.println("📍 Servidor registrado em: //localhost/RemoteList");
            System.out.println("🔒 Sistema de persistência ativo");
            System.out.println("📸 Snapshots automáticos a cada 30 segundos");
            System.out.println("🚀 Aguardando conexões de clientes...");

            // Adicionar hook para shutdown gracioso
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\n🛑 Sinal de shutdown recebido...");
                if (remoteList != null) {
                    try {
                        remoteList.shutdown();
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println("Servidor finalizado.");
            }));

            // Manter o servidor em execução
            System.out.println("\n💡 Para parar o servidor, pressione Ctrl+C");
            System.out.println("=====================================\n");

            // Loop infinito para manter o servidor ativo
            Object lock = new Object();
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    System.out.println("Servidor interrompido.");
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Erro no servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
