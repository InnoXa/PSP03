package applet;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
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
import javax.swing.JOptionPane;
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

    //Niveles del juego (Del 1 al 3)
    static int nivel = 1;

    boolean GANADO = false;
    boolean PERDIDO = false;
    
    static int maximoBolas = 10;

    public BufferedImage getCesta() {
        return cesta;
    }

    public int getPosXCesta() {
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

        posXCesta = (ANCHOVENTAJUEGO-200) - cesta.getWidth() / 2;

        for (int i = 0; i < 5; i++) {
            try {
                imagenesJ1.add(ImageIO.read(new File("src/Imagenes/Jugador1_" + i + ".png")));
                imagenesJ2.add(ImageIO.read(new File("src/Imagenes/Jugador2_" + i + ".png")));
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
            public void mouseMoved(MouseEvent e) {
                // Para no sobrepasar el jugador 1
                if (e.getX() < jugador1.getImgActual().getWidth() + cesta.getWidth() / 2) {
                    posXCesta = jugador1.getImgActual().getWidth();
                } 
                // Para no sobrepasar el jugador 2
                else if (e.getX() + cesta.getWidth() / 2 > ANCHOVENTAJUEGO - jugador2.getImgActual().getWidth()) {
                    posXCesta = ANCHOVENTAJUEGO - jugador2.getImgActual().getWidth() - cesta.getWidth();
                } 
                // Si no se sobrepasa ningun jugador colocamos la cesta
                else {
                    posXCesta = e.getX() - cesta.getWidth() / 2;
                }
            }
        });
        
        this.addKeyListener(new java.awt.event.KeyListener() {
            @Override
            public void keyPressed(KeyEvent t) {
                int key = t.getKeyCode();

                if (key == KeyEvent.VK_LEFT) {
                    if (posXCesta < jugador1.getImgActual().getWidth() + cesta.getWidth() / 2) {
                        posXCesta = jugador1.getImgActual().getWidth();
                    }else{
                        posXCesta -= 20;
                    }
                }else if (key == KeyEvent.VK_RIGHT) {
                    if (posXCesta < jugador1.getImgActual().getWidth() + cesta.getWidth() / 2) {
                        posXCesta = ANCHOVENTAJUEGO - jugador2.getImgActual().getWidth() - cesta.getWidth();
                    }else{
                        posXCesta += 20;
                    }
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
                
            }

            @Override
            public void keyReleased(KeyEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        if (!GANADO && !PERDIDO) {
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

                comprobarEncesta(b);

                reboteLatIzqCesta(b);

                reboteLatDerCesta(b);

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
            g2d.drawString("Puntuación: " + puntuacion, 60, 65);
            g2d.drawString("Vidas: " + vidas, 60, 95);
            
            
            g2d.setColor(Color.RED);
            g2d.fillRect(ANCHOVENTAJUEGO - 300, 35, 220, 75);
            g2d.setColor(Color.BLACK);
            g2d.fillRect(ANCHOVENTAJUEGO - 295, 40, 210, 65);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Monaco", StyleConstants.ALIGN_CENTER | Font.BOLD, 40));
            g2d.drawString("NIVEL: " + nivel, ANCHOVENTAJUEGO - 270, 85);

            g.drawImage(backBuffer, 0, 0, this);
            FPS++;
        }

    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    public void run() {
        final int MS_POR_SEGUNDO = 1000;
        final byte FPS_OBJETIVO = 50;
        final int MS_POR_ACTUALIZACION = MS_POR_SEGUNDO / FPS_OBJETIVO;
        long referenciaContador = System.currentTimeMillis();
        Thread hiloActual = Thread.currentThread(); //Devuelve el hilo que está actualmente en ejecución.

        while (hilo == hiloActual) {

            repaint();  // Llamada a los métodos update y paint(Graphics).
            pausa(MS_POR_ACTUALIZACION);

            jugadorElegido = generarAleatorio(2, 1);

            //FRECUENCIA DE LANZAMIENTO DE LAS BOLAS
            comienzo = generarAleatorio(10, 0);

            if (jugadorElegido == 1 && comienzo == 5) {
                jugador1.activar();
            } else if (jugadorElegido == 2 && comienzo == 5) {
                jugador2.activar();
            }

            if (System.currentTimeMillis() - referenciaContador > MS_POR_SEGUNDO) {
                showStatus("FPS: " + FPS);
                FPS = 0;
                referenciaContador = System.currentTimeMillis();
            }

            if (vidas <= 0) {
                PERDIDO = true;
                parar();
            }
        }

        if (PERDIDO) {
            JOptionPane.showMessageDialog(this, "¡HAS PERDIDO!");
        } else if (GANADO) {
            JOptionPane.showMessageDialog(this, "¡HAS GANADO!");
        }
    }

    public void parar() {
        hilo = null;
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
        if (ListaBolas.bolas.size() <= maximoBolas) {
            int random = generarAleatorio(10, 1);

            String tipo;
            Bola b;

            switch (random) {
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

            switch (codigo) {
                case 1:
                    switch(tipo){
                        case "poke":
                            b = new Bola(codigo, tipo, 50, 500, generarAleatorio(29, 15), /*generarAleatorio(40, 20)*/ 45);
                            break;
                        case "super":
                            b = new Bola(codigo, tipo, 50, 500, generarAleatorio(29, 20), /*generarAleatorio(40, 20)*/ 45);
                            break;
                        case "ultra":
                            b = new Bola(codigo, tipo, 50, 500, generarAleatorio(29, 25), /*generarAleatorio(40, 20)*/ 45);
                            break;
                        default:
                            b = new Bola(codigo, tipo, 50, 500, 30, /*generarAleatorio(40, 20)*/ 45);
                            break;
                    }
                    break;
                default:
                    switch(tipo){
                        case "poke":
                            b = new Bola(codigo, tipo, ANCHOVENTAJUEGO - 50, 500, generarAleatorio(29, 15), /*generarAleatorio(40, 20)*/ 45);
                            break;
                        case "super":
                            b = new Bola(codigo, tipo, ANCHOVENTAJUEGO - 50, 500, generarAleatorio(29, 20), /*generarAleatorio(40, 20)*/ 45);
                            break;
                        case "ultra":
                            b = new Bola(codigo, tipo, ANCHOVENTAJUEGO - 50, 500, generarAleatorio(29, 25), /*generarAleatorio(40, 20)*/ 45);
                            break;
                        default:
                            b = new Bola(codigo, tipo, ANCHOVENTAJUEGO - 50, 500, 30, /*generarAleatorio(40, 20)*/ 45);
                            break;
                    }
                    break;
                                }
            ListaBolas.bolas.add(b);
            b.start();
        }
    }

    public synchronized void comprobarEncesta(Bola b) {
        if (new Rectangle(b.getPosX(), b.getPosY(), b.getImg().getWidth(), 10).intersects(
                new Rectangle(posXCesta + 50, ALTOVENTAJUEGO - (cesta.getHeight() + 10), cesta.getWidth() - 100, 10))) {
            b.parar();
            
            switch(b.getTipo()){
                case "poke":
                    puntuacion++;
                    break;
                case "super":
                    puntuacion += 2;
                    break;
                case "ultra":
                    puntuacion += 3;
                    break;
                default:
                    puntuacion += 4;
            }

            if (puntuacion > 20 && nivel == 1) {
                maximoBolas *=2;
                nivel++;
            } else if (puntuacion > 40 && nivel == 2) {
                maximoBolas *=2;
                nivel++;
            } else if (puntuacion > 60 && nivel == 3) {
                GANADO = true;
                parar();
            }
        }
    }

    public synchronized void reboteLatIzqCesta(Bola b) {
        if (new Rectangle(b.getPosX(), b.getPosY(), b.getImg().getWidth(), 10).intersects(
                new Rectangle(posXCesta, ALTOVENTAJUEGO - cesta.getHeight(), 10, cesta.getHeight()))) {
            b.invertirVelX();
        }
    }

    public synchronized void reboteLatDerCesta(Bola b) {
        if (new Rectangle(b.getPosX(), b.getPosY(), b.getImg().getWidth(), 10).intersects(
                new Rectangle(posXCesta + (cesta.getWidth() - 10), ALTOVENTAJUEGO - cesta.getHeight(), 10, cesta.getHeight()))) {
            b.invertirVelX();
        }
    }
}
