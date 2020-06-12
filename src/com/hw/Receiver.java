package com.hw;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

public class Receiver implements Runnable{
    private MBRC mbrc;
    private int result = 0;
    private final String Add = "add";
    private final String Multiply = "multiply";
    private final String DEFAULT_KEY = "mydeskey";
    private final String messageDigest = "thisismsgdigest";

    public Receiver(MBRC mbrc) {
        this.mbrc = mbrc;
        new Thread(this, "Receiver").start();
    }

    public String decrypt(byte[] ciphertext) throws Exception {
        SecretKey desKey = new SecretKeySpec(DEFAULT_KEY.getBytes(), "DES");
        Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        byte[] plaintext = SymmetricEncryption.decrypt(ciphertext, desCipher, desKey);
        String message = new String(plaintext);
        return message;
    }

    public String generate(String messageDigest) throws Exception {
        StringBuilder sb = new StringBuilder();
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] msgBytes = messageDigest.getBytes();
        md.update(msgBytes);
        for(byte b : md.digest()){
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public void printReceivedResult(String message){
        String[] str = message.split(",");
        String operator = str[0];
        int num = Integer.parseInt(str[1]);
        if(Add.equals(operator)){
            AddCalculation addCalculation = new AddCalculation();
            result = addCalculation.add(num);
        }else if(Multiply.equals(operator)){
            MultiplyCalculation multiplyCalculation = new MultiplyCalculation();
            result = multiplyCalculation.multiply(num);
        }else{
            System.out.println("Error parsing message");
            return;
        }
        System.out.println("[Receiver]: Received result is " + result);
    }

    @Override
    public void run() {
        for(int i = 0; i < 7; i++){
            try {
                //receive message from sender via mbrc
                byte[] ciphertext = mbrc.receive();
                System.out.println("[Receiver]: The message is received");

                //decrypt the message
                String msg = decrypt(ciphertext);
                System.out.println("[Receiver]: The message is decrypted");

                //print the result
                printReceivedResult(msg);

                //generate messageDigest
                String revMsgDigest = generate(messageDigest);
                System.out.println("[Receiver]: The messageDigest is generating");

                //reply to sender
                mbrc.reply(revMsgDigest, result);
                System.out.println("[Receiver]: The response is sent");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
