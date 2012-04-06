/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udpfclient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import udpf.UDPFDatagram;
import utils.Converter;


class UDPFMain extends Thread {
    
    public static final int BUFFER_SIZE = 512;
    
    public static final int PORT = 9999;

    private DatagramDatabase _db;
    
    private DatagramSocket _socket;
    private InetAddress _addr;
    private int _port;
    private UDPFSend _send;
    
    private boolean _run;

    public UDPFMain() throws SocketException {
        _socket = new DatagramSocket(PORT);
        _db = new DatagramDatabase();
        _send = new UDPFSend(_db, _addr, _port);
    }

    @Override
    public void run() {
        _send.start();
        //putStartDatagram();
        while(_run) {
            try {
                byte[] buffer = new byte[BUFFER_SIZE];
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                _socket.receive(receivePacket);
                UDPFDatagram datagram = (UDPFDatagram) Converter.bytesToObject(receivePacket.getData());
                switch(datagram.getType()) {
                    case SYN_ACK: // End Handshake
                        putEndHandshake();
                        break;
                    case FIN_ACK: // End Comunication
                        break;
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(UDPFMain.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(UDPFMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        _send.stopSend();
    }
    
    
    /**
     * End comunication.
     */
    public void putEndComunication() {
        _db.put(new UDPFDatagram(UDPFDatagram.UDPF_HEADER_TYPE.FIN));
    }
    
    /**
     * End three way handshake.
     */
    public void putEndHandshake() {
        _db.put(new UDPFDatagram(UDPFDatagram.UDPF_HEADER_TYPE.CON_ACK));
    }

    /**
     * Put syn message in database to be sent.
     */
    public void putStartDatagram() {
        _db.put(new UDPFDatagram(UDPFDatagram.UDPF_HEADER_TYPE.SYN));
    }
}


public class UDPFClient {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SocketException {
        Thread t = new Thread(new UDPFMain());
        t.start();
    }
}