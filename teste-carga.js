import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

// 1. Configuração dos Cenários de Carga
export const options = {
    stages: [
        { duration: '30s', target: 50 },  // Ramp-up: sobe de 0 a 50 usuários simultâneos em 30s
        { duration: '2m', target: 200 },  // Pico: segura 200 usuários batendo sem parar por 2 minutos
        { duration: '30s', target: 0 },   // Ramp-down: reduz para 0 usuários gradativamente
    ],
    thresholds: {
        http_req_failed: ['rate<0.01'],   // O teste falha se mais de 1% das requisições derem erro
        http_req_duration: ['p(95)<50'],  // 95% das requisições de leitura DEVEM responder em menos de 50ms
    },
};

const BASE_URL = 'http://host.docker.internal:8080'; // Alvo apontando para o seu Spring Boot

export default function () {
    // Simula IDs de contas rotativas para não testar sempre a mesma chave no Redis
    const contaId = randomIntBetween(10000, 10500); 
    const probabilidade = Math.random();

    if (probabilidade <= 0.20) {
        // Lista com os novos enums mapeados exatamente como no Java
        const tiposDisponiveis = [
            'PIX_RECEBIDO', 'PIX_ENVIADO', 
            'TED_ENVIADA', 'TED_RECEBIDA', 
            'COMPRA_DEBITO', 'COMPRA_CREDITO', 'ESTORNO'
        ];
        const tipoAleatorio = tiposDisponiveis[Math.floor(Math.random() * tiposDisponiveis.length)];

        // 📝 CENÁRIO DE ESCRITA (20% das requisições)
        // Salva no H2/Postgres e popula o ZSet do Redis via evento
        const payload = JSON.stringify({
            conta: contaId,
            descricao: 'Transação de Teste K6',
            tipo: tipoAleatorio,
            valor: parseFloat((Math.random() * 500).toFixed(2))
        });

        const params = { headers: { 'Content-Type': 'application/json' } };
        const res = http.post(`${BASE_URL}/commands/registrar`, payload, params);

        check(res, {
            'Escrita - Status é 201 ou 200': (r) => r.status === 201 || r.status === 200,
        });

    } else {
        // 📖 CENÁRIO DE LEITURA (80% das requisições)
        // Bate direto no endpoint que chama o seu `reverseRangeByScore` no Redis
       const rotaSorteada = randomIntBetween(1, 7);
        let res;

        switch (rotaSorteada) {
            case 1: // Customizado por período
                const inicio = '2026-06-22';
                const fim = '2026-06-23';
                res = http.get(`${BASE_URL}/queries/${contaId}/transacoes?inicio=${inicio}&fim=${fim}`);
                break;

            case 2: // Semana atual
                res = http.get(`${BASE_URL}/queries/${contaId}/semana`);
                break;

            case 3: // Mês atual
                res = http.get(`${BASE_URL}/queries/${contaId}/mes`);
                break;

            case 4: // Ano atual
                res = http.get(`${BASE_URL}/queries/${contaId}/ano`);
                break;

            case 5: // Últimos X Dias (Dinâmico)
                const dias = randomIntBetween(1, 30);
                res = http.get(`${BASE_URL}/queries/${contaId}/ultimos-dias?dias=${dias}`);
                break;

            case 6: // Últimos X Meses (Dinâmico)
                const meses = randomIntBetween(1, 12);
                res = http.get(`${BASE_URL}/queries/${contaId}/ultimos-meses?meses=${meses}`);
                break;

            case 7: // Últimos X Anos (Dinâmico)
                const anos = randomIntBetween(1, 2);
                res = http.get(`${BASE_URL}/queries/${contaId}/ultimos-anos?anos=${anos}`);
                break;
        }
        
        check(res, {
            'Leitura - Status é 200': (r) => r.status === 200,
            'Leitura - Resposta rápida (<20ms)': (r) => r.timings.duration < 20,
        });
    }
    // Tempo de pensamento do usuário (espera entre 100ms e 300ms antes da próxima ação)
    sleep(randomIntBetween(1, 3) / 10);
}