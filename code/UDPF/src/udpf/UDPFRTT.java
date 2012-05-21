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

    /* Timeout tmp variables. */
    private long _timeout;
    private long _devRTT;
    private long _estimatedRTT;
    
    private int _treshdold;
    
    private final float _alpha = (float) 0.125;
    private final float _beta = (float) 0.25;
    
    private int _window;
    public TreeMap<Long, Long> _db_recv;
    public TreeMap<Long, Long> _db_sent;
    

    public UDPFRTT() {
	_db_recv = new TreeMap<Long, Long>();
	_db_sent = new TreeMap<Long, Long>();
	_timeout = 0;
	_window = 1;
	_treshdold = 0;
    }

    public UDPFRTT(int timeout) {
	_timeout = timeout;
	_db_recv = new TreeMap<Long, Long>();
	_db_sent = new TreeMap<Long, Long>();
    }

    public void addRecvTime(long time, long seq) {
	_db_recv.put(seq, time);
	try {
	calculateTimeOut(time - _db_sent.get(seq));
	} catch (NullPointerException e) {
	    
	}
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
    
    /*
     * Estima o RTT baseando-se no actual e na nova medicao
     */
    private long estimateRTT(long sampleRTT) {
        long newRTT = (long) ((1 - _alpha) * _estimatedRTT + _alpha * sampleRTT);
        _estimatedRTT = newRTT;
	return _estimatedRTT;
    }

    /*
     * Calcula desvio padrão do RTT
     */
    private long calculateDevRTT(long sampleRTT) {
        long newdevRTT = (long) ((1 - _beta) * _devRTT + _beta * (Math.abs(sampleRTT - _estimatedRTT)));
        _devRTT = newdevRTT;
	return _devRTT;
    }

    private void calculateTimeOut(long sampleRTT) {
        _timeout = estimateRTT(sampleRTT) + 4 * calculateDevRTT(sampleRTT);
    }    
    
}
