/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udpfclient;

import java.util.ArrayList;
import udpf.UDPFDatagram;

/**
 *
 * @author gabrielpoca
 */
public class DatagramDatabase {
    
    private ArrayList<UDPFDatagram> database;
    
    public DatagramDatabase() {
        database = new ArrayList<UDPFDatagram>();
    }
    
    public synchronized void put (UDPFDatagram n) {
        database.add(n);
        notifyAll();
    }
    
    public synchronized UDPFDatagram get(int last_index) throws InterruptedException {
        while(last_index >= database.size() - 1)
            wait();
        return database.get(last_index);
    }
    
}
