# 📋 Enunciado do Trabalho — Análise de Tráfego com Wireshark

## Objetivos

Este trabalho tem como principais objetivos:

1. Familiarizar-se com a ferramenta de captura e análise de tráfego de rede **Wireshark**.  
2. Desenvolver competências na definição e implementação de uma estratégia eficaz para análise de tráfego de rede.

---

## Instruções para Execução

- Faça o download da ferramenta **Wireshark** ([https://www.wireshark.org/](https://www.wireshark.org/)).  
  > Nota: Caso utilize Windows, o instalador já inclui o **WinPcap** necessário para captura de pacotes.  

- Descarregue também o ficheiro de tráfego a analisar, fornecido em anexo a este enunciado.

---

## Orientações para o Relatório

O relatório deve ser **objetivo e informativo**. Evite descrever detalhadamente cada pacote ou protocolo, o que resultaria num documento muito extenso e redundante.

### Estratégia Recomendada

1. **Descreva a estratégia adotada para a análise do tráfego** — esta é uma parte essencial do relatório. Explique os critérios usados para selecionar o que analisar e o que omitir.

2. **Análise Estatística Inicial:** Utilize as funcionalidades estatísticas do Wireshark para obter uma visão geral do tráfego, identificando sessões relevantes.

3. **Análise Detalhada das Sessões:**  
   - Uma **sessão** é definida como um conjunto de pacotes de sinalização/inicialização de um protocolo, seguidos pelos pacotes de dados correspondentes.  
   - Tenha em conta que numa única sessão podem existir várias ligações (streams, segundo a nomenclatura do Wireshark).  
   - Exemplo: Numa sessão HTTP, a página principal pode carregar conteúdos adicionais (como imagens) que exigem múltiplas conexões.

---

## Recursos Auxiliares

- A documentação oficial do Wireshark: [https://www.wireshark.org/docs/](https://www.wireshark.org/docs/)  
- Tutoriais online e fóruns de apoio podem ser usados para ajudar na compreensão e execução do trabalho.

---

## Template para o Relatório

Para facilitar a elaboração do relatório, recomendamos usar o template fornecido em anexo. Este ajuda a estruturar a informação de forma clara e concisa.

---

Se tiverem dúvidas ou precisarem de esclarecimentos adicionais, não hesitem em contactar o docente responsável.

Boa sorte!
