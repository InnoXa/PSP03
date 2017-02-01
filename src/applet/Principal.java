package applet;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class Principal extends Applet implements Runnable {

    Thread hilo;
    Fondo fondo;

    Bola pokeball;
    double rotacion;
    
    Lanzador jugador1;
    Lanzador jugador2;
    BufferedImage cesta;
    
    ArrayList<BufferedImage> imagenesJ1 = new ArrayList<>();
    ArrayList<BufferedImage> imagenesJ2 = new ArrayList<>();

    //PARA DIBUJAR LAS IMAGENES EN ESTE OBJETO EN LUGAR DE EN LA PANTALLA DE GRAPHICS
    Graphics2D g2d;

    //IMAGEN QUE CONTENDRÁ TODO LO QUE SE DIBUJARÁ EN dobleBuffer
    BufferedImage backBuffer;

    final int ALTOVENTAJUEGO = 800;
    final int ANCHOVENTAJUEGO = 1000;
    
    @Override
    public void init() {
        //TAMAÑO DEL APPLET
        this.setSize(ANCHOVENTAJUEGO, ALTOVENTAJUEGO);
        

        try {
            pokeball = new Bola(ImageIO.read(new File("src/Imagenes/poke.png")), 500, 300);
        } catch (IOException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        for(int i = 0; i < 5; i++){
            try {
                imagenesJ1.add(ImageIO.read(new File("src/Imagenes/Jugador1_"+i+".png")));
                imagenesJ2.add(ImageIO.read(new File("src/Imagenes/Jugador2_"+i+".png")));
            } catch (IOException ex) {
                Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        jugador1 = new Lanzador(0, ALTOVENTAJUEGO - imagenesJ1.get(0).getHeight(), imagenesJ1);
        jugador2 = new Lanzador(ANCHOVENTAJUEGO - imagenesJ2.get(0).getWidth(), 
                                ALTOVENTAJUEGO - imagenesJ2.get(0).getHeight(),
                                imagenesJ2);
        
        jugador1.start();
        jugador2.start();
        pokeball.start();

        //IGUALA EL TAMAÑO DE offscreen AL DEL APPLET
        backBuffer = new BufferedImage(ANCHOVENTAJUEGO, ALTOVENTAJUEGO, BufferedImage.TYPE_INT_RGB);

        //COMO SE HA DESCRITO ANTES, HACIENDO ESTO SE GUARDARÁ EN offscreen TODO LO QUE SE ESCRIBA EN dobleBuffer
        g2d = backBuffer.createGraphics();

        fondo = new Fondo();
    }

    @Override
    public void start() {

        hilo = new Thread(this); // crea el hilo
        hilo.start();            // lanza el hilo. Invoca a run()

        fondo.start();
    }

    @Override
    public synchronized void paint(Graphics g) {

        //SE LIMPIA TODO EL BUFFER ANTES DE PINTAR DE NUEVO PARA QUE NO LO HAGA ENCIMA
        g2d.clearRect(0, 0, 1000, 800);

        g2d.drawImage(fondo.getImagenActual(), 0, 0, this);

        /*double locationX = pokeball.getImg().getWidth() / 2;
        double locationY = pokeball.getImg().getHeight() / 2;

        rotacion = Math.toRadians(pokeball.getGrados());

        AffineTransform tx = AffineTransform.getRotateInstance(rotacion, locationX, locationY);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);*/

        g2d.drawImage(jugador1.getImgActual(), jugador1.getX(), jugador1.getY(), this);
        g2d.drawImage(jugador2.getImgActual(), jugador2.getX(), jugador2.getY(), this);
        
        //g2d.fill3DRect(100, 100, 100, 100, false);
        
        g2d.drawImage(/*op.filter(*/pokeball.getImg()/*, null)*/, pokeball.getPosX(), pokeball.getPosY(), this);
        g.drawImage(backBuffer, 0, 0, this);
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    public void run() {
        Thread hiloActual = Thread.currentThread(); //Devuelve el hilo que está actualmente en ejecución.
        while (hilo == hiloActual) {

            //imgActual = mario_img[0];
            repaint();  // Llamada a los métodos update y paint(Graphics).

        }
    }
}
