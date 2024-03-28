package udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class Cliente extends Thread{
    
    private final String HOST = "127.0.0.1";
    private final int PORTA = 12345;
    
    public Cliente(){
    }
    
    public void enviar(String msg){
        try {
            byte[] buffer = msg.getBytes();
            DatagramPacket pct = new DatagramPacket(
                buffer,
                buffer.length,
                InetAddress.getByName(HOST),
                PORTA
            );
            new DatagramSocket().send(pct);
            
        } catch (Exception e) {
            System.err.println("ERRO: " + e.getMessage());
        }
    }
    
}
