package applet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Fondo extends Thread{
    
    private BufferedImage[] frames;
    private int imgActual;
    private volatile Thread hilo;
    
    public Fondo(){
        frames = new BufferedImage[4];
        hilo = new Thread(this);

        for (int i = 0; i < 4; i++) {
            try {
                frames[i] = ImageIO.read(new File("src/Imagenes/"+ i +".gif"));
            } catch (IOException ioe) {
                System.err.println(ioe.getMessage());
                System.exit(0);
            }
        }
        
        imgActual = 0;
    }
    
    public BufferedImage getImagenActual(){
        return frames[imgActual];
    }
    
    public void avanzar() {
        switch (imgActual) {
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
                imgActual = 0;
        }
        pausa(200);
    }
    
    @Override
    public void run(){
        Thread hiloActual = Thread.currentThread();
        hilo = hiloActual;
        
        while(hilo == hiloActual){
            avanzar();
        }
    }
    
    void pausa(int tiempo) {
        try {
            Thread.sleep(tiempo);     //Envia el hilo a dormir
        } catch (InterruptedException ignorada) {
        }
    }
}
