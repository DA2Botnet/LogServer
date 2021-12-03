package com.jtelaa.da2.logserver;

import com.jtelaa.da2.lib.console.ConsoleBanners;
import com.jtelaa.da2.lib.console.ConsoleColors;
import com.jtelaa.da2.lib.log.Log;

/**
 * Hello world!
 *
 */

public class App {
    /** The remote cli local object */ 
    public static RemoteCLI rem_cli;
    public static void main( String[] args ) {
        Log.sendManSysMessage(ConsoleBanners.otherBanner("~/banners/MainBanner.txt", ConsoleBanners.EXTERNAL, ConsoleColors.CYAN_BOLD));
        Log.sendManSysMessage("Loading log server.....");
        LogReceiver recvr = new LogReceiver();
        recvr.start();

        rem_cli = new RemoteCLI();
        rem_cli.start();

    }
}
