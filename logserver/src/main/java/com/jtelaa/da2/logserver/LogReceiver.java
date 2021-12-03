package com.jtelaa.da2.logserver;

import com.jtelaa.da2.lib.misc.MiscUtil;
import com.jtelaa.da2.lib.net.ports.ManualPort;
import com.jtelaa.da2.lib.net.ports.Ports;
import com.jtelaa.da2.lib.net.ports.SysPorts;
import com.jtelaa.da2.lib.net.server.ServerUDP;

/**
 * Receives command responses
 * 
 * @since 2
 * @author Joseph
 */

public class LogReceiver extends Thread {

    /** Default logging port */
    public static final Ports DEFAULT_PORT = SysPorts.LOG;

    /** Port to open */
    private Ports port;

    /**
     * Constructor
     * 
     * @param port port to use in the log receiver
     */

    public LogReceiver(int port) { this.port = new ManualPort(port); }

    /**
     * Constructor
     * 
     * @param port port to use in the log receiver
     */

    public LogReceiver(Ports port) { this.port = port; }

    /**
     * Default port
     */

    public LogReceiver() { this(DEFAULT_PORT); }

    /** Server */
    private ServerUDP cmd_rx;
    public static Logger log;

    /**
     * Run
     */

    public void run() {
        // Setup
        cmd_rx = new ServerUDP(port, "Response Receiver: ");
        log = new Logger();
        log.start();

        // Wait
        while (!run) {
            MiscUtil.waitasec();
            
        }

        // When starting
        if (cmd_rx.startServer()) {
            while (run) {
                String response = cmd_rx.getMessage();

                if (MiscUtil.notBlank(response)) {
                    log.add(response, cmd_rx.getClientAddress());
                
                }
            }
        }
    }

   /** Boolean to control the receiver */
   private boolean run = true;

   /** Stops the command receiver */
   public synchronized void stopReceiver() { run = false; }

   /** Checks if the receier is ready */
   public synchronized boolean receiverReady() { return run; }

    
}