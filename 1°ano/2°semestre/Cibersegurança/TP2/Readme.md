# 📋 Enunciado do Exercício — Modelo de Segurança BLP e Lattice de Rótulos

## Contexto

Considerando um ambiente universitário, o objetivo é construir o lattice (reticulado) dos rótulos de segurança para os níveis de segurança:

- **P** (Public)  
- **C** (Confidential)  
- **SC** (Strictly Confidential)  

e as categorias:

- **AS** (Academic Services)  
- **ScS** (Scientific Services)  

---

## Parte 1 — Construção e Análise do Lattice

1. **Construir o lattice de rótulos de segurança** considerando:  
   - As propriedades fundamentais do modelo Bell-LaPadula (BLP).  
   - Os professores classificados no nível de segurança \((C, \{AS, ScS\})\).  
   - Os estudantes classificados no nível \((C, \{AS\})\).  
   - A implementação usual do modelo de controlo de acesso multilevel em sistemas informáticos.

2. **Analisar se é possível evitar que um estudante "trapaceie" com um professor**, isto é, avaliar se o modelo multilevel consegue prevenir a violação de políticas entre os níveis e categorias definidos.

---

## Parte 2 — Implementação Automática

- Elaborar uma proposta de **processo automático de implementação** deste modelo de controlo de acesso baseado em lattice, numa infraestrutura típica de Tecnologias de Informação e Comunicação (TIC).

---

## Notas e Referências

- É necessário compreender os aspetos formais do **modelo Bell-LaPadula (BLP)**.  
- Referências recomendadas para aprofundamento:

  - Sandhu, Ravi S. *"Lattice-based access control models"*, Computer, vol. 26, no. 11, 1993, pp. 9–19.  
  - [Cornell University - Access Control Notes (2011)](http://www.cs.cornell.edu/courses/cs5430/2011sp/NL.accessControl.html)  
  - [UNC Chapel Hill - Security Notes (1996)](http://www.cs.unc.edu/~dewan/242/f96/notes/prot/node1.html)

---

### Dicas

- Defina claramente os níveis e categorias, e as relações de ordem entre eles.  
- Utilize diagramas para ilustrar o lattice de segurança.  
- Explique os conceitos do modelo BLP e aplique-os ao contexto dado.  
- Considere como a infraestrutura TIC pode suportar e automatizar este esquema de controlo.

---

Se tiveres dúvidas ou quiseres discutir abordagens, podes abrir uma *issue* neste repositório.
