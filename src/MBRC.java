
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ArrayList;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.Cipher;
import java.security.MessageDigest;
import java.io.*;



//// implementation of a producer and consumer with Queue
class MBRC {
//    private String operation;
//    private int number;
//    private Pair pair;

//    private Pair[] messageBuffer = new Pair[1];
//    private Pair[] responseBuffer = new Pair[1];
    private int maxSize;
    Queue<String> messageBuffer = new LinkedList<>(); ;
    Queue<String> responseBuffer = new LinkedList<>();
//    String[] messageBuffer = new String[1];
//    String[] responseBuffer = new String[1];
    private boolean messageBufferFull;
    private boolean responseBufferFull;

//    Queue<String> messageBuffer = new LinkedList<>();
//    Queue<String> ResponseBuffer = new LinkedList<>();

    public MBRC(){
//      maxSize = 1;
        this.messageBufferFull = false;
        this.responseBufferFull = false;
    }


    synchronized String receive() {
        while(!messageBufferFull)
            try {
                System.out.println("Wait Got: ");
                wait();

            } catch(InterruptedException e) {
                System.out.println("InterruptedException caught");
            }
        String message = messageBuffer.remove();
        messageBufferFull = false;
        System.out.println("Receive: " );
        notify();
        return message;
    }

    synchronized String send(String operator, int number) {
        String str = operator + " , " + number;

        messageBuffer.add(str);
        messageBufferFull = true;
        notify();
        while(!responseBufferFull)
            try {
                System.out.println("Wait Put: ");
                wait();
            } catch(InterruptedException e) {
                System.out.println("InterruptedException caught");
            }


        responseBuffer.remove(str);
        responseBufferFull = false;
        System.out.println("send: " + "( " + operator + ", " + number + " )" );
        return str;
    }


    public void printMessage(String str){
        System.out.println(str);
    }

    synchronized void reply(String operator, int number){
        String str = operator + " , " + number;
        responseBuffer.add(str);
        responseBufferFull = true;
        notify();
    }
}




//
class Receiver implements Runnable {
    private MBRC q;

    public byte[] decrypt(byte s[], Cipher c, SecretKey sk) throws Exception
    {
        c.init(Cipher.DECRYPT_MODE, sk);
        return c.doFinal(s);
    }

    public byte[] generate(String msg) throws Exception
    {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] message = msg.getBytes();
        md.update(message);
        byte[] mdbytes = md.digest();
        return(mdbytes);
    }

    public Receiver(MBRC q) {
        this.q = q;
        new Thread(this, "Receiver").start();
    }

    public void run() {

        for(int i=0; i<10; i++) {
            q.receive();
        }
    }
}


class Sender implements Runnable {
    private MBRC q;
    private KeyGenerator keygen;

    public Sender(MBRC q) {
        this.q = q;
//        this.keygen =  KeyGenerator.getInstance("DES");
        new Thread(this, "Sender").start();

    }

    public SecretKey getKey(){
        SecretKey desKey =  this.keygen.generateKey();
        return desKey;
    }


    public byte[] encrypt(byte s[], Cipher c, SecretKey sk) throws Exception
    {
        c.init(Cipher.ENCRYPT_MODE, sk);
        return c.doFinal(s);
    }


    public Boolean verify(byte[] hashValue, String msg) throws Exception
    {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] msgBytes = msg.getBytes();
        md.update(msgBytes);
        byte[] mdBytes = md.digest();

        if (MessageDigest.isEqual(hashValue, mdBytes))
            return true;
        else
            return false;
    }




    // constructor: define a parameter : sequrity key should be shared by sender and receiver

//    public String toString(){
//        return "{" + operation +  ", " + number +"}";
//    }


    public void printMessage(){

    }

    public void printReceivedResult(){

    }

    public void run() {

        String[] messages = new String[7];
        messages[0] = "add 4";
        messages[1] = "multiply 1";
        messages[2] = "multiply 8";
        messages[3] = "add, 2";
        messages[4] = "add, 3";
        messages[5] = "add, 99";
        messages[6] = "multiply 53";



        for(int i=0; i<q.getPair.size(); i++) {
            q.send("add", 4);
        }
    }
}



class Pair{
    private final String operator;
    private final int number;
    public Pair(String op, int num){
        operator = op;
        number = num;
    }
    public String getOperator() {return this.operator;}
    public int getNumber(){return this.number;}
//    public void setOperator(String str){this.operator = str}

    public String toString(){
        return "{" + operator +  ", " + number +"}";
    }
}

class Test{
    public static void main(String args[]){
//        Pair[] p = new Pair[7];
//        ArrayList<Pair> pairs = new ArrayList<Pair>();
//        pairs.add(new Pair("add", 4));
//        pairs.add(new Pair("multiply", 8));
//        pairs.add(new Pair("multiply", 4));
//        pairs.add(new Pair("add", 2));
//        pairs.add(new Pair("add", 3));
//        pairs.add(new Pair("add", 99));
//        pairs.add(new Pair("multiply", 53));
        for (int i=0; i<pairs.size(); i++)
        System.out.println(pairs.get(i).toString());

    }
}


class AddCalculation {

    public int add(int i) {
        int sum = 0;
        sum = 10 + i;
        return sum;
    }
}

class MultiplyCalculation {
    public int multiply(int i) {
        int mul = 0;
        mul = 10 * i;
        return mul;
    }
}

class ProducerConsumer {
    public static void main(String[] args) {
        System.out.println("Program: To perform addition and multiplication operation on integer 10.");
        System.out.println("------------------------------------------------------------------------");
        MBRC q = new MBRC();
        new Sender(q);
        new Receiver(q);

    }
}