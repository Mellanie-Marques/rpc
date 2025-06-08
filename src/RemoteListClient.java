import java.rmi.Naming;
import java.util.Arrays;
import java.util.Scanner;

public class RemoteListClient {
    private static RemoteListInterface remoteList;

    public static void main(String[] args) {
        try {
            // Conectar ao servidor
            System.out.println("Conectando ao servidor RemoteList...");
            remoteList = (RemoteListInterface) Naming.lookup("//localhost/RemoteList");
            System.out.println("✅ Conectado ao servidor!\n");

            // Menu interativo
            Scanner scanner = new Scanner(System.in);
            boolean running = true;

            while (running) {
                printMenu();
                System.out.print("Escolha uma opção: ");

                try {
                    int choice = scanner.nextInt();
                    scanner.nextLine(); // Consumir quebra de linha

                    switch (choice) {
                        case 1:
                            runBasicDemo();
                            break;
                        case 2:
                            runPersistenceTest();
                            break;
                        case 3:
                            runConcurrencyTest();
                            break;
                        case 4:
                            runInteractiveMode(scanner);
                            break;
                        case 5:
                            showCurrentState();
                            break;
                        case 0:
                            running = false;
                            break;
                        default:
                            System.out.println("❌ Opção inválida!");
                    }

                } catch (Exception e) {
                    System.out.println("❌ Erro: " + e.getMessage());
                    scanner.nextLine(); // Limpar buffer
                }

                if (running) {
                    System.out.println("\nPressione Enter para continuar...");
                    scanner.nextLine();
                }
            }

            System.out.println("Cliente finalizado.");
            scanner.close();

        } catch (Exception e) {
            System.err.println("❌ Erro no cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🎛️  MENU REMOTELIST CLIENT");
        System.out.println("=".repeat(50));
        System.out.println("1. 🚀 Demonstração Básica");
        System.out.println("2. 💾 Teste de Persistência");
        System.out.println("3. ⚡ Teste de Concorrência");
        System.out.println("4. 🎮 Modo Interativo");
        System.out.println("5. 📊 Estado Atual");
        System.out.println("0. 🚪 Sair");
        System.out.println("=".repeat(50));
    }

    private static void runBasicDemo() throws Exception {
        System.out.println("\n🚀 === DEMONSTRAÇÃO BÁSICA ===");

        // Limpar dados de teste anteriores (se existirem)
        try {
            String[] ids = remoteList.listIds();
            for (String id : ids) {
                if (id.startsWith("demo_")) {
                    while (remoteList.size(id) > 0) {
                        remoteList.remove(id);
                    }
                }
            }
        } catch (Exception e) {
            // Ignorar erros de limpeza
        }

        System.out.println("📝 Criando listas de demonstração...");

        // Lista de números pares
        System.out.println("\n➕ Adicionando números pares:");
        for (int i = 2; i <= 10; i += 2) {
            remoteList.append("demo_pares", i);
            System.out.printf("   Adicionado: %d\n", i);
        }

        // Lista de números ímpares
        System.out.println("\n➕ Adicionando números ímpares:");
        for (int i = 1; i <= 9; i += 2) {
            remoteList.append("demo_impares", i);
            System.out.printf("   Adicionado: %d\n", i);
        }

        // Consultas
        System.out.println("\n🔍 Consultando elementos:");
        System.out.printf("demo_pares[0] = %d\n", remoteList.get("demo_pares", 0));
        System.out.printf("demo_pares[2] = %d\n", remoteList.get("demo_pares", 2));
        System.out.printf("demo_impares[1] = %d\n", remoteList.get("demo_impares", 1));

        // Tamanhos
        System.out.println("\n📏 Tamanhos das listas:");
        System.out.printf("demo_pares: %d elementos\n", remoteList.size("demo_pares"));
        System.out.printf("demo_impares: %d elementos\n", remoteList.size("demo_impares"));

        // Remoções
        System.out.println("\n➖ Removendo elementos:");
        System.out.printf("Removido de demo_pares: %d\n", remoteList.remove("demo_pares"));
        System.out.printf("Removido de demo_impares: %d\n", remoteList.remove("demo_impares"));

        System.out.println("\n✅ Demonstração básica concluída!");
    }

    private static void runPersistenceTest() throws Exception {
        System.out.println("\n💾 === TESTE DE PERSISTÊNCIA ===");

        System.out.println("📝 Este teste verifica se os dados persistem após reiniciar o servidor.");
        System.out.println("💡 Para testar completamente:");
        System.out.println("   1. Execute este teste");
        System.out.println("   2. Pare o servidor (Ctrl+C)");
        System.out.println("   3. Reinicie o servidor");
        System.out.println("   4. Execute este teste novamente");

        // Criar lista específica para teste de persistência
        String testListId = "persistence_test";
        long timestamp = System.currentTimeMillis();

        System.out.println("\n➕ Adicionando dados de teste...");

        // Adicionar alguns valores únicos baseados no timestamp
        int baseValue = (int) (timestamp % 1000);
        for (int i = 0; i < 5; i++) {
            int value = baseValue + i;
            remoteList.append(testListId, value);
            System.out.printf("   Adicionado: %d\n", value);
        }

        System.out.println("\n📊 Estado atual da lista de teste:");
        showListContents(testListId);

        System.out.println("\n💾 Aguarde o próximo snapshot (até 30 segundos)...");
        System.out.println("🔄 Após o snapshot, você pode reiniciar o servidor para testar a recuperação.");

        System.out.println("\n✅ Dados de persistência criados!");
    }

    private static void runConcurrencyTest() throws Exception {
        System.out.println("\n⚡ === TESTE DE CONCORRÊNCIA ===");

        String testListId = "concurrency_test";
        int numThreads = 5;
        int operationsPerThread = 10;

        System.out.printf("🧵 Criando %d threads, cada uma fazendo %d operações...\n",
                numThreads, operationsPerThread);

        // Array para armazenar threads
        Thread[] threads = new Thread[numThreads];

        // Criar e iniciar threads
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                try {
                    RemoteListInterface localRemoteList =
                            (RemoteListInterface) Naming.lookup("//localhost/RemoteList");

                    for (int j = 0; j < operationsPerThread; j++) {
                        int value = threadId * 100 + j;
                        localRemoteList.append(testListId, value);
                        System.out.printf("Thread %d: adicionou %d\n", threadId, value);

                        // Pequena pausa para simular operações reais
                        Thread.sleep(100);
                    }

                } catch (Exception e) {
                    System.err.printf("Erro na thread %d: %s\n", threadId, e.getMessage());
                }
            });

            threads[i].start();
        }

        // Aguardar todas as threads terminarem
        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("\n📊 Resultado do teste de concorrência:");
        showListContents(testListId);

        int expectedSize = numThreads * operationsPerThread;
        int actualSize = remoteList.size(testListId);

        if (actualSize == expectedSize) {
            System.out.printf("✅ Sucesso! Tamanho esperado: %d, obtido: %d\n",
                    expectedSize, actualSize);
        } else {
            System.out.printf("⚠️  Divergência! Esperado: %d, obtido: %d\n",
                    expectedSize, actualSize);
        }

        System.out.println("\n✅ Teste de concorrência concluído!");
    }

    private static void runInteractiveMode(Scanner scanner) throws Exception {
        System.out.println("\n🎮 === MODO INTERATIVO ===");
        System.out.println("💡 Comandos disponíveis:");
        System.out.println("   append <lista> <valor>  - Adicionar valor à lista");
        System.out.println("   get <lista> <índice>    - Obter valor do índice");
        System.out.println("   remove <lista>          - Remover último elemento");
        System.out.println("   size <lista>            - Tamanho da lista");
        System.out.println("   show <lista>            - Mostrar conteúdo da lista");
        System.out.println("   list                    - Listar todas as listas");
        System.out.println("   exit                    - Sair do modo interativo");

        boolean interactive = true;
        while (interactive) {
            System.out.print("\n> ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) continue;

            String[] parts = input.split("\\s+");
            String command = parts[0].toLowerCase();

            try {
                switch (command) {
                    case "append":
                        if (parts.length != 3) {
                            System.out.println("❌ Uso: append <lista> <valor>");
                            break;
                        }
                        String listId = parts[1];
                        int value = Integer.parseInt(parts[2]);
                        remoteList.append(listId, value);
                        System.out.printf("✅ Adicionado %d à lista '%s'\n", value, listId);
                        break;

                    case "get":
                        if (parts.length != 3) {
                            System.out.println("❌ Uso: get <lista> <índice>");
                            break;
                        }
                        listId = parts[1];
                        int index = Integer.parseInt(parts[2]);
                        int result = remoteList.get(listId, index);
                        System.out.printf("📋 %s[%d] = %d\n", listId, index, result);
                        break;

                    case "remove":
                        if (parts.length != 2) {
                            System.out.println("❌ Uso: remove <lista>");
                            break;
                        }
                        listId = parts[1];
                        int removed = remoteList.remove(listId);
                        System.out.printf("🗑️ Removido %d da lista '%s'\n", removed, listId);
                        break;

                    case "size":
                        if (parts.length != 2) {
                            System.out.println("❌ Uso: size <lista>");
                            break;
                        }
                        listId = parts[1];
                        int size = remoteList.size(listId);
                        System.out.printf("📏 Lista '%s' tem %d elementos\n", listId, size);
                        break;

                    case "show":
                        if (parts.length != 2) {
                            System.out.println("❌ Uso: show <lista>");
                            break;
                        }
                        listId = parts[1];
                        showListContents(listId);
                        break;

                    case "list":
                        String[] ids = remoteList.listIds();
                        System.out.println("📝 Listas existentes: " + Arrays.toString(ids));
                        break;

                    case "exit":
                        interactive = false;
                        System.out.println("👋 Saindo do modo interativo...");
                        break;

                    default:
                        System.out.println("❌ Comando desconhecido: " + command);
                        break;
                }

            } catch (NumberFormatException e) {
                System.out.println("❌ Número inválido!");
            } catch (Exception e) {
                System.out.println("❌ Erro: " + e.getMessage());
            }
        }
    }

    private static void showCurrentState() throws Exception {
        System.out.println("\n📊 === ESTADO ATUAL DO SERVIDOR ===");

        String[] ids = remoteList.listIds();

        if (ids.length == 0) {
            System.out.println("🔍 Nenhuma lista encontrada no servidor.");
            return;
        }

        System.out.printf("📝 Total de listas: %d\n\n", ids.length);

        for (String listId : ids) {
            try {
                int size = remoteList.size(listId);
                System.out.printf("📋 Lista '%s': %d elementos\n", listId, size);

                // Mostrar alguns elementos se a lista não estiver vazia
                if (size > 0) {
                    System.out.print("   Conteúdo: [");

                    // Mostrar até 10 elementos
                    int maxShow = Math.min(size, 10);
                    for (int i = 0; i < maxShow; i++) {
                        if (i > 0) System.out.print(", ");
                        System.out.print(remoteList.get(listId, i));
                    }

                    if (size > 10) {
                        System.out.print(", ... (+" + (size - 10) + " elementos)");
                    }

                    System.out.println("]");
                }

            } catch (Exception e) {
                System.out.printf("   ❌ Erro ao acessar lista '%s': %s\n", listId, e.getMessage());
            }
        }

        System.out.println("\n✅ Estado atual exibido!");
    }

    private static void showListContents(String listId) throws Exception {
        try {
            int size = remoteList.size(listId);
            System.out.printf("📋 Lista '%s' (%d elementos):\n", listId, size);

            if (size == 0) {
                System.out.println("   (vazia)");
                return;
            }

            System.out.print("   [");
            for (int i = 0; i < size; i++) {
                if (i > 0) System.out.print(", ");
                System.out.print(remoteList.get(listId, i));
            }
            System.out.println("]");

        } catch (Exception e) {
            System.out.printf("❌ Erro ao mostrar lista '%s': %s\n", listId, e.getMessage());
        }
}
}
