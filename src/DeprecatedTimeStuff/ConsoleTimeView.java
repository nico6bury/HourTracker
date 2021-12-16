package DeprecatedTimeStuff;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import HourTrackerLibrary.TimeController;
import HourTrackerLibrary.TimeGrouping;
import HourTrackerLibrary.TimeView;
import HourTrackerLibrary.TimedInstance;

/**
 * @deprecated Use {@link HourTrackerTerminal.HourTrackerConsole}
 */
public class ConsoleTimeView implements TimeView {
    /**
     * The controller for this class.
     */
    protected TimeController controller;
    public void setController(TimeController controller){
        this.controller = controller;
        // this.controller.startView();
    }//end setController(controller)
    /**
     * Scanner object for reading user input.
     */
    protected Scanner reader = new Scanner(System.in);
    /**
     * List of messages that have been sent by the controller.
     */
    protected List<String> messages = new ArrayList<String>();
    /**
     * Console for this class?
     */
    // protected IHourTerminal terminal = new HourTrackerConsole();

    public ConsoleTimeView(){
        // nothing I guess
        // terminal.startTerminal();
    }//end no-arg constructor

    /**
     * 
     * @param instance
     * @return
     */
    @Override
    public TimedInstance editInstance(TimedInstance instance) {
        return null;
    }//end editInstance(instance)
    
    /**
     * 
     * @param group
     * @return
     */
    @Override
    public TimeGrouping editGroup(TimeGrouping group) {
        return null;
    }//end editGroup(group)
    
    /**
     * Gets a group name. Will tell user if they enter the name
     * of group that doesn't exist, but return might not necessarily
     * be name of group that currently exists.
     * @return
     */
    @Override
    public String getSelectedGroupName() {
        // build set of group names
        HashSet<String> groupNames = new HashSet<String>();
        for(TimeGrouping group : controller.getGroups()){
            groupNames.add(group.getName());
        }//end building set of group names
        // get into it
        String response = "";
        while(response.equals("")){
            System.out.print("Group Name " +
            controller.getCurrentGroupName() + ") : ");
            response = reader.nextLine();
            if(response.equals("")){
                System.out.println("Group name cannot be empty.");
            }//end if response is empty
        }//end looping while response is empty
        return response;
    }//end getSelectedGroupName()
    
    /**
     * Gets a name for a timed instance.
     * @return Returns the name for the timed instance. Will not be
     * an empty string or null.
     */
    @Override
    public String getCurrentInstanceName() {
        String response = "";
        while(response.equals("")){
            System.out.print("Instance Name: ");
            response = reader.nextLine();
            if(response.equals("")){
                System.out.println("Instance name cannot be empty.");
            }//end if response is empty
        }//end looping while response is empty
        return null;
    }//end getCurrentInstanceName()
    
    /**
     * Gets a path to something from the user.
     * @param message The message to be displayed, asking user about
     * path that they should get.
     * @return Returns the string path that the user gave.
     */
    @Override
    public String getPathWithMessage(String message) {
        return "/home/nicholas/Documents/Programming Stuff/Mischellanious Projects/HourTracker/HourTracker/bin/FileStorage";
        // String response = "";
        // while(response.equals("")){
        //     System.out.println(message);
        //     System.out.print("Path: ");
        //     response = reader.nextLine();
        //     if(response.equals("")){
        //         System.out.println("Path cannot be empty.");
        //     }//end if response is blank
        // }//end looping while response is blank
        // return response;
    }//end getPathWithMessage(message)
    
    /**
     * Gets a yes or no (true or false) from the user.
     * @param message The message to be displayed. Should include
     * the question.
     * @return Returns true if the user said yes, false if the user
     * said no. Will keep asking user if they don't say yes or no,
     * so answer should always be definitive.
     */
    @Override
    public boolean confirmationMessage(String message) {
        // define constants
        String[] yes = { "yes", "y"};
        String[] no = { "no", "n"};
        // get variables ready
        boolean confirm = false;
        boolean gotResponse = false;
        // start geting stuff from the user
        while(!gotResponse){
            System.out.println(message);
            System.out.print("(Y/n): ");
            String response = reader.nextLine();
            if(Arrays.asList(yes).contains(response.toLowerCase())){
                confirm = true;
                gotResponse = true;
            }//end if user said yes
            else if(Arrays.asList(no).contains(response.toLowerCase())){
                confirm = false;
                gotResponse = true;
            }//end else if user said no
            else{
                System.out.println("Input unrecognized. Yes or no.");
            }//end else user said something indeciperable
        }//end looping while 
        return confirm;
    }//end confirmationMessage(message)

    /**
     * Refreshes the view to account for unspecified changes.
     */
    @Override
    public void refreshView() {
        String[] times = {"Time 1", "Time 2", "Time 3"};
        // terminal.updateCurrentTime(times);
        // System.out.println(ConsoleStatics
        // .displayNormalInfo(controller, messages));
        // ConsoleStatics.getNormalChoice();
    }//end refreshView()

    /**
     * Refreshes the view to update the time.
     */
    @Override
    public void updateTime() {
        System.out.println(ConsoleStatics
        .displayNormalInfo(controller, messages));
    }//end updateTime()
    
    /**
     * Displays a message to the console, giving a little
     * bit of space between current and last message.
     * @param message The message to be displayed.
     */
    @Override
    public void displayMessage(String message) {
        System.out.println("\n" + message);
    }//end displayMessage(message)
    
    /**
     * Displays a normal message without spacing between lines.
     * @param message The message displayed.
     */
    @Override
    public void logMessage(String message) {
        //System.out.println(message);
        messages.add(message);
    }//end logMessage(message)

    /**
     * Stores static messages for the ConsoleTimeView class.
     */
    public static class ConsoleStatics{
        /**
         * Separator string to go between rows.
         */
        protected static final String SEPARATOR =
        "-:-:-:-:-:-:-:-:-:-:-:" +
        "-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-";

        /**
         * 
         * @param controller
         * @return
         */
        public static String buildCurrentTime(TimeController controller){
            StringBuilder sb = new StringBuilder();
            // append current time stuff
            sb.append(Instant.now());
            sb.append(" | ");
            sb.append(controller.getClockedTime());
            sb.append(" | ");
            sb.append(controller.getProjectedGroupTotalTime());
            sb.append("\n");
            return sb.toString();
        }//end buildCurrentTime(controller)

        /**
         * 
         * @param controller
         * @return
         */
        public static String buildActiveGroup(TimeController controller){
            StringBuilder sb = new StringBuilder();
            sb.append("Active Group");
            sb.append(" | ");
            sb.append("Time Count");
            sb.append(" | ");
            sb.append("Total Time");
            sb.append("\n");
            return sb.toString();
        }//end buildActiveGroup(controller)

        /**
         * 
         * @param messages
         * @return
         */
        public static String buildMessages(List<String> messages){
            StringBuilder sb = new StringBuilder();
            int messageCount = 0;
            int messageLimit = 5;
            for(int i = messages.size() - 1;
            messageCount < messageLimit && i > 0; i--){
                sb.append(messages.get(i));
                sb.append("\n");
                messageCount++;
            }//end looping over messages
            if(messageCount == 0){
                sb.append("No Messages Yet\n");
            }//end if there are no messags yet.
            return sb.toString();
        }//end buildMessages(messages)

        /**
         * 
         * @param controller
         * @return
         */
        public static String buildGroups(TimeController controller){
            StringBuilder sb = new StringBuilder();
            int groupCount = 0;
            int groupLimit = 4;
            List<TimeGrouping> groups = controller.getGroups();
            for(int i = groups.size() - 1;
            groupCount < groupLimit && i > 0; i--){
                sb.append(groups.get(i).getName());
                sb.append(" | ");
                sb.append(groups.get(i).getTimeCount());
                sb.append(" Times | ");
                sb.append(groups.get(i).getTotalTime());
                sb.append("\n");
            }//end looping over groups
            if(groupCount == 0){
                sb.append("No Groups Yet\n");
            }//end if there are no groups yet
            return sb.toString();
        }//end buildGroups(controller)

        /**
         * 
         * @param controller
         * @return
         */
        public static String buildTimes(TimeController controller){
            StringBuilder sb = new StringBuilder();
            List<TimeGrouping> groups = controller.getGroups();
            int timeCount = 0;
            int timeLimit = 6;
            for(int i = groups.size() - 1;
            timeCount < timeLimit && i > 0; i++){
                List<TimedInstance> times = groups.get(i).getTimes();
                for(int j = times.size() - 1;
                timeCount < timeLimit && j > 0; j++){
                    TimedInstance time = times.get(j);
                    sb.append(time.getName());
                    sb.append(" | ");
                    // add start and end time
                    if(time.getHandleSpecificBeginEnd()){
                        sb.append(time.getStart());
                        sb.append(" - ");
                        sb.append(time.getEnd());
                        sb.append(" | ");
                    }//end if we should have specific start and end
                    // add duration
                    sb.append(time.getDuration());
                    // add date
                    if(time.getHandleDate()){
                        sb.append(" | ");
                        sb.append("Date");
                    }//end if we should include date
                    sb.append("\n");
                }//end looping over times in group
            }//end looping over groups
            if(timeCount == 0){
                sb.append("No Timed Instances Yet\n");
            }//end if there are no times yet
            return sb.toString();
        }//end buildTimes(controller)

        /**
         * Gets a string with the current info
         * @return 
         */
        protected static String displayNormalInfo(TimeController controller,
        List<String> messages){
            StringBuilder sb = new StringBuilder();
            // append current time stuff
            sb.append(buildCurrentTime(controller));
            sb.append(SEPARATOR + "\n");
            // append active group row
            sb.append(buildActiveGroup(controller));
            sb.append(SEPARATOR + "\n");
            // append messages
            sb.append(buildMessages(messages));
            sb.append(SEPARATOR + "\n");
            // append groups
            sb.append(buildGroups(controller));
            sb.append(SEPARATOR + "\n");
            // append timed instances
            sb.append(buildTimes(controller));
            sb.append(SEPARATOR + "\n");
            return sb.toString();
        }//end displayNormalInfo();

        public static final String[] mainMenu = {"ClockIn / Clockout",
    "Set...", "Add...", "Edit...", "Remove...", "Save...", "Page...",
    "Quit"};

        /**
         * 
         */
        protected static void getNormalChoice(){
            int i = 0;
            for(char a = 'A'; a < 'Z' && i < mainMenu.length; a++){
                System.out.print(a + ") " + mainMenu[i] + "\n");                
                i++;
            }//end looping through options
        }//end getNormalChoice();

        /**
         * Gives a parallel array of options for each given choice in
         * order to make getting the user-selected option easy.
         * @param choices The list of choices.
         * @return Returns the parallel array of options for each
         * given choice.
         * @see #getChoiceText(String[])
         */
        @SuppressWarnings("unchecked")
        protected static List<String>[] getValidOptions(String[] choices){
            // create out whacky return type
            List<String>[] validOptions =
            (List<String>[]) new List[choices.length];
            // loop through and generate all our stuff
            char charOpt = 'A';
            int numOpt = 1;
            for(int i = 0; i < validOptions.length; i++){
                // initialize inner list
                validOptions[i] = new ArrayList<String>();
                // add letter option
                validOptions[i].add(String.valueOf(charOpt));
                // add number option
                validOptions[i].add(String.valueOf(numOpt));
                // add name option
                validOptions[i].add(choices[i]);
                // update counter variables
                charOpt++;
                numOpt++;
            }//end generating valid input for each choice
            // return out whacky return type
            return validOptions;
        }//end getValidOptions();

        /**
         * Gets a list of the actual text that should be displayed
         * in the terminal, including the text for each choice, when
         * using with the getValidOptions(choices) method.
         * @param choices
         * @return Returns a list of generated choice text suitable for
         * showing to a user in order to have them choose amongst several
         * options.
         * @see #getValidOptions(String[])
         */
        protected static List<String> getChoiceText(String[] choices){
            List<String> choiceText = new ArrayList<String>();
            for(char a = 'A'; a <= 'Z'; a++){
                int curIndex = a - 65;
                choiceText.add(a + ") " + choices[curIndex]);
            }//end looping over choices
            return choiceText;
        }//end getChoiceText
    }//end class ConsoleStatics
}//end class App
