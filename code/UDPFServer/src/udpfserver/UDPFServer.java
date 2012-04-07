/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udpfserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.awt.windows.ThemeReader;
import sun.security.timestamp.TSRequest;
import udpf.UDPFDatabase;
import utils.Converter;
import udpf.UDPFDatagram;
import udpf.UDPFSend;
import utils.Debug;

/**
 *
 * @author gabrielpoca
 */
class UDPFServerReceiver extends Thread {

    private UDPFDatabase _db;
    private UDPFSend _send;
    
    private ArrayList<Integer> _ports_used;
    
    ArrayList<Long> _sent = new ArrayList<Long>();
    ArrayList<Long> _confirmed = new ArrayList<Long>();
    boolean _run; // while control
    DatagramSocket _ss;
    InetAddress _addrs;
    int _port;

    public UDPFServerReceiver(DatagramSocket ss, InetAddress addrs, int port, ArrayList<Integer> ports_used) {
        _ports_used = ports_used;
        _db = new UDPFDatabase();
        _ss = ss;
        _addrs = addrs;
        _port = port;
        _send = new UDPFSend(_db, _ss, _addrs, _port);
    }

    public void run() {
        Thread t = new Thread(_send);
        t.start();
        _run = true;
        putStartHandshake();
        byte[] receiveData = new byte[512];
        byte[] sendData = new byte[1024];
        while (_run) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                _ss.receive(receivePacket);
                UDPFDatagram data = (UDPFDatagram) Converter.bytesToObject(receivePacket.getData());
                Debug.debug("ServerReceiver: Package Received "+data.getType().name());
                switch (data.getType()) {

                }
            } catch (IOException ex) {
                Logger.getLogger(UDPFServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(UDPFServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        _ports_used.remove(_ss.getPort());
        _send.stopSend();
    }

    /**
     * Start a three way hand shake.
     */
    public void putStartHandshake() {
        _db.put(new UDPFDatagram(UDPFDatagram.UDPF_HEADER_TYPE.SYN_ACK));
    }
}

public class UDPFServer extends Thread {

    private final int[] PORTS_ALLOWED = {9997, 9996};
    private ArrayList<Integer> _ports_used;

    public UDPFServer() {
        _ports_used = new ArrayList<Integer>();
    }

    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(9999);
            boolean run = true;
            while (run) {
                byte[] buffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(receivePacket);
                UDPFDatagram receiveDatagram = (UDPFDatagram) Converter.bytesToObject(receivePacket.getData());
                Debug.debug("Server: Package Received! "+receiveDatagram.getType().name());
                switch (receiveDatagram.getType()) {
                    case SYN:
                        if (_ports_used.size() < PORTS_ALLOWED.length) {
                            int port = receivePacket.getPort();
                            InetAddress addr = receivePacket.getAddress();
                            DatagramSocket ds = getNewDatagramSocket();
                            if (ds != null) {
                                Thread t = new Thread(new UDPFServerReceiver(ds, addr, port, _ports_used));
                                t.start();
                            } else {
                                throw new NullPointerException("DatagramSocket NULL!");
                            }
                        } else {
                            System.out.print("Max number of connections allowed reached!");
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UDPFServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UDPFServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public DatagramSocket getNewDatagramSocket() throws SocketException {
        boolean found = false;
        int port = -1;
        for (int i = 0; i < PORTS_ALLOWED.length && !found; i++) {
            if (!_ports_used.contains(PORTS_ALLOWED[i])) {
                found = true;
                port = PORTS_ALLOWED[i];
            }
        }
        if (!found) {
            return null;
        } else {
            return new DatagramSocket(port);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Thread t = new Thread(new UDPFServer());
        t.start();
    }
}
