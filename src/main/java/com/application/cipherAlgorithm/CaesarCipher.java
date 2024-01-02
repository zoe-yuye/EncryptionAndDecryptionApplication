package com.application.cipherAlgorithm;

public class CaesarCipher {
    public static void main(String[] args) {

    }
    public String encrypt(int n,String s) {

        char[] oldS = s.toCharArray();
        char[] newS = new char[oldS.length];
        for(int i= 0; i < oldS.length; i++) {
            newS[i] = (char) ((oldS[i] + n -33) % 94 + 33);
        }
        return String.valueOf(newS);
    }

    public String decrypt(int n, String s) {
        char[] oldS = s.toCharArray();
        char[] newS = new char[oldS.length];

        for(int i= 0; i < oldS.length; i++) {
            newS[i] = (char) ((oldS[i] - n -33 + 94) % 94 + 33);
        }
        return String.valueOf(newS);
    }
}
