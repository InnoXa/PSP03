package applet;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

public class Bola extends Thread{
    private BufferedImage imagen;
    private int posX;
    private int posY;
    private int grados;

    private volatile Thread hilo;

    public Bola(BufferedImage imagen, int posX, int posY) {
        this.imagen = imagen;
        this.posX = posX;
        this.posY = posY;
        grados = 0;

        hilo = new Thread(this);
    }

    public BufferedImage getImg() {
        return imagen;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getGrados() {
        return grados;
    }

    /*public void paint(Graphics g, Main m){
        
     }*/
    @Override
    public void run() {
        Thread hiloActual = Thread.currentThread();
        hilo = hiloActual;

        while (hilo == hiloActual) {
            pausa(150);

            grados += 40;
        }
    }

    void pausa(int tiempo) {
        try {
            hilo.sleep(tiempo);     //Envia el hilo a dormir
        } catch (InterruptedException e) {
            System.err.println(e);
        }
    }

    /*public void parar() {

        hilo = null;

        Iterator<Fruta> itf = frutas.iterator();

        while (itf.hasNext()) {
            Fruta fruta = itf.next();

            if (fruta == this) {
                itf.remove();
                break;
            }
        }
    }*/
}
