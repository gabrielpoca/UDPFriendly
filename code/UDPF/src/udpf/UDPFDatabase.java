/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udpf;

import java.util.ArrayList;

/**
 *
 * @author gabrielpoca
 */
public class UDPFDatabase {
    
    protected ArrayList<UDPFDatagram> _database;
    
    public UDPFDatabase() {
        _database = new ArrayList<UDPFDatagram>();
    }
    
    public synchronized void put (UDPFDatagram n) {
        _database.add(n);
        notifyAll();
    }
    
    public synchronized UDPFDatagram get(int last_index) throws InterruptedException {
        while(last_index >= _database.size())
            wait();
        return _database.get(last_index);
    }

    
}
