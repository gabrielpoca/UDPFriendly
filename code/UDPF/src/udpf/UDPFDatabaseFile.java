/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udpf;

import java.util.TreeMap;

/**
 *
 * @author gabrielpoca
 */
public class UDPFDatabaseFile {

    private TreeMap<Long, UDPFDatagram> database;

    public UDPFDatabaseFile() {
	database = new TreeMap<Long, UDPFDatagram>();
    }
    
    public void put(long index, UDPFDatagram datagram) {
	database.put(index, datagram);
    }
    
    public TreeMap<Long, UDPFDatagram> get() {
	return database;
    }
    
}
