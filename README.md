# 🚀 Sistema RemoteList Distribuído

Sistema distribuído completo que implementa uma lista remota usando Java RMI, com suporte a persistência, snapshots automáticos e controle de concorrência.

## 📋 Funcionalidades Implementadas

### ✅ Operações RPC
- **`append(list_id, value)`** - Adiciona valor ao final da lista
- **`get(list_id, index)`** - Obtém valor de uma posição específica  
- **`remove(list_id)`** - Remove e retorna o último elemento
- **`size(list_id)`** - Retorna o número de elementos
- **`listIds()`** - Lista todas as listas existentes (método auxiliar)

### ✅ Múltiplas Listas
- Suporte a múltiplas listas identificadas por `list_id` único
- Criação automática de listas quando necessário
- Gerenciamento independente de cada lista

### ✅ Persistência Completa
- **Sistema de Logs**: Registra todas as operações (append/remove)
- **Snapshots Automáticos**: Salva estado completo a cada 30 segundos
- **Recuperação Automática**: Carrega estado anterior na inicialização

### ✅ Controle de Concorrência
- **Locks Granulares**: Um lock por lista para máxima eficiência
- **ReadWriteLocks**: Operações de leitura simultâneas, escrita exclusiva
- **Lock Global**: Para operações que afetam múltiplas listas
- **Thread-Safe**: Suporte a múltiplos clientes simultâneos

### ✅ Snapshot em Background
- Thread separada para criação de snapshots
- Não bloqueia operações normais durante snapshot
- Coordenação entre logs e snapshots para recuperação consistente

## 🔧 Compilação e Execução

### 1. Compilar o Projeto
```bash
# Compilar todos os arquivos Java
javac *.java

# Ou compilar individualmente
javac RemoteListInterface.java
javac RemoteListImpl.java
javac RemoteListServer.java
javac RemoteListClient.java
javac RemoteListClientEnhanced.java
javac ConcurrentTestClient.java
```

### 2. Executar o Servidor
```bash
# Iniciar o servidor (em um terminal separado)
java RemoteListServer
```

**Saída esperada:**
```
=== INICIANDO SERVIDOR REMOTELIST ===
Criando RMI Registry...
Inicializando RemoteList...
RemoteList Server iniciado com persistência!
Sistema de logs e snapshots ativo.
Scheduler de snapshots iniciado (intervalo: 30s)

=== RECUPERANDO ESTADO ANTERIOR ===
Nenhum estado anterior encontrado. Iniciando com listas vazias.
=====================================

✅ Servidor RemoteList iniciado com sucesso!
📍 Servidor registrado em: //localhost/RemoteList
🔒 Sistema de persistência ativo
📸 Snapshots automáticos a cada 30 segundos
🚀 Aguardando conexões de clientes...
```

### 3. Executar Clientes

#### Cliente 
```bash
java RemoteListClientEnhanced
```

**Menu do Cliente:**
```
==================================================
🎛️  MENU REMOTELIST CLIENT
==================================================
1. 🚀 Demonstração Básica
2. 💾 Teste de Persistência
3. ⚡ Teste de Concorrência
4. 🎮 Modo Interativo
5. 📊 Estado Atual
0. 🚪 Sair
==================================================
```

#### Teste de Concorrência
```bash
java ConcurrentTestClient
```

## 🧪 Testes Disponíveis

### 1. Teste Básico de Funcionalidade
- Criação de múltiplas listas
- Operações básicas (append, get, remove, size)
- Tratamento de erros

### 2. Teste de Persistência
1. Execute o teste de persistência
2. Pare o servidor (Ctrl+C)
3. Reinicie o servidor
4. Execute o teste novamente
5. Verifique se os dados foram recuperados

### 3. Teste de Concorrência
- 10 clientes simultâneos
- 50 operações por cliente
- Teste por 30 segundos
- Verificação de integridade dos dados

### 4. Modo Interativo
Comandos disponíveis:
```
append <lista> <valor>  - Adicionar valor à lista
get <lista> <índice>    - Obter valor do índice
remove <lista>          - Remover último elemento
size <lista>            - Tamanho da lista
show <lista>            - Mostrar conteúdo da lista
list                    - Listar todas as listas
exit                    - Sair do modo interativo
```


**✅ Projeto 100% Funcional - Todas as funcionalidades implementadas!**
