package applet;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.text.StyleConstants;

public class Principal extends Applet implements Runnable {

    Thread hilo;
    Fondo fondo;
    int comienzo, jugadorElegido;

    double rotacion;
    static int puntuacion = 0;
    static int vidas = 10;
    private int FPS = 0;
    
    Lanzador jugador1;
    Lanzador jugador2;
    BufferedImage cesta;
    int posXCesta; 
    
    ArrayList<BufferedImage> imagenesJ1 = new ArrayList<>();
    ArrayList<BufferedImage> imagenesJ2 = new ArrayList<>();

    //PARA DIBUJAR LAS IMAGENES EN ESTE OBJETO EN LUGAR DE EN LA PANTALLA DE GRAPHICS
    Graphics2D g2d;

    //IMAGEN QUE CONTENDRÁ TODO LO QUE SE DIBUJARÁ EN dobleBuffer
    BufferedImage backBuffer;

    //final int ALTOVENTAJUEGO = 800;
    //final int ANCHOVENTAJUEGO = 1000;
    
    static final int ALTOVENTAJUEGO = 600;
    static final int ANCHOVENTAJUEGO = 800;
    
    public BufferedImage getCesta(){
        return cesta;
    }
    
    public int getPosXCesta(){
        return posXCesta;
    }
    
    @Override
    public void init() {
        //TAMAÑO DEL APPLET
        this.setSize(ANCHOVENTAJUEGO, ALTOVENTAJUEGO);
        

        try {
            cesta = ImageIO.read(new File("src/Imagenes/Cesta.png"));
        } catch (IOException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        posXCesta = ANCHOVENTAJUEGO - cesta.getWidth()/2;
        
        
        for(int i = 0; i < 5; i++){
            try {
                imagenesJ1.add(ImageIO.read(new File("src/Imagenes/Jugador1_"+i+".png")));
                imagenesJ2.add(ImageIO.read(new File("src/Imagenes/Jugador2_"+i+".png")));
            } catch (IOException ex) {
                Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        jugador1 = new Lanzador(0, ALTOVENTAJUEGO - imagenesJ1.get(0).getHeight(), 1, imagenesJ1);
        jugador2 = new Lanzador(ANCHOVENTAJUEGO - imagenesJ2.get(0).getWidth(), 
                                ALTOVENTAJUEGO - imagenesJ2.get(0).getHeight(), 2,
                                imagenesJ2);
        
        jugador1.start();
        jugador2.start();

        //IGUALA EL TAMAÑO DE offscreen AL DEL APPLET
        backBuffer = new BufferedImage(ANCHOVENTAJUEGO, ALTOVENTAJUEGO, BufferedImage.TYPE_INT_RGB);

        //COMO SE HA DESCRITO ANTES, HACIENDO ESTO SE GUARDARÁ EN offscreen TODO LO QUE SE ESCRIBA EN dobleBuffer
        g2d = backBuffer.createGraphics();
        
        this.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e){
                if(e.getX() < jugador1.getImgActual().getWidth() + cesta.getWidth()/2){
                    posXCesta = jugador1.getImgActual().getWidth();
                }else if(e.getX() + cesta.getWidth()/2 > ANCHOVENTAJUEGO - jugador2.getImgActual().getWidth()){
                    posXCesta = ANCHOVENTAJUEGO - jugador2.getImgActual().getWidth() - cesta.getWidth();
                }else{  
                    posXCesta = e.getX() - cesta.getWidth()/2;
                }
            }
        });
        
        /*this.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e){
                jugador1.activar();
                jugador2.activar();
            }
        });*/

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

        Bola b;
        Iterator<Bola> itb = ListaBolas.bolas.iterator();
        while (itb.hasNext()) {
            b = itb.next();
            
            double locationX = b.getImg().getWidth() / 2;
            double locationY = b.getImg().getHeight() / 2;

            rotacion = Math.toRadians(b.getGrados());

            AffineTransform tx = AffineTransform.getRotateInstance(rotacion, locationX, locationY);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
            
            if(new Rectangle(b.getPosX(), b.getPosY(), b.getImg().getWidth(), 10).intersects(
            new Rectangle(posXCesta+30, ALTOVENTAJUEGO - cesta.getHeight(), cesta.getWidth()-30, 10))){
                b.parar();
                puntuacion++;
            }
            
            g2d.drawImage(op.filter(b.getImg(), null), b.getPosX(), b.getPosY(), this);
        }

        g2d.drawImage(jugador1.getImgActual(), jugador1.getX(), jugador1.getY(), this);
        g2d.drawImage(jugador2.getImgActual(), jugador2.getX(), jugador2.getY(), this);
        g2d.drawImage(cesta, posXCesta, ALTOVENTAJUEGO - cesta.getHeight(), this);
        
        g2d.setColor(Color.RED);
        g2d.fillRect(45, 35, 220, 75);
        g2d.setColor(Color.BLACK);
        g2d.fillRect(50, 40, 210, 65);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Monaco", StyleConstants.ALIGN_CENTER | Font.BOLD, 26));
        g2d.drawString("Puntuación: "+puntuacion, 60, 65);
        g2d.drawString("Vidas: "+vidas, 60, 95);
        
        g.drawImage(backBuffer, 0, 0, this);
        FPS++;
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    public void run() {
        final int MS_POR_SEGUNDO = 1000;
        final byte FPS_OBJETIVO = 50;
        final int MS_POR_ACTUALIZACION = MS_POR_SEGUNDO/FPS_OBJETIVO;
        long referenciaContador = System.currentTimeMillis();
        Thread hiloActual = Thread.currentThread(); //Devuelve el hilo que está actualmente en ejecución.
        
        while (hilo == hiloActual) {

            repaint();  // Llamada a los métodos update y paint(Graphics).
            pausa(MS_POR_ACTUALIZACION);
            
            jugadorElegido = generarAleatorio(2, 1);
            
            //FRECUENCIA DE LANZAMIENTO DE LAS BOLAS
            comienzo = generarAleatorio(10, 0);
            
            if(jugadorElegido == 1 && comienzo == 5){
                jugador1.activar();
            }else if(jugadorElegido == 2 && comienzo == 5){
                jugador2.activar();
            }
            
            if(System.currentTimeMillis() - referenciaContador > MS_POR_SEGUNDO){
                showStatus("FPS: "+FPS);
                FPS = 0;
                referenciaContador = System.currentTimeMillis();
            }
        }
    }
    
    void pausa(int tiempo) {
        try {
            Thread.sleep(tiempo);     //Envia el hilo a dormir
        } catch (InterruptedException e) {
            System.err.println(e);
        }
    }
    
    public static int generarAleatorio(int max, int min) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public synchronized static void generarBola(int codigo) {
        int random = generarAleatorio(10, 1);
        String tipo;
        Bola b;
        
        switch(random){
            case 1:
            case 2:
            case 3:
            case 4:
                tipo = "poke";
                break;
            case 5:
            case 6:
            case 7:
                tipo = "super";
                break;
            case 8:
            case 9:
                tipo = "ultra";
                break;
            default:
                tipo = "master";
        }
        
        switch(codigo){
            case 1:
                b = new Bola(codigo, tipo, 50, 500, generarAleatorio(30, 15), /*generarAleatorio(40, 20)*/45);
                break;
            default:
                b = new Bola(codigo, tipo, ANCHOVENTAJUEGO-50, 500, generarAleatorio(20, 15), /*generarAleatorio(40, 20)*/45); 
        }
        ListaBolas.bolas.add(b);
        b.start();
    }
}
