package applet;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Lanzador extends Thread implements Runnable{
    int x, y, imgActual;
    ArrayList<BufferedImage> imagenes;
    boolean activo;
    private volatile Thread hilo;

    public Lanzador(int x, int y, ArrayList<BufferedImage> imagenes) {
        this.x = x;
        this.y = y;
        imgActual = 0;
        this.imagenes = imagenes;
        activo = false;
        
        hilo = new Thread(this);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    public int getIndiceImg() {
        return imgActual;
    }
    
    public BufferedImage getImgActual() {
        return imagenes.get(imgActual);
    }

    public ArrayList<BufferedImage> getImagenes() {
        return imagenes;
    }
    
    public void activar(){
        activo = true;
    }
    
    public void reiniciarIndice(){
        imgActual = 0;
    }
    
    public void avanzar(){
        switch(imgActual){
            case 0:
                imgActual = 1;
                break;
            case 1:
                imgActual = 2;
                break;
            case 2:
                imgActual = 3;
                break;
            case 3:
                imgActual = 4;
                break;
            case 4:
                imgActual = 0;
                activo = false;
        }
    }
    
    @Override
    public void run() {
        Thread hiloActual = Thread.currentThread();
        hilo = hiloActual;

        while (hilo == hiloActual) {
            if(activo){
                avanzar();
            }
            pausa(150);
        }
    }

    void pausa(int tiempo) {
        try {
            hilo.sleep(tiempo);     //Envia el hilo a dormir
        } catch (InterruptedException e) {
            System.err.println(e);
        }
    }
}
