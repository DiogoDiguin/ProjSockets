package udp.bidirecional;

import java.awt.RenderingHints.Key;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JTextArea;
import java.util.Random;

import java.security.SecureRandom;
import javax.crypto.spec.IvParameterSpec;

public class ClienteServidor extends Thread {
	
	private Random random = new Random();

    private final int PORTA_IN;
    private final int PORTA_OUT;
    private final String HOST;
    private final JTextArea txt;

    // Chave de criptografia
    private static final byte[] CHAVE = "chaveencriptacao".getBytes(); // Substitua pela sua chave de criptografia

    public ClienteServidor(String HOST, int PORTA_IN, int PORTA_OUT, JTextArea txt) {
        this.HOST = HOST;
        this.PORTA_IN = PORTA_IN;
        this.PORTA_OUT = PORTA_OUT;
        this.txt = txt;
        txt.append("Iniciado\n");
    }

    public void enviar(String msg) {
        try {
            String mensagemCriptografada = criptografar(msg); // Criptografar a mensagem

            byte[] buffer = mensagemCriptografada.getBytes();
            DatagramPacket pct = new DatagramPacket(
                    buffer,
                    buffer.length,
                    InetAddress.getByName(HOST),
                    PORTA_OUT
            );
            new DatagramSocket().send(pct);

            txt.append("\nMSG: " + msg + "\n");

            if (msg.equals("caca niquel")) {
                String[] vetor = new String[3];
                int qtd;
                
                for (qtd = 0; qtd < 3; qtd ++){
                	int randomNumber = random.nextInt(10);
                    String valor = Integer.toString(randomNumber);
                    vetor[qtd] = valor;
                    
                    //System.out.printf("%d\n", vetor[qtd]);
                    String resultado = vetor[qtd];
                    txt.append(" | ");
                    txt.append(resultado);
                    txt.append(" | ");
                }
                
                if(vetor[0].equals(vetor[1]) && vetor[0].equals(vetor[2])){
                    txt.append("\nGanhou tudo");
                }else if(vetor[0].equals(vetor[1]) && !vetor[0].equals(vetor[2])){
                    txt.append("\n1º e 2º");
                }else if(!vetor[0].equals(vetor[1]) && vetor[0].equals(vetor[2])){
                    txt.append("\n1º e 3º");
                }else if(!vetor[0].equals(vetor[1]) && vetor[1].equals(vetor[2])){
                    txt.append("\n2º e 3º");
                }else{
                    txt.append("\nNenhum igual");
                }
            }
            
        } catch (Exception e) {
            txt.append("\nERRO: " + e.getMessage() + "\n");
        }
    }

    @Override
    public void run() {
        try {
            DatagramSocket srv = new DatagramSocket(PORTA_IN);

            while (true) {
                byte[] buffer = new byte[256];
                DatagramPacket pct = new DatagramPacket(
                        buffer,
                        buffer.length
                );

                srv.receive(pct);
                String mensagemCriptografada = new String(pct.getData()).trim();
                txt.append("DE: " + pct.getAddress().getHostAddress() + "\n");
                txt.append("MSG: " + mensagemCriptografada + "\n");
                String msg = descriptografar(mensagemCriptografada); // Descriptografar a mensagem
                txt.append("MSG: " + msg + "\n\n");
            }

        } catch (Exception e) {
            txt.append("\nERRO: " + e.getMessage() + "\n");
        }
    }

    private String criptografar(String mensagem) throws Exception {
        SecretKeySpec chave = gerarChave();
        Cipher cifrador = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // Geração de IV aleatório
        SecureRandom random = new SecureRandom();
        byte[] ivBytes = new byte[16];
        random.nextBytes(ivBytes);
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        cifrador.init(Cipher.ENCRYPT_MODE, chave, ivSpec);
        byte[] mensagemCriptografada = cifrador.doFinal(mensagem.getBytes());
        byte[] mensagemCriptografadaComIV = new byte[ivBytes.length + mensagemCriptografada.length];
        System.arraycopy(ivBytes, 0, mensagemCriptografadaComIV, 0, ivBytes.length);
        System.arraycopy(mensagemCriptografada, 0, mensagemCriptografadaComIV, ivBytes.length, mensagemCriptografada.length);
        return Base64.getEncoder().encodeToString(mensagemCriptografadaComIV);
    }

    private String descriptografar(String mensagemCriptografada) throws Exception {
        SecretKeySpec chave = gerarChave();
        Cipher cifrador = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // Recuperação do IV a partir da mensagem criptografada
        byte[] mensagemCriptografadaComIV = Base64.getDecoder().decode(mensagemCriptografada);
        byte[] ivBytes = new byte[16];
        System.arraycopy(mensagemCriptografadaComIV, 0, ivBytes, 0, ivBytes.length);
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        cifrador.init(Cipher.DECRYPT_MODE, chave, ivSpec);
        byte[] mensagemBytes = cifrador.doFinal(mensagemCriptografadaComIV, ivBytes.length, mensagemCriptografadaComIV.length - ivBytes.length);
        return new String(mensagemBytes);
    }

    private SecretKeySpec gerarChave() {
        return new SecretKeySpec(CHAVE, "AES");
    }
}