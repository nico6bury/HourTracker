package HourTrackerTerminal;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import HourTrackerLibrary.TimeController;
import HourTrackerLibrary.TimeGrouping;
import HourTrackerLibrary.TimeView;
import HourTrackerLibrary.TimedInstance;

/**
 * This class basically handles all
 * the console stuff in order to make it easier for the View class
 * to do consoele stuff easily.
 */
public class HourTrackerConsole implements TimeView  {
	Terminal terminal = null;
	TextGraphics graphics = null;
	int curMessageCount = 0;
	int curGroupCount = 0;
	int curTimeCount = 0;
	int curOptionsCount = 0;
	boolean isGroupActive = false;
	int getCurTimeRow(){return 1;}
	int getActiveGroupRow(){
		if(isGroupActive) return getCurTimeRow() + 1 + 1;
		else return getCurTimeRow();
	}//end getActiveGroupColumn()
	int getMessageRow(){return getActiveGroupRow() + 1 + 1;}
	int getGroupRow(){return getMessageRow() + curMessageCount + 1;}
	int getTimeRow(){return getGroupRow() + curGroupCount + 1;}
	int getOptionRow(){return getTimeRow() + curTimeCount + 1;}
	int getInputRow(){return getOptionRow() + curOptionsCount + 1;}
	boolean terminalStarted = false;
	List<String> savedMessages = new ArrayList<String>();
	protected static final String SEPARATOR =
        "-:-:-:-:-:-:-:-:-:-:-:" +
        "-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-";
	String[] mainMenu = {"ClockIn / Clockout", "Set...", "Add...", "Edit...",
		"Remove...", "Save...", "Page...", "Quit"};
	String[] setMenu = {"Shown Messages", "Shown Groups", "Shown Times",
		"Back"};
	String[] addMenu = {"Add Previous Time", "Add Group",
	"Import Old Files From...", "Back"};
	String[] editMenu = {"Edit Time", "Edit Group", "Back"};
	String[] removeMenu = {"Remove Time", "Remove Group", "Back"};
	String[] pageMenu = {"Messages Up", "Messages Down", "Groups Up",
		"Groups Down", "Times Up", "Times Down"};

	protected final TimeController controller;

	public HourTrackerConsole(TimeController controller){
		// get the terminal started
		startTerminal();
		// connect the controller
		this.controller = controller;
		this.controller.setView(this);
	}//end sole constructor

	public boolean startTerminal() {
		DefaultTerminalFactory defaultTerminalFactory =
		new DefaultTerminalFactory();
		try{
			terminal = defaultTerminalFactory.createTerminal();
			terminal.enterPrivateMode();
			terminal.clearScreen();
			terminal.setCursorVisible(false);
			graphics = terminal.newTextGraphics();
			terminalStarted = true;
		}//end trying to start terminal
		catch (IOException e){
			e.printStackTrace();
		}//end catching IOExceptions
		return terminalStarted;
	}//end startTerminal()

	public boolean stopTerminal(){
		try{
			terminal.close();
			graphics = null;
			terminal = null;
			terminalStarted = false;
		}//end trying to close the terminal
		catch (IOException e){
			e.printStackTrace();
		}//end catching IOExceptions
		return terminalStarted;
	}//end stopTerminal()

	/**
	 * Shows the main menu in addition to normal information.
	 */
	public void showMainMenu() {
		// displays normal info at top
		displayInfo();
		// display options
		displayOptions(mainMenu);
		// get a thing from the user
		int choice = getOption(mainMenu);
		graphics.putString(0,0,"You selected " + mainMenu[choice]);
	}//end showMainMenu()

	/**
	 * Displays the regular information on current stats and stuff
	 * on the terminal.
	 */
	private void displayInfo() {
		// render current time info
		updateTime();
		graphics.putString(1, getCurTimeRow() + 1, SEPARATOR);
		// render active group info
		updateActiveGroup(buildActiveGroup());
		graphics.putString(1, getActiveGroupRow()+1, SEPARATOR);
		// render messages
		updateMessages(buildMessages());
		graphics.putString(1, getMessageRow()+curMessageCount, SEPARATOR);
		// render groups
		updateGroups(buildGroups());
		graphics.putString(1, getGroupRow()+curGroupCount, SEPARATOR);
		// render times
		updateTimes(buildTimes());
		graphics.putString(1, getTimeRow()+curTimeCount, SEPARATOR);
	}//displayInfo()

	/**
	 * Displays a menu of options to the user in the specified part
	 * of the screen, and then also update the curOptionsCount.
	 * Should probable be called before getOption().
	 * @param menu The menu of options to display.
	 * @see #getOption(String[])
	 */
	private void displayOptions(String[] menu){
		int indexCounter = 0;
		for(String item : getChoiceText(menu)){
			graphics.putString(1, getOptionRow() + indexCounter, item);
			indexCounter++;
		}//end printing out each item in menu
		curOptionsCount = indexCounter;
	}//end displayOptions(menu)

	/**
	 * Given a menu of options, does all the necessary dialogue stuff
	 * in order to get the user to pick one of the options given.
	 * Currently doesn't do error checking so probably don't pass it
	 * an empty array. You should probably call displayOptions() first.
	 * @param menu The menu of options to choose from.
	 * @return Returns the index in the menu parameter of the option
	 * selected by the user.
	 * @see #displayOptions(String[])
	 */
	private int getOption(String[] menu){
		// return variable
		int index = -1;
		List<String>[] validOptions = getValidOptions(menu);
		// define some quick constants
		String promptForInput = "Please enter the letter or " +
		"number of the choice you wish to select.";
		String explainError = "Oops! Something went wrong. " +
		"Please try that again. Maybe it\'ll work this time.";
		String outsideValid = "Oops! that input is not valid.";
		String inputPref = ":) ";
		// loop variable
		boolean gotInput = false;
		while(!gotInput){
			int optionRow = getOptionRow();
			int inputRow = getInputRow();
			// put text prompting user for input
			graphics.putString(0, getInputRow(), promptForInput);
			// put the little :_ thing by where user should input
			//graphics.putString(0, getInputRow()+1, inputPref);
			// start actually trying to get input
			try {
				String input = getInput(getInputRow()+1, inputPref);
				for(int i = 0; i < validOptions.length; i++){
					for(String option : validOptions[i]){
						if(option.toLowerCase().equals(input.toLowerCase())){
							index = i;
							gotInput = true;
						}//end if this option is valid
					}//end looping over options for each choice
				}//end looping over options for each thing
				if(!gotInput){
					graphics.putString(0, getInputRow(), outsideValid);
					try{
						terminal.bell();
						Thread.sleep(3000);
					} catch (InterruptedException | IOException e){
						e.printStackTrace();
					}//end catching exceptions we don't cate about
				}//end if we still haven't gotten input yet
			}//end trying to get input from the user
			catch (IOException e) {
				e.printStackTrace();
				graphics.putString(0, getInputRow(), explainError);
				try {
					terminal.bell();
					Thread.sleep(3000);
				} catch (InterruptedException | IOException e1) {
					e1.printStackTrace();
				}//end catching InterruptedException
			}//end catching IOExceptions
		}//end looping whil we still need input from user
		return index;
	}//end getOption(menu)

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
			if(curIndex >= choices.length) break;
			choiceText.add(a + ") " + choices[curIndex]);
		}//end looping over choices
		return choiceText;
	}//end getChoiceText

	String lastTime = "";
	public void updateCurrentTime(String[] times) {
		// Generate the actual string from the parameter to display
		StringBuilder sb = new StringBuilder();
		for(String timeComponent : times) {
			sb.append(timeComponent);
			sb.append(" | ");
		}//end building time components together
		if(sb.length() > 0) sb.setLength(sb.length() - 3);
		lastTime = sb.toString();
		// Actually render the text to the screen
		graphics.putString(1, getCurTimeRow(), lastTime);
	}//end updateCurrentTime(times)
	
	String lastActiveGroup = "";
	public void updateActiveGroup(String[] groupStats) {
		// Generate the actual string from the parameter to display
		StringBuilder sb = new StringBuilder();
		for(String groupStat : groupStats) {
			sb.append(groupStat).append(" | ");
		}//end building group stats together
		if(sb.length() > 0) {sb.setLength(sb.length() - 3);}
		lastActiveGroup = "";
		// actually render the text to the screen
		graphics.putString(1, getGroupRow(), lastActiveGroup);
	}//end of updateActiveGroup(groupStats)

	protected String[] buildActiveGroup(){
		String[] groupInfo = new String[3];
		groupInfo[0] = "Active Group";
		groupInfo[1] = "Time Count";
		groupInfo[2] = "Total Time";
		return groupInfo;
	}//end buildActiveGroup()
	
	List<String> lastMessages = new ArrayList<String>();
	public void updateMessages(List<String> messages) {
		if(messages == null) return;
		// Generate the actual string from the parameter to display
		lastMessages = messages;
		curMessageCount = messages.size();
		// actually render the text to the screen
		for(int i = 0; i < curMessageCount; i++){
			int col = i + getMessageRow();
			graphics.putString(1, col, messages.get(i));
		}//end looping over the messages
	}//end updateMessages()

	protected List<String> buildMessages(){
		List<String> messages = new ArrayList<String>();
		int messageCount = 0;
		int messageLimit = 5;
		for(int i = messages.size() - 1;
		messageCount < messageLimit && i > 0; i--){
			messages.add(savedMessages.get(i) + "\n");
			messageCount++;
		}//end looping over messages
		if(messageCount == 0){
			messages.add("No Messages Yet\n");
		}//end if there are no messags yet.
		return messages;
	}//end buildMessages()

	List<String> lastGroups = new ArrayList<String>();
	public void updateGroups(List<String> groups) {
		// Generate the actual string from the parameter to display
		lastGroups = groups;
		curGroupCount = groups.size();
		// actually render the text to the screen
		for(int i = 0; i < curGroupCount; i++) {
			int col = i + getGroupRow();
			graphics.putString(1, col, groups.get(i));
		}//end rendering each group to the screen
	}//end updateGroups(groups)

	protected List<String> buildGroups(){
		List<String> groups = new ArrayList<String>();
		int groupCount = 0;
		int groupLimit = 4;
		List<TimeGrouping> timeGroupings = controller.getGroups();
		StringBuilder sb = new StringBuilder();
		for(int i = timeGroupings.size() - 1;
		groupCount < groupLimit && i > 0; i--){
			sb.append(timeGroupings.get(i).getName());
			sb.append(" | ");
			sb.append(timeGroupings.get(i).getTimeCount());
			sb.append(" Times | ");
			sb.append(timeGroupings.get(i).getTotalTime());
			sb.append("\n");
			groups.add(sb.toString());
			sb.setLength(0);
		}//end looping over groups
		if(groups.size() == 0){
			groups.add("No Groups Yet\n");
		}//end if there are no groups yet
		return groups;
	}//end buildGroups()

	List<String> lastTimes = new ArrayList<String>();
	public void updateTimes(List<String> times) {
		// Generate the actual string from the parameter to display
		lastTimes = times;
		curTimeCount = times.size();
		// actually render the text to the screen
		for(int i = 0; i < curTimeCount; i++) {
			int col = i + getTimeRow();
			graphics.putString(1, col, times.get(i));
		}//end rendering each time to the screen
	}//end updateTimes(times)

	protected List<String> buildTimes(){
		List<String> lines = new ArrayList<String>();
		List<TimeGrouping> timeGroupings = controller.getGroups();
		int timeCount = 0;
		int timeLimit = 6;
		StringBuilder sb = new StringBuilder();
		for(int i = timeGroupings.size() - 1;
		timeCount < timeLimit && i > 0; i++){
			List<TimedInstance> times = timeGroupings.get(i).getTimes();
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
				lines.add(sb.toString());
				sb.setLength(0);
			}//end looping over times in group
		}//end looping over groups
		if(timeCount == 0){
			lines.add("No Timed Instances Yet\n");
		}//end if there are no times yet
		return lines;
	}//end buildTimes()

	public int getUserChoice(String[] options) {
		// Generate the actual string from the parameter to display
		return 0;
	}//end getUserChoice(options)

	public TimedInstance editTimedInstance(TimedInstance timeToEdit,
	boolean allowCancellation, Field[] fieldsToEdit) {
		// TODO Auto-generated method stub
		return null;
	}//end editTimedInstance(timeToEdit, allowCancellation, fieldsToEdit)

	public TimeGrouping editTimeGrouping(TimeGrouping groupToEdit,
	boolean allowCancellation, Field[] fieldsToEdit) {
		// TODO Auto-generated method stub
		return null;
	}//end editTimeGrouping(groupToEdit, allowCancellation, fieldsToEdit)
	
	@Override
	public void refreshView() {
		this.showMainMenu();
	}//end refreshView()
	
	@Override
	public void updateTime() {
		String[] timeInfo = new String[3];
		timeInfo[0] = Instant.now().toString();
		timeInfo[1] = controller.getClockedTime().toString();
		timeInfo[2] = controller.getProjectedGroupTotalTime().toString();
		this.updateCurrentTime(timeInfo);
	}//end updateTime()
	
	@Override
	public String getSelectedGroupName() {
		// TODO Auto-generated method stub
		return "Ungrouped";
	}//end getSelectedGroupName()
	
	@Override
	public String getCurrentInstanceName() {
		// TODO Auto-generated method stub
		return null;
	}//end getCurrentInstanceName()
	
	/**
	 * @deprecated
	 */
	@Override
	public String getPathWithMessage(String message) {
		return null;
	}//end getPathWithMessage(message)
	
	@Override
	public TimedInstance editInstance(TimedInstance instance) {
		// TODO Auto-generated method stub
		return null;
	}//end editInstance(instance)
	
	@Override
	public TimeGrouping editGroup(TimeGrouping group) {
		// TODO Auto-generated method stub
		return null;
	}//end editGroup(group)
	
	/**
	 * Gets a string from the user. Hopefully works. Doesn't write anything,
	 * instead it just reads input.
	 * @return String given by user
	 * @throws IOException if underlying I/O error occurs
	 */
	public String getInput(int rowNum, String rowStart) throws IOException{
		terminal.setCursorVisible(true);
		StringBuilder full = new StringBuilder();
		graphics.putString(0, rowNum, rowStart);
		KeyStroke keyStroke = terminal.readInput();
		while(keyStroke.getKeyType() != KeyType.Enter){
			if(keyStroke.getKeyType() == KeyType.Character){
				full.append(keyStroke.getCharacter());
			}//end if user typed a character
			else if(keyStroke.getKeyType() == KeyType.Backspace){
				if(full.length() > 0){
					full.setLength(full.length() - 1);
					// erase prior section
					graphics.putString(0, rowNum, rowStart + full + " ");
				}//end if length greater than one
				else{
					terminal.bell();
				}//end else nothing there already
			}//end if user wants to go back a space
			graphics.putString(0, rowNum, rowStart + full);
			keyStroke = terminal.readInput();
		}//end trying to get user input
		terminal.setCursorVisible(false);
		return full.toString();
	}//end getInput()

	@Override
	public void displayMessage(String message) {
		logMessage(message);
	}//end displayMessage()
	
	@Override
	public boolean confirmationMessage(String message) {
		// TODO Auto-generated method stub
		return true;
	}//end confirmationMessage(message)
	
	@Override
	public void logMessage(String message) {
		savedMessages.add(message);
		updateMessages(buildMessages());
	}//end logmessage(message)

}//end class HourTrackerConsole
