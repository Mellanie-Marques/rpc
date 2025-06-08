import java.rmi.Naming;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;

public class ConcurrentTestClient {
    private static final String SERVER_URL = "//localhost/RemoteList";
    private static final AtomicInteger operationCount = new AtomicInteger(0);
    private static final AtomicInteger errorCount = new AtomicInteger(0);

    public static void main(String[] args) {
        try {
            System.out.println("🚀 === TESTE DE CONCORRÊNCIA AVANÇADO ===");
            System.out.println("🎯 Simulando múltiplos clientes acessando simultaneamente");

            // Configurações do teste
            int numClients = 10;
            int operationsPerClient = 50;
            int testDurationSeconds = 30;

            System.out.printf("⚙️ Configuração:\n");
            System.out.printf("   • %d clientes simultâneos\n", numClients);
            System.out.printf("   • %d operações por cliente\n", operationsPerClient);
            System.out.printf("   • Duração: %d segundos\n", testDurationSeconds);

            // Executar teste
            runConcurrencyTest(numClients, operationsPerClient, testDurationSeconds);

            // Verificar integridade dos dados
            verifyDataIntegrity();

            System.out.println("\n✅ Teste de concorrência concluído!");

        } catch (Exception e) {
            System.err.println("❌ Erro no teste: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runConcurrencyTest(int numClients, int operationsPerClient, int durationSeconds)
            throws InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(numClients);

        System.out.println("\n🏁 Iniciando teste...");
        long startTime = System.currentTimeMillis();

        // Criar e executar clientes
        for (int clientId = 0; clientId < numClients; clientId++) {
            final int id = clientId;
            executor.submit(() -> runClientOperations(id, operationsPerClient));
        }

        // Aguardar por tempo determinado
        executor.shutdown();
        boolean finished = executor.awaitTermination(durationSeconds, TimeUnit.SECONDS);

        if (!finished) {
            System.out.println("⏰ Tempo limite atingido, forçando parada...");
            executor.shutdownNow();
        }

        long endTime = System.currentTimeMillis();
        double duration = (endTime - startTime) / 1000.0;

        // Estatísticas
        System.out.println("\n📊 === ESTATÍSTICAS ===");
        System.out.printf("⏱️ Tempo total: %.2f segundos\n", duration);
        System.out.printf("✅ Operações realizadas: %d\n", operationCount.get());
        System.out.printf("❌ Erros encontrados: %d\n", errorCount.get());
        System.out.printf("🚀 Taxa de operações: %.2f ops/segundo\n", operationCount.get() / duration);

        if (errorCount.get() == 0) {
            System.out.println("🎉 Nenhum erro de concorrência detectado!");
        } else {
            System.out.println("⚠️ Alguns erros foram encontrados - verifique os logs");
        }
    }

    private static void runClientOperations(int clientId, int numOperations) {
        try {
            // Conectar ao servidor
            RemoteListInterface remoteList =
                    (RemoteListInterface) Naming.lookup(SERVER_URL);

            Random random = new Random(clientId); // Seed baseada no ID do cliente
            String[] listIds = {"shared_list", "client_" + clientId, "stress_test"};

            for (int i = 0; i < numOperations; i++) {
                try {
                    String listId = listIds[random.nextInt(listIds.length)];
                    int operation = random.nextInt(4); // 0-3 para diferentes operações

                    switch (operation) {
                        case 0: // APPEND
                            int value = clientId * 1000 + i;
                            remoteList.append(listId, value);
                            System.out.printf("Client %d: APPEND %d to %s\n", clientId, value, listId);
                            break;

                        case 1: // GET (se lista não estiver vazia)
                            try {
                                int size = remoteList.size(listId);
                                if (size > 0) {
                                    int index = random.nextInt(size);
                                    int result = remoteList.get(listId, index);
                                    System.out.printf("Client %d: GET %s[%d] = %d\n", clientId, listId, index, result);
                                }
                            } catch (Exception e) {
                                // Lista pode estar vazia ou índice inválido
                            }
                            break;

                        case 2: // SIZE
                            int size = remoteList.size(listId);
                            System.out.printf("Client %d: SIZE %s = %d\n", clientId, listId, size);
                            break;

                        case 3: // REMOVE (se lista não estiver vazia)
                            try {
                                int removed = remoteList.remove(listId);
                                System.out.printf("Client %d: REMOVE %d from %s\n", clientId, removed, listId);
                            } catch (Exception e) {
                                // Lista pode estar vazia
                            }
                            break;
                    }

                    operationCount.incrementAndGet();

                    // Pequena pausa para simular tempo de processamento real
                    Thread.sleep(random.nextInt(50) + 10); // 10-60ms

                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    System.err.printf("Client %d - Erro na operação %d: %s\n",
                            clientId, i, e.getMessage());
                }
            }

            System.out.printf("🏁 Client %d finalizado (%d operações)\n", clientId, numOperations);

        } catch (Exception e) {
            errorCount.incrementAndGet();
            System.err.printf("❌ Client %d - Erro de conexão: %s\n", clientId, e.getMessage());
        }
    }

    private static void verifyDataIntegrity() {
        try {
            System.out.println("\n🔍 === VERIFICAÇÃO DE INTEGRIDADE ===");

            RemoteListInterface remoteList =
                    (RemoteListInterface) Naming.lookup(SERVER_URL);

            String[] listIds = remoteList.listIds();
            System.out.printf("📝 Total de listas criadas: %d\n", listIds.length);

            int totalElements = 0;
            for (String listId : listIds) {
                try {
                    int size = remoteList.size(listId);
                    totalElements += size;
                    System.out.printf("   Lista '%s': %d elementos\n", listId, size);

                    // Verificar alguns elementos aleatórios
                    if (size > 0) {
                        Random random = new Random();
                        for (int i = 0; i < Math.min(3, size); i++) {
                            int index = random.nextInt(size);
                            int value = remoteList.get(listId, index);
                            // Verificação básica: valores devem ser positivos (baseado na lógica do teste)
                            if (value < 0) {
                                System.out.printf("⚠️ Valor suspeito encontrado: %s[%d] = %d\n",
                                        listId, index, value);
                            }
                        }
                    }

                } catch (Exception e) {
                    System.err.printf("❌ Erro ao verificar lista '%s': %s\n", listId, e.getMessage());
                }
            }

            System.out.printf("📊 Total de elementos em todas as listas: %d\n", totalElements);
            System.out.println("✅ Verificação de integridade concluída!");

        } catch (Exception e) {
            System.err.println("❌ Erro na verificação: " + e.getMessage());
        }
    }
}
