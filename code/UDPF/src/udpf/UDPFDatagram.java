/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package udpf;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import utils.Converter;

/**
 *
 * @author gabrielpoca
 */
public class UDPFDatagram implements Serializable {

    public static final int MTU = 512;

    public enum UDPF_HEADER_TYPE {

        INFO, /* pacote com informaçao */
        SYN, /* enviado pelo cliente para iniciar three way handshake */
        SYN_ACK, /* enviado por servidor para confirmar recepcao do SYN */
        CON_ACK, /* enviado pelo cliente para finalizar three way handshake */
        ACK, /* confirmar a recepcao de um pacote */
        FIN,
        FIN_ACK
    }
    UDPF_HEADER_TYPE _type;
    long _seq_num;
    byte[] _data;

    public UDPFDatagram() {
        _type = UDPF_HEADER_TYPE.INFO;
    }

    public UDPFDatagram(UDPF_HEADER_TYPE type) {
        _type = type;
    }

    public byte[] getData() {
        return _data;
    }

    public void setData(byte[] _data) {
        this._data = _data;
    }

    public long getSeqNum() {
        return _seq_num;
    }

    public void setSeqNum(long _seq_num) {
        this._seq_num = _seq_num;
    }

    public UDPF_HEADER_TYPE getType() {
        return _type;
    }

    public void setType(UDPF_HEADER_TYPE _type) {
        this._type = _type;
    }

    public static UDPFDatagram receivaDatagram(DatagramSocket socket) throws IOException, ClassNotFoundException {
        byte[] receiveData = new byte[512];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        socket.receive(receivePacket);
        return (UDPFDatagram) Converter.bytesToObject(receivePacket.getData());
    }
}
