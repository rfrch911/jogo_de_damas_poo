package modelo;

import java.util.ArrayList;
import java.util.List;

import jogo.Movimento;
import jogo.Tabuleiro;

public class PecaComum extends Peca {

    // Direções diagonais possíveis para realizar capturas.
    private static final int[][] DIRECOES_CAPTURA = {
        {-1, -1},
        {-1, 1},
        {1, -1},
        {1, 1}
    };

    // Construtor da peça comum.
    public PecaComum(Cor corDaPeca) {
        super(corDaPeca);
    }

    // Retorna o símbolo da peça: b ou p.
    @Override
    public String getSimbolo() {
        return getCor().getSimbolo();
    }

    // Transforma a peça comum em dama.
    @Override
    public Peca promoverPeca() {
        return new Dama(getCor());
    }

    // =====================================================
    // MOVIMENTO SIMPLES
    // =====================================================

    @Override
    public List<Movimento> getMovimentosSimples(
            Tabuleiro tabuleiro, Posicao origem) {

        List<Movimento> movimentos = new ArrayList<>();

        // A peça avança de acordo com a direção da sua cor.
        int novaLinha = origem.getLinha() + getCor().getDirecao();

        // Verifica as diagonais esquerda e direita.
        int[] colunas = {
            origem.getColuna() - 1,
            origem.getColuna() + 1
        };

        for (int novaColuna : colunas) {

            Posicao destino = new Posicao(novaLinha, novaColuna);

            // Verifica se a posição existe e está vazia.
            if (destino.isDentroDoTabuleiro()
                    && tabuleiro.getPeca(destino) == null) {

                movimentos.add(new Movimento(origem, destino));
            }
        }

        return movimentos;
    }

    // =====================================================
    // CAPTURA SIMPLES E MÚLTIPLA
    // =====================================================

    @Override
    public List<List<Movimento>> getSequenciasCaptura(
            Tabuleiro tabuleiro, Posicao origem) {

        List<List<Movimento>> todasSequencias = new ArrayList<>();

        // Analisa as quatro diagonais.
        for (int[] direcao : DIRECOES_CAPTURA) {

            int linha = origem.getLinha();
            int coluna = origem.getColuna();

            // Casa ocupada pela peça adversária.
            Posicao posAlvo = new Posicao(
                linha + direcao[0],
                coluna + direcao[1]
            );

            // Casa onde a peça irá pousar.
            Posicao posPouso = new Posicao(
                linha + 2 * direcao[0],
                coluna + 2 * direcao[1]
            );

            // Verifica os limites das duas posições.
            if (!posAlvo.isDentroDoTabuleiro()
                    || !posPouso.isDentroDoTabuleiro()) {
                continue;
            }

            // A casa de pouso precisa estar vazia.
            if (tabuleiro.getPeca(posPouso) != null) {
                continue;
            }

            // Identifica a peça que será capturada.
            Peca alvo = tabuleiro.getPeca(posAlvo);

            // Verifica se a peça pode ser capturada.
            if (!this.podeCapturar(alvo)) {
                continue;
            }

            // Registra o movimento de captura.
            Movimento saltoAtual = new Movimento(origem, posPouso);

            // Marca o adversário como capturado.
            alvo.marcarCapturada();

            List<List<Movimento>> continuacoes;

            // Simula o movimento para procurar novas capturas.
            tabuleiro.removerPeca(origem);

            try {

                tabuleiro.colocarPeca(this, posPouso);

                // Busca recursivamente outras capturas possíveis.
                continuacoes = getSequenciasCaptura(
                    tabuleiro, posPouso
                );

            } finally {

                // Restaura o tabuleiro após a simulação.
                tabuleiro.removerPeca(posPouso);
                tabuleiro.colocarPeca(this, origem);

                // Libera a marcação temporária da peça capturada.
                alvo.desmarcarCapturada();
            }

            // Se não existem novas capturas, a sequência terminou.
            if (continuacoes.isEmpty()) {

                List<Movimento> sequencia = new ArrayList<>();

                sequencia.add(saltoAtual);

                todasSequencias.add(sequencia);

            } else {

                // Combina o salto atual com cada continuação possível.
                for (List<Movimento> resto : continuacoes) {

                    List<Movimento> sequencia = new ArrayList<>();

                    sequencia.add(saltoAtual);
                    sequencia.addAll(resto);

                    todasSequencias.add(sequencia);
                }
            }
        }

        return todasSequencias;
    }
}
