# Extrato e Histórico de Transações

Projeto Spring Boot para gerenciar transações financeiras sem atrasos.

## Visão Geral

O sistema registra transações e mantém um histórico de extratos por conta, permitindo consultar movimentações por períodos variados:

- Extrato por intervalo de datas / últimos X dias
- Extrato da semana atual
- Extrato do mês atual / últimos X meses
- Extrato do ano atual / últimos X anos

## Uso

Após subir a aplicação, as transações podem ser registradas pela camada de comandos. O histórico de extrato fica disponível em Redis e pode ser consultado rapidamente por datas ou intervalos predefinidos.

## Arquitetura

O projeto adota uma arquitetura CQRS (Command Query Responsibility Segregation) com separação clara entre comandos e consultas. Isso ajuda a manter a lógica de escrita e leitura isolada.

O projeto separa responsabilidades em camadas:

- `commands` — registros e atualizações de transações
- `queries` — consultas de extrato e sincronização com Redis
- `shared` — eventos de domínio compartilhados

## Vantagens da arquitetura para gerenciar transações

- Melhor isolamento entre operações de escrita e leitura, reduzindo impacto de consultas no fluxo de processamento das transações.
- Maior escalabilidade, pois cada camada pode ser otimizada ou dimensionada de forma independente.
- Processamento de transações mais confiável com filas e eventos de domínio, evitando inconsistências entre gravação e atualização de extratos.
- Resposta mais rápida nas consultas de histórico, usando Redis para leitura rápida e representações pré-calculadas.
- Mais fácil de testar e evoluir regras de negócio sem misturar lógica de registro de transações e leitura de extratos.

## Práticas de clean code utilizadas no projeto

- Separação de responsabilidades em camadas distintas para reduzir acoplamento e manter o código limpo.
- Nomes de classes e handlers significativos que refletem claramente sua responsabilidade no fluxo.
- Validação e regras de negócio centralizadas no `RegistrarTransacaoHandler` antes de persistir dados.
- Atualização de cache/consulta assíncrona via eventos de domínio com o `ExtradoSincronizadorHandler`.

## Componentes principais

- `RegistrarTransacaoHandler`:
  - valida transação
  - aplica taxas conforme tipo
  - salva no banco
  - dispara evento de transação concluída

- `ExtradoSincronizadorHandler`:
  - escuta evento de transação concluída após commit
  - atualiza Redis com o extrato da conta

- `ObterExtratoHandler`:
  - consultas de extrato usando dados armazenados em Redis
  - normaliza intervalos para retorno de dados no período correto

## Requisitos

- Java 17+
- Redis configurado e disponível
- Spring Boot
- Banco de dados compatível com JPA (dependendo da implementação do repositório)

## Como executar

1. Compile e execute o projeto usando sua IDE ou ferramenta de build.

Exemplo com Maven:

```bash
mvn clean install
mvn spring-boot:run
```

<!-- ## Melhoria de usabilidade

- Apresentar mensagens de erro claras em caso de transação duplicada ou não encontrada
- Garantir que a leitura de extrato retorne resultados ordenados por data mais recente
- Centralizar lógica de período e normalização de datas para evitar duplicação -->
