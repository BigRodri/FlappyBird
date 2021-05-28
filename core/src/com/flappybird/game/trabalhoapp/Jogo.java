package com.flappybird.game.trabalhoapp;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
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
    private int pontuacaoMaxima = 0;
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
    private float posicaoHorizontalPassaro;
    private float posicaoInicialVerticalPassaro = 0;
    private float posicaoCanoHorizontal;
    private float posicaoCanoVertical;
    private float espacoEntreCanos;

    private SpriteBatch batch;

    //Puxa a biblioteca bitmap para fazer o placar de score
    BitmapFont textoPontuacao;
    BitmapFont textoReiniciar;
    BitmapFont textoMelhorPontuacao;

    Sound somVoando;
    Sound somColisao;
    Sound somPontuacao;


    //verifica se o passaro passou do cano ou colidiu com ele
    private boolean passouCano = false;

    private Random random;

    //Cria as variaveis dos coliders ouxando da biblioteca GDX
    private ShapeRenderer shapeRenderer;
    private Circle circuloPassaro;
    private Rectangle retanguloCanoCima;
    private Rectangle retanguloCanoBaixo;

    Preferences preferencias;

    @Override
    //Cria as texturas e os objetos para o renderes
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

        batch = new SpriteBatch();                                                                   //cria o objeto
        random = new Random();                                                                       //cria o objeto random

        //Forçar a escala do background para qualquer aparelho movel
        larguraDispositivo = Gdx.graphics.getWidth();                                                //construção grafica do cano
        alturaDispositivo = Gdx.graphics.getHeight();                                                //construção grafica do cano

        //Seta a posição inical do passaro no meio da tela
        posicaoInicialVerticalPassaro = alturaDispositivo / 2;                                       //Posição que o passaro fica antes do jogo começar
        posicaoCanoHorizontal = larguraDispositivo;                                                  //posição inicial dos canos
        espacoEntreCanos = 300;                                                                      //espaço entre os canos

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

        //Inicializa o collider dos assets do jogo
        shapeRenderer = new ShapeRenderer();
        circuloPassaro = new Circle();
        retanguloCanoCima = new Rectangle();
        retanguloCanoBaixo = new Rectangle();

        //Usa a biblioteca gdx para puxar os audios do jogo
        somColisao = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));                 //puxa o arvquivo de som da colisão
        somPontuacao = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));               //puxa o arvquivo de som dos pontos
        somVoando = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));                     //puxa o arvquivo de som das asas

        preferencias = Gdx.app.getPreferences("flappybird");
        pontuacaoMaxima = preferencias.getInteger("pontuacaoMaxima", 0);

    }

    private void verificarEstadoJogo() {
        //Faz com que o jogo responda com o toque na tela fazendo com que o passaro faça seus pulos.
        boolean touchScream = Gdx.input.justTouched();

        if(estadoDoJogo == 0)
        {
            if (Gdx.input.justTouched())                                                             //Condição que verifica o toque da tela para realizar a animação
            {
                gravidade = -18;                                                                     //Atribui o valor da gravidade em relação ao pulo
                estadoDoJogo = 1;                                                                    //Verifica que o jogo se encontra no estado 1
                somVoando.play();                                                                    //Toca o SFX do passaro voando
            }
        }
        else if (estadoDoJogo == 1)
        {
            if (Gdx.input.justTouched())                                                             //Condição que verifica o toque da tela para realizar a animação
            {
                gravidade = -18;                                                                     //Atribui o valor da gravidade em relação ao pulo
                somVoando.play();                                                                    //Toca o SFX do passaro voando
            }

            //Verifica a posição dos canos na tela e faz com que os canos se movam em direção ao passaro
            posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;
            if(posicaoCanoHorizontal < -canoTopo.getWidth()) {                                       //Posição horizontal dos canos
                posicaoCanoHorizontal = larguraDispositivo;                                          //Defini o tamanho do cano de acordo com o dispositivo
                posicaoCanoVertical = random.nextInt(400) - 200;                                  //velocidade de movimentação dos canos
                passouCano = false;                                                                  //Verifica a colisao no cano
            }

            //Faz com que o passaro responda com o touch da tela
            if (posicaoInicialVerticalPassaro > 0 || touchScream)
                posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;           //faz acontecer o pulo do passaro

            gravidade++;                                                                             //atribui a gravidade no passaro

        }
        else if (estadoDoJogo == 2)
        {
            if(pontos>pontuacaoMaxima)
            {
                pontuacaoMaxima = pontos;                                                            //Seta seu melhor score no jogo
                preferencias.putInteger("pontuacaoMaxima", pontuacaoMaxima);
            }
            posicaoHorizontalPassaro -= Gdx.graphics.getDeltaTime() * 500;                           //animação do passaro caindo apos a colisao


            if(touchScream)
            { //Faz com que o jogo reinicie com os padrões iniciais.
                estadoDoJogo = 0;
                pontos = 0;
                gravidade = 0;
                posicaoHorizontalPassaro = 0;
                posicaoInicialVerticalPassaro = alturaDispositivo / 2;
                posicaoCanoHorizontal = larguraDispositivo;

            }
        }
    }

    private void validarPontos() {
        //Termina a verificação se o passaro passou o cano para acionar o Score.
        if (posicaoCanoHorizontal < 50 - passaros[0].getWidth()) {
            if (!passouCano)
            {
                pontos++;                                                                            //atribui pontos
                passouCano = true;                                                                   //Confirma que passou pelo cano
                somPontuacao.play();                                                                 //Toca o SFX de pontuação
            }
        }
        //Animação do passáro
        variacao += Gdx.graphics.getDeltaTime() * 10;
        if (variacao > 3)                                                                            //Deixa a animação acontecendo para que o -
            variacao = 0;                                                                            // - jogador não pense que o jogo está travado
    }

    private void desenharTexturas() {
        //Posiciona as png's na tela do aparelho móvel
        batch.begin();
        batch.draw(background, 0, 0, larguraDispositivo, alturaDispositivo);
        batch.draw(passaros[(int) variacao], 50 + posicaoHorizontalPassaro, posicaoInicialVerticalPassaro);
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
            textoMelhorPontuacao.draw(batch, "SUA MELHOR PONTUAÇÃO É: " + pontuacaoMaxima + " PONTOS",
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

        boolean colisaoCanoCima = Intersector.overlaps(circuloPassaro, retanguloCanoCima);           //Verifica se bateu ou não
        boolean colisaoCanoBaixo = Intersector.overlaps(circuloPassaro, retanguloCanoBaixo);         //Verifica se bateu ou não

        //Caso haja a colisão inicia o estado 2 do jogo onde há a opção de reiniciar
        if (colisaoCanoBaixo || colisaoCanoCima) {
            if(estadoDoJogo ==1)
            {
                somColisao.play();                                                                   //Toca o SFX de colisão do passaro
                estadoDoJogo = 2;                                                                    //Inicia o estado 2 do jogo
            }
        }
    }

    @Override
    public void dispose()
    {

    }
}
