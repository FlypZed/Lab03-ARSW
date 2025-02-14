/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.prodcons;

import java.util.Random;
import java.util.concurrent.BlockingQueue;


/**
 *
 * @author hcadavid
 */
public class Producer extends Thread {
    private final BlockingQueue<Integer> queue;
    private final Random rand = new Random();

    public Producer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                int dataSeed = rand.nextInt(100); // Genera número aleatorio
                queue.put(dataSeed); // Bloquea si la cola está llena
                System.out.println("Producer added: " + dataSeed);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
