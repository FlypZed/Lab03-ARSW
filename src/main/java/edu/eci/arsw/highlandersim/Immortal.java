package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;

class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback;
    private int health;
    private final int defaultDamageValue;
    private final List<Immortal> immortalsPopulation;
    private final String name;
    private final Random r = new Random(System.currentTimeMillis());
    private final ControlFrame controller;
    private volatile boolean running = true;

    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue,
            ImmortalUpdateReportCallback ucb, ControlFrame controller) {
        super(name);
        this.updateCallback = ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue = defaultDamageValue;
        this.controller = controller;
    }

    public void run() {
        Immortal opponent;
        while (running) {
            controller.waitIfPaused();

            if (immortalsPopulation.size() <= 1)
                break;

            int myIndex = immortalsPopulation.indexOf(this);
            int opponentIndex = r.nextInt(immortalsPopulation.size());

            if (opponentIndex == myIndex) {
                opponentIndex = (opponentIndex + 1) % immortalsPopulation.size();
            }

            opponent = immortalsPopulation.get(opponentIndex);
            fight(opponent);

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void fight(Immortal i2) {
        Immortal first = this.hashCode() < i2.hashCode() ? this : i2;
        Immortal second = this.hashCode() < i2.hashCode() ? i2 : this;
        synchronized (first) {
            synchronized (second) {
                if (i2.getHealth() > 0 && immortalsPopulation.contains(second) && immortalsPopulation.contains(first)) {
                    i2.changeHealth(i2.getHealth() - defaultDamageValue);
                    this.health += defaultDamageValue;
                    updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
                    if (i2.getHealth() <= 0) {
                        updateCallback.processReport(this + " says: " + i2 + " is dead!\n");
                        immortalsPopulation.remove(i2);

                    }
                } else {
                    updateCallback.processReport(this + " says: " + i2 + " is already dead!\n");
                }
            }
        }
        if (immortalsPopulation.size() == 1) {
            updateCallback.processReport(this + " says: " + immortalsPopulation.get(0) + " wins!\n");
        }
    }

    public void changeHealth(int v) {
        health = v;
    }

    public int getHealth() {
        return health;
    }

    public void stopInmmortal() {
        running = false;
    }

    @Override
    public String toString() {
        return name + "[" + health + "]";
    }
}
