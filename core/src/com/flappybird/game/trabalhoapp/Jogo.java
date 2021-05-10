package com.flappybird.game.trabalhoapp;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class Jogo extends ApplicationAdapter {
    //Construção dos assets
	private SpriteBatch batch;
	private Texture passaro;
	private Texture background;

	//Movimentação
	private int movimentaY = 0;
	private int movimentaX = 0;

	//Atribuindo variaveis para o redimensionar o background
	private float larguraDispositivo;
	private float alturaDispositivo;


	@Override
	public void create () {

		batch = new SpriteBatch();
		passaro = new Texture("passaro1.png");
		background = new Texture("fundo.png");

		larguraDispositivo = Gdx.graphics.getWidth();
		alturaDispositivo = Gdx.graphics.getHeight();

	}

	@Override
	public void render () {

		batch.begin();

		batch.draw(background, 0, 0, larguraDispositivo, alturaDispositivo);
		batch.draw(passaro,50,50, movimentaX, movimentaY);

		movimentaX++;
		movimentaY++;

		batch.end();

	}
	
	@Override
	public void dispose () {

	}
}
