package jogo;

import modelo.Cor;
import modelo.Peca;
import modelo.PecaComum;
import modelo.Posicao;

import java.util.ArrayList;
import java.util.List;

public class Tabuleiro {
	private final Peca[][] casas = new Peca[8][8];
	 	
	public void limpar() {
		for(int linha = 0; linha < 8; linha++) {
			for(int coluna = 0; coluna < 8; coluna++) {
				casas[linha][coluna] = null;
			}
		}
	}
	
	public void inicializarTabuleiro() {
		limpar();
		for(int linha = 0; linha < 8; linha++) {
			for(int coluna = 0; coluna < 8; coluna++) {
				Posicao pos = new Posicao(linha, coluna);
				if (pos.isEscura()) {
					if (linha >= 0 && linha < 3) {
						casas[linha][coluna] = new PecaComum(Cor.BRANCA);
					}
					if (linha > 4 && linha < 8) {
						casas[linha][coluna] = new PecaComum(Cor.PRETA);
					}
				}
			}
		}
	}
	
	public int contarPecas(Cor corDoJogador) {
  	  int total = 0;
   	 for(int linha = 0; linha < 8; linha++) {
      	  for(int coluna = 0; coluna < 8; coluna++) {
            	if(casas[linha][coluna] != null &&
                corDoJogador == casas[linha][coluna].getCor()) {
                total++;
           	 }
       	 }
   	 }
  	  return total;
	}

	public void removerPecasMarcadasComoCapturadas() {
		for(int linha = 0; linha < 8; linha++) {
			for(int coluna = 0; coluna < 8; coluna++) {
				if (casas[linha][coluna] != null && casas[linha][coluna].isCapturada()) {
					casas[linha][coluna] = null;
				}
			}
		}
	}
	
	public Peca getPeca(Posicao espaco) {
		if (espaco == null || !espaco.isDentroDoTabuleiro()) {
			return null;
		}
		return casas[espaco.getLinha()][espaco.getColuna()];
	}
	
	public void colocarPeca(Posicao espaco, Peca circulo) {
		if (espaco == null || !espaco.isDentroDoTabuleiro()) {
			return;
		}
		casas[espaco.getLinha()][espaco.getColuna()] = circulo;
	}
	
	public void removerPeca(Posicao espaco) {
		colocarPeca(espaco, null);
	}
	
	public String gerarRepresentacaoTextual() {
		StringBuilder sb = new StringBuilder();
		sb.append("TABULEIRO_INICIO\n");
		for (int linha = 7; linha >= 0; linha--) {
			for (int coluna = 0; coluna < 8; coluna++) {
				Peca p = casas[linha][coluna];
				sb.append(p == null ? "." : p.getSimbolo());
			}
			sb.append("\n");
		}
		sb.append("TABULEIRO_FIM");
		return sb.toString();
	}
	
	public Peca encontrarPecaCapturadaNoSalto(Posicao origem, Posicao destino) {
        int stepL = Integer.compare(destino.getLinha(), origem.getLinha());
        int stepC = Integer.compare(destino.getColuna(), origem.getColuna());
        int linha = origem.getLinha() + stepL;
        int coluna = origem.getColuna() + stepC;
        while (linha != destino.getLinha() && coluna != destino.getColuna()) {
            Peca p = casas[linha][coluna];
            if (p != null) {
                return p;
            }
            linha += stepL;
            coluna += stepC;
        }
        return null;
    }
	
	//retorna uma lista contendo os melhores movimentos de captura para um jogador de uma certa cor
	public List<List<Movimento>> getSequenciasCapturaObrigatorias(Cor corJogador) {
        List<List<Movimento>> melhoresSequencias = new ArrayList<>();
        int maxCapturas = 0;
        for (int linha = 0; linha < 8; linha++) {
            for (int coluna = 0; coluna < 8; coluna++) {
                Peca p = casas[linha][coluna];
                if (p != null && !p.isCapturada() && p.getCor() == corJogador) {
                    Posicao origem = new Posicao(linha, coluna);
                    List<List<Movimento>> seqs = p.getSequenciasCaptura(this, origem);
                    for (List<Movimento> seq : seqs) {
                        if (seq.size() > maxCapturas) {
                            maxCapturas = seq.size();
                            melhoresSequencias.clear();
                            melhoresSequencias.add(seq);
                        } else if (seq.size() == maxCapturas && maxCapturas > 0) {
                            melhoresSequencias.add(seq);
                        }
                    }
                }
            }
        }
        return melhoresSequencias;
    }

	//se o jogador pode fazer um movimento de captura, força o jogador a fazer o melhor movimento
	//se o jogador não tem um movimento de captura, permite qualquer movimento
    public boolean jogadorPossuiMovimentoLegal(Cor corJogador) {
        if (!getSequenciasCapturaObrigatorias(corJogador).isEmpty()) {
            return true;
        }
        for (int linha = 0; linha < 8; linha++) {
            for (int coluna = 0; coluna < 8; coluna++) {
                Peca p = casas[linha][coluna];
                if (p != null && p.getCor() == corJogador) {
                    if (!p.getMovimentosSimples(this, new Posicao(linha, coluna)).isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
