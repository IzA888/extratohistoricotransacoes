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

## Teste de carga

O projeto inclui um script de teste de carga em `teste-carga.js` que utiliza k6 para validar o desempenho das operações de escrita e leitura.

- 20% das requisições simulam o registro de transações em `/commands/registrar`
- 80% das requisições consultam extratos em `/queries/{conta}/...`
- Cenários de ramp-up, pico e ramp-down com limites de falha e latência
- Permite testar endpoints como `semana`, `mes`, `ano`, `ultimos-dias`, `ultimos-meses` e `ultimos-anos`

Para executar o teste:

```bash
k6 run teste-carga.js
```

Ajuste `BASE_URL` no script caso o serviço esteja rodando em outro endereço ou porta.

### Resultados do teste de carga

⚠️ **Explicação dos resultados:**

O teste indica que o serviço respondeu corretamente na maioria das leituras e gravações, mas apresentou **alta latência em consultas**, com apenas **3% dos acessos de leitura atendidos em menos de 20ms**. Isso sugere a necessidade de **otimização de desempenho** para melhorar a velocidade das consultas de extrato.

```
TOTAL RESULTS 

    checks_total.......: 88989  494.016809/s
    checks_succeeded...: 57.28% 50975 out of 88989
    checks_failed......: 42.71% 38014 out of 88989

    ✗ Leitura - Status é 200
      ↳  99% — ✓ 39508 / ✗ 3
    ✗ Leitura - Resposta rápida (<20ms)
      ↳  3% — ✓ 1501 / ✗ 38010
    ✗ Escrita - Status é 201 ou 200
      ↳  99% — ✓ 9966 / ✗ 1

    NETWORK
    data_received..................: 205 MB 1.1 MB/s
    data_sent......................: 6.8 MB 38 kB/s

```