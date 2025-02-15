/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.prodcons;

import edu.eci.arsw.highlandersim.BlacklistChecker;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StartProduction {
    public static void main(String[] args) {
        List<String> blacklistedServers = List.of("server1.com", "server2.com", "malicious.net", "badhost.com");
        BlacklistChecker checker = new BlacklistChecker(blacklistedServers);

        String host = "badhost.com";
        if (checker.isBlacklisted(host)) {
            System.out.println("Host bloqueado.");
        } else {
            System.out.println("Host seguro.");
        }
    }
}