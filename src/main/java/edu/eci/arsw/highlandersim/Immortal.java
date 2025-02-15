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
        while (true) {
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
        synchronized (this) {
            synchronized (i2) {
                if (i2.getHealth() > 0) {
                    i2.changeHealth(i2.getHealth() - defaultDamageValue);
                    this.health += defaultDamageValue;
                    updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
                } else {
                    updateCallback.processReport(this + " says: " + i2 + " is already dead!\n");
                }
            }
        }
    }

    public void changeHealth(int v) {
        health = v;
    }

    public int getHealth() {
        return health;
    }

    @Override
    public String toString() {
        return name + "[" + health + "]";
    }
}
