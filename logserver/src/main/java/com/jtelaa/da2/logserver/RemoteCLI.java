package com.jtelaa.da2.logserver;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import com.jtelaa.da2.lib.cli.Cases;
import com.jtelaa.da2.lib.cli.LocalCLI;
import com.jtelaa.da2.lib.console.ConsoleBanners;
import com.jtelaa.da2.lib.console.ConsoleColors;
import com.jtelaa.da2.lib.control.Command;
import com.jtelaa.da2.lib.control.ComputerControl;
import com.jtelaa.da2.lib.log.Log;
import com.jtelaa.da2.lib.misc.MiscUtil;

public class RemoteCLI extends LocalCLI {

    private boolean run_as_local;

    public RemoteCLI() { run_as_local = false; }
    public RemoteCLI(boolean run_as_local) { this.run_as_local = run_as_local; }

    @Override
    public void run() {
        while (!run) {
            MiscUtil.waitasec();

        }

        Log.sendMessage("CLI: Starting Local CLI");

        runRX();
    }

    @Override
    public synchronized String terminal(Command command) {
        Command[] commands = command.split(" ");
        Command cmd = commands[0];

        String response = "";

        // Shutdown
        if (Cases.exit(cmd)) {
            run = false;
            return "Shutting Down CLI";

        // CMD
        } else if (Cases.command(cmd)) {
            ComputerControl.sendCommand(command.addBlankUser().addBlankControlID().modifyforSys());

        // Dump Query Queue
        } else if (Cases.checkCase(cmd, new String[] {"dump"})) {
            response = "Listing Log Queue";
            int qty_to_dump = 100;

            // If command has arg
            if (commands.length > 1) {
                try {
                    // Parse the integer
                    qty_to_dump = Integer.parseInt(commands[1].command());

                } catch (NumberFormatException e) {
                    // Add error message
                    response += "\nInvalid Arg (Default 100)\n";

                }
            }

            if (qty_to_dump > LogReceiver.log.entry_queue.size()) { qty_to_dump = LogReceiver.log.entry_queue.size(); }
            response += "\nDumping " + qty_to_dump + "\n\n\n";

            // List out queries
            for (int i = 0; i < qty_to_dump; i++) {
                response += LogReceiver.log.address_queue.poll() + " " + LogReceiver.log.entry_queue.poll() + "\n";

            }

        // Clear Queues
        } else if (Cases.checkCase(cmd, new String[] {"clear"})) {

            if (commands.length > 1) {
                int qty_to_remove;

                try {
                    qty_to_remove = Integer.parseInt(commands[1].command());

                } catch (NumberFormatException e) {
                    qty_to_remove = 10;

                }
                
                if (qty_to_remove > LogReceiver.log.entry_queue.size()) { qty_to_remove = LogReceiver.log.entry_queue.size(); }

                int i = 0;
                for (; i < qty_to_remove; i++) {
                    try {
                        LogReceiver.log.entry_queue.remove();
                        LogReceiver.log.address_queue.remove();

                    } catch (NoSuchElementException e) {
                        response = "Cleared " + i + " entries";

                    }
                }

                response = "Cleared " + i + " entries";

            } else {
                LogReceiver.log.entry_queue = new LinkedList<>();
                LogReceiver.log.address_queue = new LinkedList<>();

                response = "Cleared Queues";

            }

            

        // Get or change size of queue
        } else if (Cases.checkCase(cmd, new String[] {"title"})) {
            response += ConsoleColors.CLEAR.getEscape() + ConsoleColors.LINES.getEscape();
            response += ConsoleBanners.otherBanner("~banners/MainBanner.txt", ConsoleColors.CYAN_BOLD) + "\n";
            
        } else if (Cases.help(cmd)) {
            response += ConsoleColors.CLEAR.getEscape() + ConsoleColors.LINES.getEscape();
            response += ConsoleBanners.otherBanner("~banners/MainBanner.txt", ConsoleColors.CYAN_BOLD) + "\n";

            String help = (
                "Query Generator CLI Help:\n"
                + ConsoleColors.YELLOW_UNDERLINED.getEscape() + "cmd" + ConsoleColors.RESET.getEscape() + " -> pass through a command to the systems OS\n"
                + ConsoleColors.YELLOW_UNDERLINED.getEscape() + "dump x" + ConsoleColors.RESET.getEscape() + " -> remove x queries from queue and print them (default 100)\n"
                + ConsoleColors.YELLOW_UNDERLINED.getEscape() + "clear x" + ConsoleColors.RESET.getEscape() + " -> clear x queries from the queue and clear bot queue (default all)\n"
            );

            response += help + ConsoleColors.LINES_SHORT.getEscape();

        }

        return response;
        
    }
}
