# Sistema de Semáforos Inteligentes Suportado por Comunicações V2X

Este projeto, desenvolvido na disciplina **Redes Veiculares** (METI 2023/2024, Universidade do Minho), visa a implementação de um protótipo funcional de um sistema de semáforos inteligentes, baseado em comunicações veiculares V2X. 

## Resumo do Projeto

O objetivo principal é otimizar a fluidez do tráfego rodoviário em cruzamentos, utilizando dados de mobilidade enviados pelos veículos para adaptar dinamicamente os sinais dos semáforos.

O desenvolvimento do sistema ocorre em várias fases:

- **Fase 1:** Implementação inicial com semáforo físico, onde os veículos enviam mensagens contendo seus dados de mobilidade para a unidade de comunicação fixa (RSU). O servidor local processa essas informações e determina qual via recebe sinal verde, melhorando a fluidez do tráfego.
- **Fase 2:** Introdução de encaminhamento multihop para permitir que veículos fora do alcance direto do RSU possam retransmitir mensagens por meio de veículos vizinhos, ampliando a perceção do semáforo sobre a área circundante.
- **Fase 3:** Substituição do semáforo físico por um semáforo virtual, com o RSU enviando comandos de avanço ou paragem diretamente aos veículos.
- **Fase 4 (opcional):** Aperfeiçoamento do sistema com decisões distribuídas entre os próprios veículos ou com a coordenação entre múltiplos semáforos para maximizar a eficiência do tráfego.

A plataforma de simulação utilizada para o desenvolvimento e testes foi o **Eclipse MOSAIC**, uma ferramenta open-source que integra simuladores multi-domínio para mobilidade conectada e automatizada.

---

## 👥 Equipa do Projeto

- **Catarina Pereira** — PG53733
- **Inês Neves** — PG53864 
- **Leonardo Martins** - PG53996

---

## 🏁 Nota Final do Projeto

> ⭐ **11,4 valores** (escala de 0 a 20)

---

**Universidade do Minho**  
*Disciplina: Redes Veiculares (METI 2023/2024)*

  
