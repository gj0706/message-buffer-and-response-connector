package com.hw;

public class Main {
    public static void main(String[] args) {
        MBRC mbrc = new MBRC();
        new Sender(mbrc);
        new Receiver(mbrc);
    }
}
