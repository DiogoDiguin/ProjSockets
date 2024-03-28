package udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Servidor extends Thread {
    
    private final int PORTA = 12345;

    public Servidor() {
        System.out.println("Servidor iniciado.");
    }
    
    @Override
    public void run(){
        try {
            DatagramSocket srv = new DatagramSocket(PORTA);
            
            while(true){
                byte[] buffer = new byte[256];
                DatagramPacket pct = new DatagramPacket(
                    buffer,
                    buffer.length
                );
                
                srv.receive(pct);
                
                String msg = new String(pct.getData()).trim();
                
                System.out.println("DE");
                System.out.println(pct.getAddress().getHostAddress());
                System.out.println("\nMSG");
                System.out.println(msg + "\n\n");
            }
            
        } catch (Exception e) {
            System.err.println("ERRO: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        Servidor srv = new Servidor();
        srv.start();
    }
    
    
}
