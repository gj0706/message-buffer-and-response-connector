package com.hw;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Queue;

public class Sender implements Runnable{
    private MBRC mbrc;
    private final String messageDigest = "thisismsgdigest";
    private final String DEFAULT_KEY = "mydeskey";
    public Sender(MBRC mbrc) {
        this.mbrc = mbrc;
        new Thread(this, "Sender").start();
    }

    public byte[] encrypt(String message) throws Exception {
        byte[] cleartext = message.getBytes();
        SecretKey desKey = new SecretKeySpec(DEFAULT_KEY.getBytes(), "DES");
        Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        byte[] ciphertext = SymmetricEncryption.encrypt(cleartext, desCipher, desKey);
        return ciphertext;
    }

    public boolean verify(String response){
        StringBuilder sb = new StringBuilder();
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] msgBytes = messageDigest.getBytes();
        md.update(msgBytes);
        for(byte b : md.digest()){
            sb.append(String.format("%02X", b));
        }
        // 正确密码的出来的乱码
        String sendMsgDigest = sb.toString();

        String[] str = response.split(",");
        String msgDigest = str[0];
        int result = Integer.parseInt(str[1]);
        if (sendMsgDigest.equals(msgDigest)){
            printReceivedResult(result);
            return true;
        }
        else{
            System.out.println("Data integrity has been altered");
            return false;
        }
    }

    public void printReceivedResult(int result){
        System.out.println("[Sender]: received " + result);
    }

    public void printMessage(String message){
        System.out.println("[Sender]: Sent " + message);
    }

    @Override
    public void run() {
        Queue<String> messages = new LinkedList<>();
        messages.add("add,4");
        messages.add("multiply,1");
        messages.add("multiply,8");
        messages.add("add,2");
        messages.add("add,3");
        messages.add("add,99");
        messages.add("multiply,53");

        while (!messages.isEmpty()) {
            String msg = messages.poll();
            String[] str = msg.split(",");
            String operator = str[0];
            int num = Integer.parseInt(str[1]);
            System.out.println("------------------------------------------------------------------------");
            System.out.println("Program: To perform " + operator + " operation on " + num);

            try {
                //print
                printMessage(msg);

                //encrypt the message
                System.out.println("[Sender]: The message is encrypted");
                byte[] ciphertext = encrypt(msg);

                //send the message via mbrc object
                System.out.println("[Sender]: The message is sent");
                mbrc.send(ciphertext);

                //verify response
                System.out.println("[Sender]: The response is received");
                byte[] resp = mbrc.responseBuffer.poll();
                String response = new String(resp);
                verify(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
