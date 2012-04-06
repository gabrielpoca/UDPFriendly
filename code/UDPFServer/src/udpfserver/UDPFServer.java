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
import utils.Converter;
import udpf.UDPFDatagram;
import utils.Debug;

/**
 *
 * @author gabrielpoca
 */
public class UDPFServer extends Thread {

    ArrayList<Long> _sent = new ArrayList<Long>();
    ArrayList<Long> _confirmed = new ArrayList<Long>();
    boolean _run; // while control
    DatagramSocket _ss;
    InetAddress _addrs;
    int _port;

    public UDPFServer(DatagramSocket ss, InetAddress addrs, int port) {
        _ss = ss;
        _addrs = addrs;
        _port = port;
    }

    public void run() {
        _run = true;
        byte[] receiveData = new byte[512];
        byte[] sendData = new byte[1024];
        while (_run) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                _ss.receive(receivePacket);
                UDPFDatagram data = (UDPFDatagram) Converter.bytesToObject(receivePacket.getData());
                switch (data.getType()) {

                }
            } catch (IOException ex) {
                Logger.getLogger(UDPFServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(UDPFServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SocketException, InterruptedException {
        //DatagramSocket ss = new DatagramSocket(9999);
        boolean run = true;
        Debug.debug("Starting!");

    }
}
