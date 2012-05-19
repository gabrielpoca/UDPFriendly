/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udpf;

import java.util.TreeMap;
import utils.Debug;

/**
 *
 * @author gabrielpoca
 */
public class UDPFRTT {

    private long _timeout;
    private int _treshdold;
    
    private float _a;
    private float _b;
    private int _window;
    public TreeMap<Long, Long> _db_recv;
    public TreeMap<Long, Long> _db_sent;
    

    public UDPFRTT() {
	_db_recv = new TreeMap<Long, Long>();
	_db_sent = new TreeMap<Long, Long>();
	_a = (float) 0.125;
	_timeout = 0;
	_window = 1;
	_treshdold = 0;
    }

    public UDPFRTT(int timeout) {
	_timeout = timeout;
	_db_recv = new TreeMap<Long, Long>();
	_db_sent = new TreeMap<Long, Long>();
	_a = (float) 0.125;
    }

    public void addRecvTime(long time, long seq) {
	_db_recv.put(seq, time);
    }

    public void addSentTime(long time, long seq) {
	_db_sent.put(seq, time);
    }
    
    public void incWindow() {
	if(_window <= _treshdold || _treshdold == 0)
	    _window = _window * 2;
	else _window += 1;
    }

    public int getWindow() {
	return _window;
    }

    public void setWindow(int window) {
	_window = window;
    }

    public long getTimeout() {
	return _timeout;
    }

    public void setTimeout(long timeout) {
	_timeout = timeout;
    }

    public void setTreshold(int treshold) {
	_treshdold = treshold;
    }
    
    public int getTreshold() {
	return _treshdold;
    }
    
    public TreeMap<Long, Long> getSentDB() {
	return _db_sent;
    }
    
    public TreeMap<Long, Long> getRecvDB() {
	return _db_recv;
    }
}
