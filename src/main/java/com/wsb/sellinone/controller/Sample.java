package com.wsb.sellinone.controller;

import java.util.ArrayList;
import java.util.stream.IntStream;

public class Sample implements Runnable{ //여기수정
    int seq;
    public Sample(int seq){
        this.seq = seq;
    }

    public void run(){
        System.out.println(this.seq + " thread start. ");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        System.out.println(this.seq + " thread end.");
    }
    public static void main(String[] args){
        ArrayList<Thread> threads = new ArrayList<>();

        IntStream.range(0, 10).forEach(i -> {
                    Thread t = new Thread(new Sample(i)); //여기수정!
                    t.start();
                    threads.add(t);
        });


        threads.stream().forEach(thread ->{
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("main end.");
    }
}
