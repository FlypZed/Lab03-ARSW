package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BlacklistChecker {
    private static final int BLACK_LIST_ALARM_COUNT = 5;
    private AtomicInteger occurrences = new AtomicInteger(0);
    private List<String> blacklistServers;

    public BlacklistChecker(List<String> servers) {
        this.blacklistServers = servers;
    }

    public boolean isBlacklisted(String host) {
        int numThreads = Runtime.getRuntime().availableProcessors();
        Thread[] threads = new Thread[numThreads];
        int chunkSize = blacklistServers.size() / numThreads;

        for (int i = 0; i < numThreads; i++) {
            int start = i * chunkSize;
            int end = (i == numThreads - 1) ? blacklistServers.size() : (start + chunkSize);

            threads[i] = new Thread(() -> {
                for (int j = start; j < end && occurrences.get() < BLACK_LIST_ALARM_COUNT; j++) {
                    if (checkServer(blacklistServers.get(j), host)) {
                        if (occurrences.incrementAndGet() >= BLACK_LIST_ALARM_COUNT) {
                            break;
                        }
                    }
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return occurrences.get() >= BLACK_LIST_ALARM_COUNT;
    }

    private boolean checkServer(String server, String host) {
        return server.contains(host);
    }
}