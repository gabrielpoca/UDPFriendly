/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udpf;

/**
 *
 * @author gabrielpoca
 */
public class UDPFDatabaseWindow extends UDPFDatabase {
    private int _sent;
    private int _window;
    
    public UDPFDatabaseWindow(int window) {
        super();
	_sent = 0;
	_window = window;
    }
    
    public synchronized UDPFDatagram get(int last_index) throws InterruptedException {
        while(last_index >= _database.size() && _window != _sent)
            wait();
	_sent++;
        return _database.get(last_index);
    }
    
    public synchronized void resetSent() {
	_sent = 0;
    }
    
    public synchronized void setWindow(int window) {
	_window = window;
    }
       
}
