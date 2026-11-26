package persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jogo.Partida;
import jogo.StatusPartida;
import jogo.Tabuleiro;
import modelo.Cor;
import modelo.Dama;
import modelo.Jogador;
import modelo.Peca;
import modelo.PecaComum;
import modelo.Posicao;

public class PersistenciaArquivo implements PersistenciaPartida {

	@Override
	public void salvar(Partida partida, String caminhoArquivo) throws PersistenciaException {
		if (partida == null) {
			throw new PersistenciaException("Nenhuma partida ativa para salvar.");
		}
		if (partida.isEmCapturaMultipla()) {
			throw new PersistenciaException("Não é permitido salvar durante uma captura múltipla.");
		}

		File arquivo = new File(caminhoArquivo);
		if (arquivo.getParentFile() != null) {
			arquivo.getParentFile().mkdirs();
		}

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {
			writer.write("BRANCAS=" + partida.getJogadorBrancas().getNome());
			writer.newLine();
			writer.write("PRETAS=" + partida.getJogadorPretas().getNome());
			writer.newLine();
			writer.write("TURNO=" + partida.getTurno().getCor());
			writer.newLine();
			writer.write("STATUS=" + partida.getStatus().name());
			writer.newLine();
			writer.write(partida.getTabuleiro().gerarRepresentacaoTextual());
			writer.newLine();
		} catch (IOException e) {
			throw new PersistenciaException("Falha ao escrever o arquivo: " + e.getMessage(), e);
		}
	}

	@Override
	public Partida carregar(String caminhoArquivo) throws PersistenciaException {
		File arquivo = new File(caminhoArquivo);
		if (!arquivo.exists() || !arquivo.isFile()) {
			throw new PersistenciaException("Arquivo não encontrado: " + caminhoArquivo);
		}

		List<String> linhas = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
			String linha;
			while ((linha = reader.readLine()) != null) {
				if (!linha.trim().isEmpty()) {
					linhas.add(linha.trim());
				}
			}
		} catch (IOException e) {
			throw new PersistenciaException("Falha ao ler o arquivo: " + e.getMessage(), e);
		}

		if (linhas.size() != 14) {
			throw new PersistenciaException("Arquivo vazio ou com formato inválido.");
		}

		if (!linhas.get(0).startsWith("BRANCAS=") || !linhas.get(1).startsWith("PRETAS=")
				|| !linhas.get(2).startsWith("TURNO=") || !linhas.get(3).startsWith("STATUS=")
				|| !linhas.get(4).equals("TABULEIRO_INICIO") || !linhas.get(13).equals("TABULEIRO_FIM")) {
			throw new PersistenciaException("Cabeçalho ou marcadores do tabuleiro inválidos.");
		}

		String nomeBrancas = linhas.get(0).substring("BRANCAS=".length()).trim();
		String nomePretas = linhas.get(1).substring("PRETAS=".length()).trim();
		if (nomeBrancas.isEmpty() || nomePretas.isEmpty()) {
			throw new PersistenciaException("Nomes dos jogadores inválidos no arquivo.");
		}

		Cor turno = parseTurno(linhas.get(2).substring("TURNO=".length()).trim());
		StatusPartida status = parseStatus(linhas.get(3).substring("STATUS=".length()).trim());

		Tabuleiro tabuleiro = new Tabuleiro();
		for (int i = 0; i < 8; i++) {
			String linhaTab = linhas.get(5 + i);
			if (linhaTab.length() != 8) {
				throw new PersistenciaException("Linha " + (8 - i) + " do tabuleiro deve ter 8 caracteres.");
			}
			int linhaMatriz = 7 - i;
			for (int col = 0; col < 8; col++) {
				char simbolo = linhaTab.charAt(col);
				Posicao pos = new Posicao(linhaMatriz, col);
				Peca peca = criarPecaPorSimbolo(simbolo);
				if (peca != null && pos.isClara()) {
					throw new PersistenciaException("Peça posicionada em casa clara inválida: " + pos);
				}
				tabuleiro.colocarPeca(peca, pos);
			}
		}

		if (tabuleiro.contarPecas(Cor.BRANCA) == 0 && tabuleiro.contarPecas(Cor.PRETA) == 0) {
			throw new PersistenciaException("Tabuleiro inválido: nenhuma peça encontrada.");
		}

		Jogador jBrancas = new Jogador(nomeBrancas, Cor.BRANCA);
		Jogador jPretas = new Jogador(nomePretas, Cor.PRETA);
		return new Partida(jBrancas, jPretas, status, turno, tabuleiro);
	}

	private Cor parseTurno(String valor) throws PersistenciaException {
		if ("BRANCAS".equalsIgnoreCase(valor)) return Cor.BRANCA;
		if ("PRETAS".equalsIgnoreCase(valor)) return Cor.PRETA;
		throw new PersistenciaException("Turno inválido no arquivo: " + valor);
	}

	private StatusPartida parseStatus(String valor) throws PersistenciaException {
		try {
			return StatusPartida.valueOf(valor.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new PersistenciaException("Status inválido no arquivo: " + valor);
		}
	}

	private Peca criarPecaPorSimbolo(char simbolo) throws PersistenciaException {
		switch (simbolo) {
			case '.': return null;
			case 'b': return new PecaComum(Cor.BRANCA);
			case 'B': return new Dama(Cor.BRANCA);
			case 'p': return new PecaComum(Cor.PRETA);
			case 'P': return new Dama(Cor.PRETA);
			default: throw new PersistenciaException("Símbolo desconhecido no tabuleiro: " + simbolo);
		}
	}
}