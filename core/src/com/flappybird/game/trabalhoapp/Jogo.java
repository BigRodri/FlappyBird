package com.flappybird.game.trabalhoapp;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class Jogo extends ApplicationAdapter {
    //Construção dos assets
	private SpriteBatch batch;
	private Texture[] passaros;
	private Texture background;
	private Texture canoTopo;
	private Texture canoBaixo;


	//Movimentação

	private int movimentaX = 0;

	//Atribuindo variaveis para os assets
	private float larguraDispositivo;
	private float alturaDispositivo;
	private float variacao = 0;
	private float gravidade = 0;
	private float posicaoInicialVerticalPassaro = 0;
	private float posicaoCanoHorizontal;
	private float espacoEntreCanos;


	@Override
	public void create () {
		inicializarTexturas();
		inicializarObjetos();
	}

	private void inicializarObjetos() {

		batch = new SpriteBatch();
		//Forçar a escala do background para qualquer aparelho movel
		larguraDispositivo = Gdx.graphics.getWidth();
		alturaDispositivo = Gdx.graphics.getHeight();
		//Seta a posição inical do passaro no meio da tela
		posicaoInicialVerticalPassaro = alturaDispositivo / 2;
		posicaoCanoHorizontal = larguraDispositivo;
		espacoEntreCanos = 150;

	}

	private void inicializarTexturas() {
        //Junta as 3 imagens do passaro para fazer a animação quadro a quadro
		passaros = new Texture[3];
		passaros [0] = new Texture("passaro1.png");
		passaros [1] = new Texture("passaro2.png");
		passaros [2] = new Texture("passaro3.png");

		//Trazendo os assets para dentro do jogo
		background = new Texture("fundo.png");
		canoBaixo = new Texture("cano_baixo_maior.png");
		canoTopo = new Texture("cano_topo_maior.png");

	}

	@Override
	public void render () {

		verificarEstadoJogo();
		desenharTexturas();
		MovimentaCano();

	}

	private void verificarEstadoJogo() {
        //Faz com que o jogo responda com o toque na tela fazendo assim com que o passaro faça seus pulos
		boolean touchScream = Gdx.input.justTouched();
		if (Gdx.input.justTouched())
		{
			gravidade = -25;
		}
		if (posicaoInicialVerticalPassaro > 0 || touchScream)
			posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;
        //É a parte que faz acontecer a variação das imagens do passaro
		variacao += Gdx.graphics.getDeltaTime() * 10;
		if (variacao > 3)
			variacao = 0;

		gravidade ++;

	}

	private void desenharTexturas() {
		//Posiciona as png's na tela do aparelho móvel com as posições ja setadas
		batch.begin();
		batch.draw(background, 0, 0, larguraDispositivo, alturaDispositivo);
		batch.draw(passaros [(int) variacao], 30, posicaoInicialVerticalPassaro);

		batch.draw(canoBaixo, posicaoCanoHorizontal - 100, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2);
		batch.draw(canoTopo, posicaoCanoHorizontal - 100, alturaDispositivo / 2 + espacoEntreCanos);

		movimentaX++;


		batch.end();
	}

	private void MovimentaCano() {
		//Faz com que os canos se movimentemem direção ao passaro, dando a entender que quem se movimenta é o passaro
		if(posicaoCanoHorizontal <-canoTopo.getWidth()){
			posicaoCanoHorizontal = larguraDispositivo;
		}
		posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime()*200;
	}

	@Override
	public void dispose () {

	}
}
