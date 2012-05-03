/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udpf;

/**
 *
 * @author gabrielpoca
 */
public class UDPFTimeout extends Thread {
    
    private float _time;
    
    public UDPFTimeout(float time) {
	_time = time;
    }
    
    public void setTime(float time) {
	_time = time;
    }
    
    public void run() {
	
    }
    
}
