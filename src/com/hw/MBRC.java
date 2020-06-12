package com.hw;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Queue;

import static java.lang.Thread.sleep;

public class MBRC {

    Queue<byte[]> messageBuffer;
    Queue<byte[]> responseBuffer;

    private boolean messageBufferFull;
    private boolean responseBufferFull;

    public MBRC(){
        this.messageBuffer =  new LinkedList<>();
        this.responseBuffer = new LinkedList<>();
        this.messageBufferFull = false;
        this.responseBufferFull = false;
    }

    synchronized void send(byte[] ciphertext) throws InterruptedException {
        messageBuffer.add(ciphertext);
        messageBufferFull = true;
        notify();
        while(!responseBufferFull){
            try {
                System.out.println("Wait reply");
                wait();
            } catch(InterruptedException e) {
                System.out.println("InterruptedException caught");
            }
        }
        responseBufferFull = false;
    }

    synchronized byte[] receive() throws InterruptedException {
        while(!messageBufferFull){
            try {
                System.out.println("Wait receive");
                wait();

            } catch(InterruptedException e) {
                System.out.println("InterruptedException caught");
            }
        }

        byte[] ciphertext  = messageBuffer.poll();
        messageBufferFull = false;
        notify();
        //暂停一下receiver, 让后续结果跑完, 这样for循环不至于结束太快
        sleep(10);
        return ciphertext;
    }

    synchronized void reply(String messageDigest, int result) throws InterruptedException {
        String response = messageDigest + ',' + result;
        byte[] resp = response.getBytes();
        responseBuffer.add(resp);
        responseBufferFull = true;
        notify();
    }
}
