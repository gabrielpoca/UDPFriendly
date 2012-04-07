/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udpf;

import java.util.ArrayList;
import udpf.UDPFDatagram;
import utils.Debug;

/**
 *
 * @author gabrielpoca
 */
public class UDPFDatabase {
    
    private ArrayList<UDPFDatagram> database;
    
    public UDPFDatabase() {
        database = new ArrayList<UDPFDatagram>();
    }
    
    public synchronized void put (UDPFDatagram n) {
        database.add(n);
        notifyAll();
    }
    
    public synchronized UDPFDatagram get(int last_index) throws InterruptedException {
        while(last_index >= database.size())
            wait();
        return database.get(last_index);
    }
    
}
