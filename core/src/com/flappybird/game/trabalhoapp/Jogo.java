package com.flappybird.game.trabalhoapp;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Random;

public class Jogo extends ApplicationAdapter {

    //Cria variavel pontos e gravidade
    private int pontos = 0;
    private int gravidade = 0;
    private int estadoDoJogo = 0;

    //Construção dos assets e array da animação do passaro

    private Texture[] passaros;
    private Texture background;
    private Texture canoTopo;
    private Texture canoBaixo;
    private Texture gameOver;

    //Atribuindo variaveis para os assets e defini os parametros da tela do movel
    private float larguraDispositivo;
    private float alturaDispositivo;
    private float variacao = 0;
    private float posicaoInicialVerticalPassaro = 0;
    private float posicaoCanoHorizontal;
    private float posicaoCanoVertical;
    private float espacoEntreCanos;

    private SpriteBatch batch;

    //Puxa a biblioteca bitmap para fazer o placar de score
    BitmapFont textoPontuacao;
    BitmapFont textoReiniciar;
    BitmapFont textoMelhorPontuacao;


    //verifica se o passaro passou do cano ou colidiu com ele
    private boolean passouCano = false;

    private Random random;

    //Cria as variaveis dos coliders ouxando da biblioteca GDX
    private ShapeRenderer shapeRenderer;
    private Circle circuloPassaro;
    private Rectangle retanguloCanoCima;
    private Rectangle retanguloCanoBaixo;

    @Override
    public void create()
    {
        inicializarTexturas();
        inicializarObjetos();
    }

    @Override
    public void render()
    {
        verificarEstadoJogo();
        validarPontos();
        desenharTexturas();
        detectarColisao();
    }

    private void inicializarTexturas() {
        //Junta as 3 imagens do passaro para fazer a animação quadro a quadro.
        passaros = new Texture[3];
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");

        //Trazendo os assets para dentro do jogo.
        background = new Texture("fundo.png");
        canoBaixo = new Texture("cano_baixo_maior.png");
        canoTopo = new Texture("cano_topo_maior.png");
        gameOver = new Texture("game_over.png");
    }

    private void inicializarObjetos() {

        batch = new SpriteBatch();
        random = new Random();

        //Forçar a escala do background para qualquer aparelho movel
        larguraDispositivo = Gdx.graphics.getWidth();
        alturaDispositivo = Gdx.graphics.getHeight();

        //Seta a posição inical do passaro no meio da tela
        posicaoInicialVerticalPassaro = alturaDispositivo / 2;
        posicaoCanoHorizontal = larguraDispositivo;
        espacoEntreCanos = 350;

        //Seta as propriedades do placar de pontos
        textoPontuacao = new BitmapFont();
        textoPontuacao.setColor(Color.WHITE);
        textoPontuacao.getData().setScale(10);

        //Seta as propriedades do texto informativo de Score
        textoMelhorPontuacao = new BitmapFont();
        textoMelhorPontuacao.setColor(Color.GREEN);
        textoMelhorPontuacao.getData().setScale(2);

        //Seta as propriedades do texto informatico de como reiniciar o jogo
        textoReiniciar = new BitmapFont();
        textoReiniciar.setColor(Color.RED);
        textoReiniciar.getData().setScale(2);

        //Cria
        shapeRenderer = new ShapeRenderer();
        circuloPassaro = new Circle();
        retanguloCanoCima = new Rectangle();
        retanguloCanoBaixo = new Rectangle();

    }

    private void verificarEstadoJogo() {
        //Faz com que o jogo responda com o toque na tela fazendo com que o passaro faça seus pulos.
        boolean touchScream = Gdx.input.justTouched();

        if(estadoDoJogo == 0)
        {
            if (Gdx.input.justTouched())
            {
                gravidade = -25;
                estadoDoJogo = 1;
            }
        }
        else if (estadoDoJogo == 1)
        {
            if (Gdx.input.justTouched())
            {
                gravidade = -25;
            }

            //Verifica a posição dos canos na tela e faz com que os canos se movam em direção ao passaro
            posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;
            if(posicaoCanoHorizontal < -canoTopo.getWidth()) {
                posicaoCanoHorizontal = larguraDispositivo;
                posicaoCanoVertical = random.nextInt(400) - 200;
                passouCano = false;
            }

            //Faz com que o passaro responda com o touch da tela
            if (posicaoInicialVerticalPassaro > 0 || touchScream)
                posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;

            gravidade++;

        }
        else if (estadoDoJogo == 2)
        {

        }
    }

    private void validarPontos() {
        //Termina a verificação se o passaro passou o cano para acionar o Score.
        if (posicaoCanoHorizontal < 50 - passaros[0].getWidth()) {
            if (!passouCano)
            {
                pontos++;
                passouCano = true;
            }
        }
        variacao += Gdx.graphics.getDeltaTime() * 10;
        if (variacao > 3)
            variacao = 0;
    }

    private void desenharTexturas() {
        //Posiciona as png's na tela do aparelho móvel
        batch.begin();
        batch.draw(background, 0, 0, larguraDispositivo, alturaDispositivo);
        batch.draw(passaros[(int) variacao], 50, posicaoInicialVerticalPassaro);
        batch.draw(canoBaixo, posicaoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical);
        batch.draw(canoTopo, posicaoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVertical);

        //Desenha o placar de pontos na tela
        textoPontuacao.draw(batch, String.valueOf(pontos), larguraDispositivo / 2, alturaDispositivo - 100);

        //Estado do jogo depois que o passaro colidi no cano, desenha o Game Over, Seu melhor score e informa como reiniciar o jogo na tela
        if(estadoDoJogo == 2)
        {
            batch.draw(gameOver, larguraDispositivo / 2 - gameOver.getWidth() / 2, alturaDispositivo / 2);
            textoReiniciar.draw(batch, "TOQUE NA TELA PARA REINICIAR!!!",
                    larguraDispositivo / 2 -250, alturaDispositivo /2  - gameOver.getHeight() / 2);
            textoMelhorPontuacao.draw(batch, "Sua melhor pontuação é: 0 PONTOS",
                    larguraDispositivo / 2 -250, alturaDispositivo /2 - gameOver.getHeight() * 2);
        }


        batch.end();
    }

    private void detectarColisao() {
        //Cria os parametros do colider para os determinados assets do jogo (Passaro e os canos)
    	circuloPassaro.set(50 + passaros[0].getWidth() / 2,
                posicaoInicialVerticalPassaro + passaros[0].getHeight() / 2, passaros[0].getWidth() / 2);

        retanguloCanoCima.set(posicaoCanoHorizontal,
                alturaDispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVertical, canoTopo.getWidth(), canoTopo.getHeight());

        retanguloCanoBaixo.set(posicaoCanoHorizontal,
                alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical, canoBaixo.getWidth(), canoBaixo.getHeight());

        boolean colisaoCanoCima = Intersector.overlaps(circuloPassaro, retanguloCanoCima);
        boolean colisaoCanoBaixo = Intersector.overlaps(circuloPassaro, retanguloCanoBaixo);

        //Verifica se ocorreu ou não a colisão
        if (colisaoCanoBaixo || colisaoCanoCima) {
            Gdx.app.log("Log", "Colidiu");
            estadoDoJogo = 2;
        }
    }

    @Override
    public void dispose()
    {

    }
}
