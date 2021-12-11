import HourTrackerLibrary.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class ConsoleTimeView implements TimeView {
    /**
     * The controller for this class.
     */
    protected TimeController controller = new TimeController(this);
    /**
     * Scanner object for reading user input.
     */
    protected Scanner reader = new Scanner(System.in);

    /**
     * The main method for this view,
     * @param args Args provided here.
     */
    public static void main(String[] args) {
        System.out.println("It seems things loaded correctly.");
    }//end main method
    
    /**
     * 
     */
    protected void displayNormalInfo(){
        // TODO Auto-generated method stub
    }//end displayNormalInfo();

    /**
     * 
     */
    protected void getNormalChoice(){
        // TODO Auto-generated method stub
    }//end getNormalChoice();
    
    /**
     * 
     * @param instance
     * @return
     */
    @Override
    public TimedInstance editInstance(TimedInstance instance) {
        // TODO Auto-generated method stub
        return null;
    }//end editInstance(instance)
    
    /**
     * 
     * @param group
     * @return
     */
    @Override
    public TimeGrouping editGroup(TimeGrouping group) {
        // TODO Auto-generated method stub
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
        String response = "";
        while(response.equals("")){
            System.out.println(message);
            System.out.print("Path: ");
            response = reader.nextLine();
            if(response.equals("")){
                System.out.println("Path cannot be empty.");
            }//end if response is blank
        }//end looping while response is blank
        return response;
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
        displayNormalInfo();
    }//end refreshView()

    /**
     * Refreshes the view to update the time.
     */
    @Override
    public void updateTime() {
        displayNormalInfo();
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
        System.out.println(message);
    }//end logMessage(message)
}//end class App
