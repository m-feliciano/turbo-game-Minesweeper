package br.com.game.junit.model;

import java.util.ArrayList;
import java.util.List;

public class Campo {

	private final int linha;
	private final int coluna;
	private boolean aberto = false;
	private boolean minado = false;
	private boolean marcado = false;

	private List<Campo> vizinhos = new ArrayList<>();
	private List<CampoObservador> observadores = new ArrayList<>();

	public Campo(int row, int column) {
		this.linha = row;
		this.coluna = column;
	}

	public void registrarObservador(CampoObservador observador) {
		observadores.add(observador);
	}

	private void notificarObservadores(CampoEvento evento) {
		observadores.stream().forEach(o -> o.eventOcorreu(this, evento));
	}

	boolean addVizinho(Campo vizinho) {
		boolean outraLinha = linha != vizinho.linha;
		boolean outraColuna = coluna != vizinho.coluna;
		boolean diagonal = outraColuna && outraLinha;

		int deltaLinha = Math.abs(linha - vizinho.linha);
		int deltaColuna = Math.abs(coluna - vizinho.coluna);
		int delta = deltaColuna + deltaLinha;

		if (delta == 1 && !diagonal) {
			vizinhos.add(vizinho);
			return true;
		} else if (delta == 2 && diagonal) {
			vizinhos.add(vizinho);
			return true;
		} else {
			return false;
		}
	}

	public void alterarMarcacao() {
		if (!aberto) {
			marcado = !marcado;
		}
		if (marcado) {
			notificarObservadores(CampoEvento.MARCAR);
		} else {
			notificarObservadores(CampoEvento.DESMARCAR);
		}
	}

	public boolean abrir() {
		if (!aberto && !marcado) {

			if (minado) {
				notificarObservadores(CampoEvento.EXPLODIR);
				return true;
			}

			setAberto(true);

			if (vizinhacaSegura()) {
				vizinhos.forEach(v -> v.abrir());
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean vizinhacaSegura() {
		return vizinhos.stream().noneMatch(v -> v.minado);
	}

	void minar() {
		minado = true;
	}

	public boolean isMinado() {
		return minado;
	}

	public boolean isMarcado() {
		return marcado;
	}

	public boolean isAberto() {
		return aberto;
	}

	public boolean isFechado() {
		return !isAberto();
	}

	void setAberto(boolean aberto) {
		this.aberto = aberto;
		if (aberto) {
			notificarObservadores(CampoEvento.ABRIR);
		}
	}

	void setMinado(boolean minado) {
		this.minado = minado;
	}

	void setMarcado(boolean marcado) {
		this.marcado = marcado;
	}

	boolean objetivoAcancado() {
		boolean visivel = !minado && aberto;
		boolean protegido = minado && marcado;
		return visivel || protegido;
	}

	public int minasAoRedor() {
		return (int) vizinhos.stream().filter(x -> x.minado).count();
	}

	void reiniciar() {
		aberto = false;
		marcado = false;
		minado = false;
		notificarObservadores(CampoEvento.REINICIAR);
	}

	public int getLinha() {
		return linha;
	}

	public int getColuna() {
		return coluna;
	}

}