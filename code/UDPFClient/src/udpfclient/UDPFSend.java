/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udpfclient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Converter;

/**
 *
 * @author gabrielpoca
 */
public class UDPFSend extends Thread {

    private InetAddress _addr;
    private int _port;
    private DatagramSocket _socket;
    private DatagramDatabase _db;
    private boolean _run;

    public UDPFSend(DatagramDatabase db, InetAddress addr, int port) {
        _db = db;
        _run = true;
        _addr = addr;
        _port = port;
    }

    @Override
    public void run() {
        try {
            byte[] send_info = null;
            int i = 0;
            while (_run) {
                send_info = Converter.objectToBytes(_db.get(i));
                _socket.send(new DatagramPacket(send_info, send_info.length, _addr, _port));
                i++;
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(UDPFSend.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UDPFSend.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stopSend() {
        _run = false;
    }
}
