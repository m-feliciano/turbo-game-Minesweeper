package br.com.game.junit.model;

@FunctionalInterface
public interface CampoObservador {

	public void eventOcorreu(Campo e, CampoEvento evento);

}
