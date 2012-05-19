/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udpf;

import java.util.Observable;
import java.util.Observer;
import utils.Debug;

/**
 *
 * @author gabrielpoca
 */
public class UDPFDatabaseWindow extends UDPFDatabase {
        
    private int _wait;
    
    public UDPFDatabaseWindow() {
        super();
	
	_wait = 1;
    }
    
    public synchronized UDPFDatagram get(int last_index) throws InterruptedException {
        while(last_index >= _database.size())
            wait();
	
	while(_wait == 0)
	    wait();
	
	_wait--;
        return _database.get(last_index);
    }
    
    public synchronized void send(int num) {
	_wait = num;
	notifyAll();
    }

       
}
