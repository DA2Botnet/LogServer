package com.jtelaa.da2.logserver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Queue;

import com.jtelaa.da2.lib.control.ComputerControl;
import com.jtelaa.da2.lib.misc.MiscUtil;

public class Logger extends Thread {
    /** Log entry queue */
    public volatile Queue<String> entry_queue;
    
    /** Queue of the ip addresses */
    public volatile Queue<String> address_queue;

    /** Logging path */
    private volatile String path = "~/logserver/";

    /** @param path Sets the logging path*/
    public synchronized void setPath(String path) { this.path = path; }

    /** @param path Create the log directory */
    public synchronized void createFolder(String path) { ComputerControl.sendCommand("mkdir " + path); }

    /**
     * Adds an entry into the queue
     * 
     * @param entry Log entry
     * @param from Source IP
     */

    public synchronized void add(String entry, String from) { entry_queue.add(entry); address_queue.add(from); }

    /**
     * Runs the thread
     */

    public void run() {
        // Wait to start
        while (!run) { MiscUtil.waitasec(); }

        // Get date
        LocalDate current_date = LocalDate.now();

        // File
        File file;
        FileWriter fs_out;

        while (run) {
            // Check data
            int current_day = current_date.getDayOfMonth();

            // Log file
            try {
                file = new File(path + "log" + current_date.getDayOfYear() + "-" + current_date.getYear() + ".txt");
                fs_out = new FileWriter(file);

            } catch (IOException e) { 
                run = false;
                e.printStackTrace(); 
                break;
            
            }

            // While current dat file
            while (current_day == current_date.getDayOfMonth() && run) {
                try {
                    fs_out.write(System.currentTimeMillis() + ": " + address_queue.poll() + ">" + entry_queue.poll());
                    
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }

            // Close
            try {
                fs_out.close();
    
            } catch (IOException e) {
                e.printStackTrace();
    
            }
        }
    }


    /** Boolean to control the receiver */
    private volatile boolean run = false;

    /** Stops the command receiver */
    public synchronized void stopLogger() { run = false; }

    /** Stops the command receiver */
    public synchronized void startLogger() { run = true; }

    /** Checks if the receier is ready */
    public synchronized boolean loggerReady() { return run; }
    
}
