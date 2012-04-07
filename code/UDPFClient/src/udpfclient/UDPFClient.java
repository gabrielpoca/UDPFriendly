/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udpfclient;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import udpf.UDPFSend;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import udpf.UDPFDatabase;
import udpf.UDPFDatagram;
import utils.Converter;
import utils.Debug;


class UDPFMain extends Thread {
    
    public static final int BUFFER_SIZE = 512;    
    public static final int PORT = 9998;
 
    /* Server Information. */
    private InetAddress _addr;
    private int _port;

    /* Local variables. */
    private DatagramSocket _socket;    
    private UDPFDatabase _db;
    private UDPFSend _send;
    private String _file;
        
    private boolean _run;

    public UDPFMain(String file) throws SocketException, UnknownHostException {
        // Start socket and database.
        _socket = new DatagramSocket(PORT);
        _db = new UDPFDatabase();
        // Set send
        _addr = InetAddress.getByName("localhost");
        _port = 9999;
        _send = new UDPFSend(_db, _socket, _addr, _port);
        // Set file
        _file = file;
        // Set run
        _run = true;
    }

    @Override
    public void run() {
        /* Start send. */
        Thread t = new Thread(_send);
        t.start();
        /* Send first package. */
        putStartDatagram();
        while(_run) {
            try {
                byte[] buffer = new byte[BUFFER_SIZE];
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                _socket.receive(receivePacket);
                UDPFDatagram receiveDatagram = (UDPFDatagram) Converter.bytesToObject(receivePacket.getData());
                Debug.debug("CLIENT: Package Received! "+receiveDatagram.getType().name());
                switch(receiveDatagram.getType()) {
                    case SYN_ACK: // End Handshake
                        /* Update Server Port. */
                        _port = receivePacket.getPort();
                        _send.setPort(_port);
                        /* End Handshake. */
                        putEndHandshake();
                        /* Send File. */
                        putFile();
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
    
    public void putFile() throws FileNotFoundException, IOException {
        byte[] file_info = Converter.filetoBytes(_file);
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
    public static void main(String[] args) throws SocketException, UnknownHostException {
        Thread t = new Thread(new UDPFMain("/Users/gabrielpoca/Projects/UDPFriendly/text.txt"));
        t.start();
    }
    
}