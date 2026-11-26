package jogo;

import java.util.ArrayList;
import java.util.List;
import modelo.Cor;
import modelo.Jogador;
import modelo.Peca;
import modelo.Posicao;

public class Partida {
    private final Jogador jogadorBrancas;
    private final Jogador jogadorPretas;
    private final Tabuleiro tabuleiro;
    private StatusPartida status;
    private Cor turno;
    private Posicao pecaEmCapturaMultipla;
    private List<List<Movimento>> sequenciasEmAndamento;

    public Partida(String nomeBrancas, String nomePretas) {
        this.jogadorBrancas = new Jogador(nomeBrancas, Cor.BRANCA);
        this.jogadorPretas = new Jogador(nomePretas, Cor.PRETA);
        this.tabuleiro = new Tabuleiro();
        this.status = StatusPartida.EM_ANDAMENTO;
        this.turno = Cor.BRANCA;
        this.pecaEmCapturaMultipla = null;
        this.sequenciasEmAndamento = null;
    }

    public static Partida iniciarNovaPartida(String nomeBrancas, String nomePretas) {
        return new Partida(nomeBrancas, nomePretas);
    }

    public Partida(Jogador jogadorBrancas, Jogador jogadorPretas, StatusPartida status, Cor turno, Tabuleiro tabuleiro) {
        this.jogadorBrancas = jogadorBrancas;
        this.jogadorPretas = jogadorPretas;
        this.status = status;
        this.turno = turno;
        this.tabuleiro = tabuleiro;
        this.pecaEmCapturaMultipla = null;
        this.sequenciasEmAndamento = null;
        atualizarStatusPartida();
    }

    public void realizarMovimento(Movimento movimento) throws JogadaInvalidaException {
        if (status != StatusPartida.EM_ANDAMENTO) {
            throw new JogadaInvalidaException("A partida já foi encerrada.");
        }

        Posicao origem = movimento.getOrigem();
        Posicao destino = movimento.getDestino();
        Peca peca = tabuleiro.getPeca(origem);

        if (peca == null) {
            throw new JogadaInvalidaException("Não existe peça na posição de origem.");
        }

        if (peca.getCor() != turno) {
            throw new JogadaInvalidaException("A peça selecionada pertence ao adversário.");
        }

        if (isEmCapturaMultipla() && !origem.equals(pecaEmCapturaMultipla)) {
            throw new JogadaInvalidaException(
                "Você deve continuar a captura múltipla com a peça em " + pecaEmCapturaMultipla
            );
        }

        List<List<Movimento>> obrigatorias = isEmCapturaMultipla()
                ? sequenciasEmAndamento
                : tabuleiro.getSequenciasCapturaObrigatorias(turno);

        if (!obrigatorias.isEmpty()) {
            List<List<Movimento>> proximosPassos = new ArrayList<>();
            boolean lanceValido = false;

            for (List<Movimento> seq : obrigatorias) {
                if (seq.get(0).equals(movimento)) {
                    lanceValido = true;
                    if (seq.size() > 1) {
                        proximosPassos.add(seq.subList(1, seq.size()));
                    }
                }
            }

            if (!lanceValido) {
                throw new JogadaInvalidaException(
                    "Existe captura obrigatória (ou sequência com maior número de capturas)."
                );
            }

            Peca alvo = tabuleiro.encontrarPecaCapturadaNoSalto(origem, destino);
            if (alvo != null) {
                alvo.marcarCapturada();
                tabuleiro.removerPeca(origem);
                tabuleiro.colocarPeca(destino, peca);

                if (!proximosPassos.isEmpty()) {
                    this.pecaEmCapturaMultipla = destino;
                    this.sequenciasEmAndamento = proximosPassos;
                    return;
                }

                finalizarJogada(destino);
            }
        } else {
            List<Movimento> simples = peca.getMovimentosSimples(tabuleiro, origem);
            if (!simples.contains(movimento)) {
                throw new JogadaInvalidaException("Movimento inválido para esta peça.");
            }

            tabuleiro.removerPeca(origem);
            tabuleiro.colocarPeca(destino, peca);
            finalizarJogada(destino);
        }
    }

    private void finalizarJogada(Posicao destinoFinal) {
        tabuleiro.removerPecasMarcadasComoCapturadas();
        Peca pecaFinal = tabuleiro.getPeca(destinoFinal);
        if (pecaFinal != null && destinoFinal.getLinha() == pecaFinal.getCor().getLinhaPromocao()) {
            tabuleiro.colocarPeca(destinoFinal, pecaFinal.promoverPeca());
        }
        this.pecaEmCapturaMultipla = null;
        this.sequenciasEmAndamento = null;
        this.turno = this.turno.oposta();
        atualizarStatusPartida();
    }

    private void atualizarStatusPartida() {
        if (status != StatusPartida.EM_ANDAMENTO) {
            return;
        }
        if (tabuleiro.contarPecas(turno) == 0 || !tabuleiro.jogadorPossuiMovimentoLegal(turno)) {
            status = (turno == Cor.BRANCA) ? StatusPartida.VITORIA_PRETAS : StatusPartida.VITORIA_BRANCAS;
        }
    }

    public Jogador getJogadorBrancas() {
        return jogadorBrancas;
    }

    public Jogador getJogadorPretas() {
        return jogadorPretas;
    }

    public Jogador getJogadorAtual() {
        return turno == Cor.BRANCA ? jogadorBrancas : jogadorPretas;
    }

    public Tabuleiro getTabuleiro() {
        return tabuleiro;
    }

    public StatusPartida getStatus() {
        return status;
    }

    public Cor getTurno() {
        return turno;
    }

    public boolean isEmCapturaMultipla() {
        return pecaEmCapturaMultipla != null;
    }

    public Posicao getPecaEmCapturaMultipla() {
        return pecaEmCapturaMultipla;
    }
}
