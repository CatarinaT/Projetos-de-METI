# Enunciado
Este trabalho tem por objectivo:
1. Familiarização com uma ferramenta de captura e análise tráfego (no caso, o wireshark)
2. Desenvolvimento de competências na definição e implementação de uma estratégia adequada à análise de tráfego em rede.
Para executar o trabalho deve descarregar a ferramenta wireshark (já deve incluir o winpcap, caso esteja a usar o Windows) e descarregar o ficheiro de tráfego a analisar, em anexo a esta descrição.

O relatório a produzir deve ser objetivo e informativo. Se começar a descrever cada pacote e cada protocolo, vai acabar por produzir um relatório com várias dezenas de páginas, com informação redundante. Assim, a estratégia a adotar é bastante relevante e deve ser inicialmente descrita no relatório.

Deve então começar por compreender o tráfego recolhido, utilizando as funções estatísticas do wireshark, procurando identificar as sessões e, após isso, analisar isoladamente cada sessão - neste contexto, uma sessão compreende um conjunto de pacotes de sinalização/inicialização de um protocolo, seguidos de pacotes de dados; numa só sessão podem coexistir diversas ligações - streams, na nomenclatura do wireshark - (e.g., numa sessão HTTP, uma página pode conter links para conteúdos, como imagens, que obrigam ao estabelecimento de mais ligações, para os transferir). Para facilitar a decisão sobre o que deve colocar no relatório, pode (e deve sorriso) usar este template.

A documentação do wireshark, assim como vários tutoriais disponíveis online, podem ajudar a realizar o trabalho...
