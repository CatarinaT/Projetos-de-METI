/**
 * SERVIÇOS DE REDE E APLICAÇÕES MULTIMÉDIA 2024
 *
 * @name Protocolo Transporte Multimédia
 *
 * @SDK Oracle OpenJDK version 22
 *
 * @date 18/06/24
 *
 * @authors Catarina Pereira  <pg53733@alunos.uminho.pt>
 *          Inês Neves <pg53864@alunos.uminho.pt>
 *          Leonardo Martins <pg53996@alunos.uminho.pt>
 *
 * @details Este programa tem como objetivo testar um protocolo de transporte
 *          Multimédia para enviar dados de um servidor para um cliente através de
 *          um reencaminhador que irá introduzir um atraso e perdas de pacotes na
 *          comunicação
 */

import java.io.Serializable;
import java.util.ArrayList;
public class PDU implements Serializable {
    private int F;
    private int A;
    private ArrayList<byte[]> data;
    private String string = null;
    private long timeSentMillis = 0;

    public PDU(int F, int A, ArrayList<byte[]> data) {
        this.F = F;
        this.A = A;
        this.data = data;
    }
    public PDU(int F, int A){
        this.F = F;
        this.A = A;
        this.data = new ArrayList<>();
    }

    public int getF() {
        return F;
    }

    public int getA() {
        return A;
    }

    public ArrayList<byte[]> getData() {
        return data;
    }

    public void addDigit(byte[] digit){
        this.data.add(digit);
    }

    public void addString(String string){
        this.string = string;
    }

    public String getString(){
        return this.string;
    }

    public void setTimeSent(){this.timeSentMillis = System.currentTimeMillis();}
    public long getTimeSentMillis(){return this.timeSentMillis;}
}