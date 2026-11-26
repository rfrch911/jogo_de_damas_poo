# Jogo de Damas em Java (POO)
## 1. Integrantes do Grupo
* Rafael (Arquitetura base, Tabuleiro e Interface de Terminal)
* Integrante 1 (Movimentação e captura da Peça Comum)
* Integrante 2 (Movimentação e captura da Dama)
* Integrante 3 (Controle da Partida, Turnos e Vitória)
* Integrante 4 (Persistência em Arquivos Locais)
* Integrante 5 (Cenários de Teste, Relatório e Documentação)
## 2. Descrição do Sistema
Implementação em Java do jogo de damas brasileiro em tabuleiro 8x8 operado exclusivamente via terminal (console), permitindo partidas
entre dois jogadores humanos locais, validações de regras obrigatórias, promoção a dama, salvamento e carregamento de partidas em arquivo
texto.
## 3. Versão do Java Utilizada
* Java Development Kit (JDK) 8 ou superior (projeto Java convencional sem Maven/Gradle).
## 4. Como Importar o Projeto no Eclipse IDE
1. Abra o Eclipse IDE.
2. Acesse **File → Import → General → Existing Projects into Workspace**.
3. Selecione a pasta `jogodedamas` dentro deste repositório.
4. Clique em **Finish**.
## 5. Como Executar o Programa
1. No *Package Explorer*, navegue até `src/aplicacao/Main.java`.
2. Clique com o botão direito → **Run As → Java Application**.
3. Interaja através da aba **Console** do Eclipse.
## 6. Comandos Disponíveis
* `NOVO <jogador1> <jogador2>` — Inicia uma nova partida com 24 peças.
* `MOVER <origem> <destino>` — Executa um movimento ou etapa de captura (ex.: `MOVER A3 B4`).
* `ESTADO` — Exibe a matriz textual do tabuleiro (`TABULEIRO_INICIO` a `TABULEIRO_FIM`), o turno atual e o status.
* `SALVAR <arquivo>` — Salva o estado da partida em arquivo local (permitido entre turnos).
* `CARREGAR <arquivo>` — Recupera uma partida salva anteriormente.
* `SAIR` — Encerra a aplicação.
## 7. Principais Classes e Responsabilidades
* `aplicacao.Main` / `aplicacao.InterpretadorComandos`: Leitura de comandos do console e exibição de respostas.
* `jogo.Partida`: Orquestração dos turnos, captura obrigatória, Lei da Maioria, promoção e condições de vitória.
* `jogo.Tabuleiro`: Representação encapsulada da matriz 8x8, inicialização das peças e filtragem das maiores sequências de captura.
* `jogo.Movimento` / `jogo.StatusPartida`: Representação de um deslocamento e dos estados da partida (`EM_ANDAMENTO`, `VITORIA_BRANCAS`,
`VITORIA_PRETAS`).
4
* `modelo.Peca` (abstrata), `modelo.PecaComum` e `modelo.Dama`: Modelagem das peças e suas regras específicas de movimento e captura.
* `modelo.Posicao` / `modelo.Jogador` / `modelo.Cor`: Coordenadas do tabuleiro, jogadores e metadados das cores.
* `persistencia.PersistenciaPartida` (interface) / `persistencia.PersistenciaArquivo`: Contrato e implementação da leitura/escrita em
arquivos `.txt`.
## 8. Aplicação dos Quatro Pilares de POO
* **Abstração:** A classe abstrata `Peca` define o contrato de comportamento de qualquer peça no tabuleiro, enquanto a interface
`PersistenciaPartida` abstrai o mecanismo de armazenamento de dados.
* **Herança:** `PecaComum` e `Dama` estendem `Peca`, reaproveitando atributos (`cor`, `capturada`) e métodos comuns (`podeCapturar`).
* **Polimorfismo:** `Tabuleiro` e `Partida` invocam `getMovimentosSimples` e `getSequenciasCaptura` diretamente sobre referências de
`Peca`, executando comportamentos distintos para peças comuns e damas sem verificação explícita de tipos.
* **Encapsulamento:** Atributos são declarados como `private` e modificados apenas por métodos validados que lançam
`JogadaInvalidaException` ou `PersistenciaException` em caso de violação de regra.
## 9. Formato dos Arquivos de Persistência
Arquivos texto de 14 linhas contendo os metadados (`BRANCAS`, `PRETAS`, `TURNO`, `STATUS`) seguidos pelo bloco
`TABULEIRO_INICIO`..`TABULEIRO_FIM` com 8 linhas de 8 caracteres (`b`, `B`, `p`, `P`, `.`).
## 10. Instruções para Reproduzir os Testes Manuais
Os cenários preparados encontram-se em `jogodedamas/testes/cenarios/` e podem ser carregados via comando `CARREGAR testes/cenarios/
<arquivo>.txt`. Os resultados detalhados estão documentados em `jogodedamas/testes/relatorio-testes.md`.
## 11. Limitações Conhecidas
* As condições adicionais de empate oficiais (como empate por repetição de lances ou regra dos 20 lances de damas) não foram
implementadas, conforme previsto como item opcional na Seção 4.7 do enunciado.
