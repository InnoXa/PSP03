package applet;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class Bola extends Thread{
    int codigoJugador;
    private BufferedImage imagen;
    private String tipo;
    private int posX;
    private int posY;
    private int grados;
    
    private double velX;
    private double velY;

    private double velocidad;
    private double inclinacion;
    private static final double GRAVEDAD = 9.81;

    private Rectangle rectangulo;
    
    private volatile Thread hilo;

    public Bola(int codigoJugador, String imagen, int posX, int posY, double velocidad, double inclinacion) {
        this.codigoJugador = codigoJugador;
        this.tipo = imagen;
        
        try {
            this.imagen = ImageIO.read(new File("src/Imagenes/"+imagen+".png"));
        } catch (IOException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.posX = posX;
        this.posY = posY;
        grados = 0;
        
        this.velocidad = velocidad;
        this.inclinacion = inclinacion;

        hilo = new Thread(this);
    }

    public BufferedImage getImg() {
        return imagen;
    }

    public void invertirVelX() {
        velX = -velX;
    }

    public String getTipo(){
        return tipo;
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
    
    public Rectangle getRectangulo(){
        return new Rectangle(posX, posY, getImg().getWidth(), getImg().getHeight());
    }

    @Override
    public void run() {
        Thread hiloActual = Thread.currentThread();
        hilo = hiloActual;

        cargar(velocidad, inclinacion);
        
        try{
            while (mover(30) && hilo == hiloActual) {
                pausa(30);

                grados += 40;
            }
        }catch (Exception ex) {

        } catch (Throwable ex) {
            
        }
        
        ListaBolas.bolas.remove(this);
    }
    
    public boolean mover(int tiempo) throws Throwable {
        posX = (int) (getPosX() + (tiempo / 10 * velX));
        posY = (int) (getPosY() - (tiempo / 10 * velY));

        velY = velY - (GRAVEDAD * tiempo / 10000);

        //SI SE GOLPEA CON OTRA BOLA
        if(rebote()){
            velX = -velX;
        }
      
        
        if(getPosY() > Principal.ALTOVENTAJUEGO - getImg().getHeight()){
            velY /= ((velY - (GRAVEDAD * tiempo / 10000)) - 0.1);
        }
        
        return !finMovimiento();
    }
    
        
    
    public synchronized boolean rebote(){
        Bola b;
        
        Iterator<Bola> itb = ListaBolas.bolas.iterator();
        while (itb.hasNext()) {
            b = itb.next();
            
            if(b != this){
                return this.getRectangulo().intersects(b.getRectangulo());
            }
        }
        return false;
    }
    
    public boolean finMovimiento() throws Throwable{
        //SI SE SALE DE LA PANTALLA POR CUALQUIER LADO EXCEPTO POR ARRIBA
        if(getPosX() < -getImg().getWidth() || getPosX() > Principal.ANCHOVENTAJUEGO || getPosY() > Principal.ALTOVENTAJUEGO){
            Principal.vidas--;
            return true;
        }
        return false;
    }

    //CARGAR LA POTENCIA E INCLINACION DE LA PELOTA
    public void cargar(double vel, double inclinacion) {

        this.velocidad = vel / 10;

        velX = velocidad * Math.cos(inclinacion);
        if(codigoJugador == 2){
            velX = -velX;
        }
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

    public synchronized void parar() {
        hilo = null;
    }
}
