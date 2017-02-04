package applet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class Bola extends Thread{
    private BufferedImage imagen;
    private int posX;
    private int posY;
    private int grados;
    
    private double velX;
    private double velY;

    private double velocidad;
    private double inclinacion;
    private static final double GRAVEDAD = 9.81;

    private volatile Thread hilo;

    public Bola(String imagen, int posX, int posY) {
        
        try {
            this.imagen = ImageIO.read(new File("src/Imagenes/"+imagen+".png"));
        } catch (IOException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
        
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

    @Override
    public void run() {
        Thread hiloActual = Thread.currentThread();
        hilo = hiloActual;

        cargar(30, 45);
        
        try{
            while (mover(30)) {
                pausa(30);

                grados += 40;
            }
        }catch (Exception ex) {

        } catch (Throwable ex) {
            
        }
    }
    
    public boolean mover(int tiempo) throws Throwable {
        posX = (int) (getPosX() + (tiempo / 10 * velX));
        posY = (int) (getPosY() - (tiempo / 10 * velY));

        velY = velY - (GRAVEDAD * tiempo / 10000);

        //SI SE GOLPEA CON EL TABLERO
        /*if(rebote(pantalla.getTablero())){
            velX = -velX;
        }*/
        
        return !finMovimiento();

    }
    
    /*public boolean rebote(Elemento tablero){
        Rectangle recBalon = new Rectangle(getPosX(), getPosY(), getAncho(), getAlto());
        Rectangle recTablero = new Rectangle(tablero.getPosX(), tablero.getPosY(), 10, getAlto());
        
        return recBalon.intersects(recTablero);
    }*/
    
    public boolean finMovimiento() throws Throwable{
        //SI SE SALE DE LA PANTALLA POR CUALQUIER LADO EXCEPTO POR ARRIBA
        if(getPosX() < 0 || getPosX() > 800 || getPosY() > 600){
            //VUELVE A SU POSICION ORIGINAL
            //reset();
            return true;
        }else{
            //SI HA METIDO CANASTA
            /*if(comprobarCanasta(pantalla.getCanasta())){
                //VUELVE A SU POSICION ORIGINAL
                reset();
                return true;
            }*/
        }
        return false;
    }
    
    /*public boolean comprobarCanasta(Elemento canasta) throws Throwable{
        //SI ESTA CAYENDO
        if(velY < 0){
            Rectangle recBalon = new Rectangle(getPosX(), getPosY(), getAncho(), 10);
            Rectangle recCanasta = new Rectangle(canasta.getPosX()+30, canasta.getPosY(), canasta.getAncho()-30, 10);

            if(recBalon.intersects(recCanasta)){
                //PUNTUAR SEGUN LA DISTANCIA
                pantalla.encestar();
                return true;
            }
        }
        
        return false;
    }*/

    //CARGAR LA POTENCIA E INCLINACION DE LA PELOTA
    public void cargar(double vel, double inclinacion) {

        this.velocidad = vel / 10;

        velX = velocidad * Math.cos(inclinacion);
        velY = velocidad * Math.sin(inclinacion);

        this.inclinacion = inclinacion;
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
