/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applet;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author alumno
 */
public class Lanzador extends Thread {
    int x, y, imgActual;
    ArrayList<BufferedImage> imagenes;
    private volatile Thread hilo;

    public Lanzador(int x, int y, ArrayList<BufferedImage> imagenes) {
        this.x = x;
        this.y = y;
        imgActual = 0;
        this.imagenes = imagenes;
        
        hilo = new Thread(this);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    public BufferedImage getImgActual() {
        return imagenes.get(imgActual);
    }

    public ArrayList<BufferedImage> getImagenes() {
        return imagenes;
    }
    
    @Override
    public void run() {
        Thread hiloActual = Thread.currentThread();
        hilo = hiloActual;

        while (hilo == hiloActual) {
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
