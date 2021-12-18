package HourTrackerTerminal;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.googlecode.lanterna.TerminalPosition;
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
	int maxMessages = 4;
	int maxGroups = 4;
	int maxTimes = 4;
	int messageOffset = 0;
	int groupOffset = 0;
	int timesOffset = 0;
	/**
	 * The index in the controller's group list
	 * of the group we consider active. No active
	 * group if this is set to -1.
	 */
	int activeGroupIndex = -1;
	int getCurTimeRow(){return 1;}
	int getActiveGroupRow(){
		if(activeGroupIndex != -1) return getCurTimeRow() + 1 + 1;
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
		"Active Group","Back"};
	String[] addMenu = {"Add Previous Time", "Add Group",
	"Import Old Files From...", "Back"};
	String[] editMenu = {"Edit Time", "Edit Group", "Back"};
	String[] removeMenu = {"Remove Time", "Remove Group", "Back"};
	String[] pageMenu = {"Messages Up", "Messages Down", "Groups Up",
		"Groups Down", "Times Up", "Times Down", "Back"};
	DateTimeFormatter clockFormat = DateTimeFormatter.ofPattern("h:mm:ss a");
	private String formatDuration(Duration d){
		return String.format("%d:%02d:%02d",d.toHours(),
		d.toMinutesPart(),d.toSecondsPart());
	}//end formatDuration(d)
	DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("uu/MM/d EEE");
	/**
	 * The controller for this class. Holds all our data too.
	 */
	protected final TimeController controller;
	/**
	 * Enum for menu state.
	 */
	protected enum MenuState{
		/**
		 * The main menu, with "main" controlls.
		 */
		MainMenu,
		/**
		 * The set menu, for changing certain settings.
		 */
		SetMenu,
		/**
		 * The add menu, for adding things into the application.
		 */
		AddMenu,
		/**
		 * The edit menu, for editing things in the application.
		 */
		EditMenu,
		/**
		 * The remove menu, for removing things in the application.
		 */
		RemoveMenu,
		/**
		 * The page menu, for controller pagination settings.
		 */
		PageMenu,
		/**
		 * A placeholder option for some other menu type.
		 */
		Other
	}//end enum MenuState
	/**
	 * The current (or at least most recently updated) menu
	 * state for this view.
	 */
	protected MenuState currentState = MenuState.MainMenu;

	/**
	 * Sole constructor for this class.
	 * @param controller Controller for this class.
	 */
	public HourTrackerConsole(TimeController controller){
		// get the terminal started
		startTerminal();
		// connect the controller
		this.controller = controller;
		this.controller.setView(this);
	}//end sole constructor

	/**
	 * Starts the terminal. and enters private mode,
	 * effectively starting the application.
	 * @return
	 */
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

	/**
	 * Starts the main application loop
	 */
	public void startApplicationLoop(){
		boolean continueLooping = true;
		while(continueLooping){
			currentState = MenuState.MainMenu;
			menuDecisionMatrix(-1);
		}//end looping indefinetly
	}//end startApplicationLoop()

	/**
	 * Stops teh terminal and exits private mode.
	 * @return
	 */
	public boolean stopTerminal(){
		try{
			terminal.clearScreen();
			terminal.exitPrivateMode();
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
	 * Clears the line at the specified row.
	 * @param rowNum
	 * @return
	 */
	private boolean clearLine(int rowNum){
		try {
			int cols = terminal.getTerminalSize().getColumns();
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < cols; i++){
				sb.append(" ");
			}//end adding cols spaces to sb
			graphics.putString(0,rowNum,sb.toString());
			return true;
		} catch (IOException e) {return false;} 
	}//end clearLine(rowNum)

	/**
	 * Changes menus based on the choice given to the current
	 * menu state. To re-interpret the current menu state, pass
	 * -1 to menuChoice,
	 * @param menuChoice The choice given by the user for the current
	 * menu. If you want to reinterpret current menu, pass -1.
	 */
	public void menuDecisionMatrix(int menuChoice){
		switch(currentState){
			case MainMenu:
				if(menuChoice == -1){
					try {
						terminal.clearScreen();
					} catch (IOException e) {}
					menuChoice = showMenu(mainMenu);
				}//end if we need to reinterpret this state
				// figure out what to do based on option
				switch(menuChoice){
					case 0: // clock in/out
						if(controller.getCurrentlyClocked()){
							controller.clockOut();
						}//end if we should clock out
						else{
							controller.clockIn();
						}//end else we should clock in
						break;
					case 1: // set...
						currentState = MenuState.SetMenu;
						menuDecisionMatrix(-1);
						break;
					case 2: // add...
						currentState = MenuState.AddMenu;
						menuDecisionMatrix(-1);
						break;
					case 3: // edit...
						currentState = MenuState.EditMenu;
						menuDecisionMatrix(-1);
						break;
					case 4: // remove...
						currentState = MenuState.RemoveMenu;
						menuDecisionMatrix(-1);
						break;
					case 5: // save...
						controller.saveCurrentState();
						menuDecisionMatrix(-1);
						break;
					case 6: // page...
						currentState = MenuState.PageMenu;
						menuDecisionMatrix(-1);
						break;
					case 7: // quit
						boolean save = confirmationMessage("Would you like " +
						"to save before quitting?");
						if(save){
							controller.saveCurrentState();
						}//end if we should save first
						// stop the terminal
						stopTerminal();
						// exit program if we can
						System.exit(0);
						break;
				}//end switching based on menu choice
				break;
			case SetMenu:
				if(menuChoice == -1){
					try {
						terminal.clearScreen();
					} catch (IOException e) {}
					menuChoice = showMenu(setMenu);
				}//end if we need to reinterpret this state
				// figure out what to do based on option
				int numberOfMessages = -1;
				switch(menuChoice){
					case 0: // shown messages
						do{
							numberOfMessages = maxMessages;
							String promptForNewNum = "Current number of "+
							"messages shown is "+numberOfMessages+". It "+
							"needs to be greater than 0. What do you "+
							"want to set it to?";
							numberOfMessages = getInt(promptForNewNum);
						} while(numberOfMessages < 1);
						// update setting
						maxMessages = numberOfMessages;
						// go ahead and move on to next menu
						currentState = MenuState.SetMenu;
						menuDecisionMatrix(-1);
						break;
					case 1: // shown groups
						do{
							numberOfMessages = maxGroups;
							String promptForNewNum = "Current number of "+
							"groups shown is "+numberOfMessages+". It "+
							"needs to be greater than 0. What do you "+
							"want to set it to?";
							numberOfMessages = getInt(promptForNewNum);
						} while(numberOfMessages < 1);
						// update setting
						maxGroups = numberOfMessages;
						// go ahead and move on to next menu
						currentState = MenuState.SetMenu;
						menuDecisionMatrix(-1);
						break;
						case 2: // shown times
						do{
							numberOfMessages = maxTimes;
							String promptForNewNum = "Current number of "+
							"times shown is "+numberOfMessages+". It "+
							"needs to be greater than 0. What do you "+
							"want to set it to?";
							numberOfMessages = getInt(promptForNewNum);
						} while(numberOfMessages < 1);
						// update setting
						maxTimes = numberOfMessages;
						// go ahead and move on to next menu
						currentState = MenuState.SetMenu;
						menuDecisionMatrix(-1);
						break;
					case 3: // active group
						String groupName = getSelectedGroupName();
						if(!controller.containsGroup(groupName)){
							controller.addGroup(groupName);
						}//end if group already exists
						int activeIndex = -1;
						for(int i = 0; i < controller.getGroups().size(); i++){
							if(controller.getGroup(i).getName()
							.equals(groupName)){
								activeIndex = i;
								break;
							}//end if we found index
						}//end looking for index of groupName
						activeGroupIndex = activeIndex;
						controller.setActiveGroupIndex(activeGroupIndex);
						break;
					default:
						break;
				}//end switching based on menu choice
				break;
			case AddMenu:
				if(menuChoice == -1){
					try {
						terminal.clearScreen();
					} catch (IOException e) {}
					menuChoice = showMenu(addMenu);
				}//end if we need to reinterpret this state
				// figure out what to do based on option
				switch(menuChoice){
					case 0: // add previous time
					// TODO: Implement functionallity for adding times
						break;
					case 1: // add group
						controller.addGroup();
						break;
					case 2: // import old files from...
					// TODO: Allow older files to be imported.
						break;
					case 3: // back
						//currentState = MenuState.MainMenu;
						// do nothing to cancel out recursion
						//menuDecisionMatrix(-1);
						break;
				}//end switching based on menu choice
				break;
			case EditMenu:
				if(menuChoice == -1){
					try {
						terminal.clearScreen();
					} catch (IOException e) {}
					menuChoice = showMenu(editMenu);
				}//end if we need to reinterpret this state
				// figure out what to do based on option
				switch(menuChoice){
					case 0: // edit time
					// TODO: Implement editing functionallity
						break;
					case 1: // edit group
					// TODO: Implement editing functionallity
						break;
					case 2: // back
						//currentState = MenuState.MainMenu;
						// do nothing to cancel out recursion
						//menuDecisionMatrix(-1);
						break;
				}//end switching based on menu choice
				break;
			case RemoveMenu:
				if(menuChoice == -1){
					try {
						terminal.clearScreen();
					} catch (IOException e) {}
					menuChoice = showMenu(removeMenu);
				}//end if we need to reinterpret this state];
				// figure out what to do based on option
				switch(menuChoice){
					case 0: // remove time
						if(controller.getTimes().size() < 1){
							displayMessage("There are no times " +
							"to remove.");
						}//end if there are no times
						else{
							String[] groups = new String[controller.getGroups().size()];
							for(int i = 0; i < groups.length; i++){
								groups[i] = controller.getGroup(i).getName();
							}//end adding group names to groups
							// clear the screen
							try {terminal.clearScreen();} catch (IOException e) {}
							int groupIndex = showMenu(groups);
							TimeGrouping group = controller.getGroup(groupIndex);
							String[] times = new String[group.getTimeCount()];
							for(int i = 0; i < times.length; i++){
								times[i] = group.getTime(i).toString();
							}//end adding timed instances to times
							// clear the screen
							try {terminal.clearScreen();} catch (IOException e) {}
							int timeIndex = showMenu(times);
							controller.removeTime(groupIndex, timeIndex);
						}//end else there are times
						break;
					case 1: // remove group
						if(controller.getGroups().size() < 1){
							displayMessage("There are no groups "+
							"to remove.");
						}//end if there are no groups
						else{
							String[] groups = new String[controller.getGroups().size()];
							for(int i = 0; i < groups.length; i++){
								groups[i] = controller.getGroup(i).getName();
							}//end adding group names to groups
							// clear the screen
							try {terminal.clearScreen();} catch (IOException e) {}
							int groupIndex = showMenu(groups);
							controller.removeGroup(groupIndex);
						}//end else there is at least one group
						break;
					case 2: // back
						//currentState = MenuState.MainMenu;
						// do nothing to cancel out recursion
						//menuDecisionMatrix(-1);
						break;
				}//end switching based on menu choice
				break;
			case PageMenu:
				if(menuChoice == -1){
					try {
						terminal.clearScreen();
					} catch (IOException e) {}
					menuChoice = showMenu(pageMenu);
				}//end if we need to reinterpret this state
				// figure out what to do based on option
				switch(menuChoice){
					case 0: // messages up
						if(messageOffset < maxMessages){
							messageOffset++;
						}//end if offset less than max
						menuDecisionMatrix(-1);
						break;
					case 1: // messages down
						if(messageOffset > 0){
							messageOffset--;
						}//end if offset greater than 0
						menuDecisionMatrix(-1);
						break;
					case 2: // groups up
						if(groupOffset < maxGroups){
							groupOffset++;
						}//end if offset less than max
						menuDecisionMatrix(-1);
						break;
					case 3: // groups down
						if(groupOffset > 0){
							groupOffset--;
						}//end if offset greater than 0
						menuDecisionMatrix(-1);
						break;
					case 4: // times up
						if(timesOffset < maxTimes){
							timesOffset++;
						}//end if offset less than max
						menuDecisionMatrix(-1);
						break;
					case 5: // times down
						if(timesOffset > 0){
							timesOffset--;
						}//end if offset greater than 0
						menuDecisionMatrix(-1);
						break;
					default:
						break;
				}//end switching based on menu choice
				break;
			default:
				break;
		}//end switching based on state
	}//end menuDecisionMatrix(menuChoice)

	/**
	 * Gets an integer from the user.
	 * @return Returns the integer given by the user.
	 */
	protected int getInt(String message){
		int chosenInt = -1;
		boolean gotResponse = false;
		while(!gotResponse){
			try{
				clearLine(getInputRow());
				graphics.putString(0, getInputRow(), message);
				String response = getInput(getInputRow()+1, ":) ");
				chosenInt = Integer.parseInt(response);
				// if we got here, we didn't get caught
				gotResponse = true;
			} catch(IOException e){}
			catch(NumberFormatException e){
				clearLine(getInputRow());
				graphics.putString(0, getInputRow(),
				"That wasn't very integer of you...");
				try{
					Thread.sleep(3000);
				} catch(InterruptedException e1){}
			}//end catching NumberFormatExceptions
		}//end looping while we don't have response yet
		return chosenInt;
	}//end getInt()

	/**
	 * The generic method for displaying a menu of string options,
	 * and then getting one of those back from the user.
	 * @param menu The menu to be displayed.
	 * @return Returns the index in the menu of the option chosen
	 * by the user.
	 */
	public int showMenu(String[] menu){
		// displays normal info at top
		displayInfo();
		// display options
		displayOptions(menu);
		// get a thing from the user
		return getOption(mainMenu);
	}//end showMenu(menu)

	/**
	 * Displays the regular information on current stats and stuff
	 * on the terminal. Good to update before getting input.
	 */
	private void displayInfo() {
		// render current time info
		updateTime();
		graphics.putString(1, getCurTimeRow() + 1, SEPARATOR);
		// render active group info
		if(activeGroupIndex != -1){
			updateActiveGroup(buildActiveGroup());
			graphics.putString(1, getActiveGroupRow()+1, SEPARATOR);
		}//end if there actually is a group selected rn
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
	protected void displayOptions(String[] menu){
		int indexCounter = 0;
		for(String item : getChoiceText(menu)){
			int row = getOptionRow() + indexCounter;
			clearLine(row);
			graphics.putString(1, row, item);
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
	protected int getOption(String[] menu){
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
			// put text prompting user for input
			clearLine(getInputRow());
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
					clearLine(getInputRow());
					graphics.putString(0, getInputRow(), outsideValid);
					try{
						terminal.bell();
						Thread.sleep(3000);
					} catch (InterruptedException | IOException e){
						//e.printStackTrace();
					}//end catching exceptions we don't cate about
				}//end if we still haven't gotten input yet
			}//end trying to get input from the user
			catch (IOException e) {
				clearLine(getInputRow());
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
	 * given choice. Each index corresponds to a list of Strings.
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
		clearLine(getCurTimeRow());
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
		lastActiveGroup = sb.toString();
		// actually render the text to the screen
		clearLine(getActiveGroupRow());
		graphics.putString(1, getActiveGroupRow(), lastActiveGroup);
	}//end of updateActiveGroup(groupStats)

	protected String[] buildActiveGroup(){
		String[] groupInfo = new String[3];
		if(activeGroupIndex == -1){
			groupInfo[0] = "Active Group";
			groupInfo[1] = "Time Count";
			groupInfo[2] = "Total Time";
		}//end if we should just set default
		else{
			TimeGrouping active = controller.getGroup(activeGroupIndex);
			groupInfo[0] = active.getName();
			groupInfo[1] = Integer.toString(active.getTimeCount());
			groupInfo[2] = formatDuration(active.getTotalTime());
		}//end else we should populate actual data
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
			clearLine(col);
			graphics.putString(1, col, messages.get(i));
		}//end looping over the messages
	}//end updateMessages()

	protected List<String> buildMessages(){
		List<String> messages = new ArrayList<String>();
		int messageCount = 0;
		for(int i = savedMessages.size() - 1 - messageOffset;
		messageCount < maxMessages && i >= 0; i--){
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
			clearLine(col);
			graphics.putString(1, col, groups.get(i));
		}//end rendering each group to the screen
	}//end updateGroups(groups)

	protected List<String> buildGroups(){
		List<String> groups = new ArrayList<String>();
		int groupCount = 0;
		List<TimeGrouping> timeGroupings = controller.getGroups();
		StringBuilder sb = new StringBuilder();
		for(int i = timeGroupings.size() - 1 - groupOffset;
		groupCount < maxGroups && i >= 0; i--){
			sb.append(timeGroupings.get(i).getName());
			sb.append(" | ");
			sb.append(timeGroupings.get(i).getTimeCount());
			sb.append(" Times | ");
			sb.append(formatDuration(timeGroupings.get(i).getTotalTime()));
			sb.append("\n");
			groups.add(sb.toString());
			sb.setLength(0);
			groupCount++;
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
			clearLine(col);
			graphics.putString(1, col, times.get(i));
		}//end rendering each time to the screen
	}//end updateTimes(times)

	protected List<String> buildTimes(){
		List<String> lines = new ArrayList<String>();
		List<TimeGrouping> timeGroupings = controller.getGroups();
		int timeCount = 0;
		StringBuilder sb = new StringBuilder();
		int tempOffset = timesOffset;
		for(int i = timeGroupings.size() - 1;
		timeCount < maxTimes && i >= 0; i--){
			List<TimedInstance> times = timeGroupings.get(i).getTimes();
			for(int j = times.size() - 1 - tempOffset;
			timeCount < maxTimes && j >= 0; j--){
				TimedInstance time = times.get(j);
				sb.append(time.getName());
				sb.append(" | ");
				// add start and end time
				if(time.getHandleSpecificBeginEnd()){
					sb.append(clockFormat.format(time.getStart()));
					sb.append(" - ");
					sb.append(clockFormat.format(time.getEnd()));
					sb.append(" | ");
				}//end if we should have specific start and end
				// add duration
				sb.append(formatDuration(time.getDuration()));
				// add date
				if(time.getHandleDate()){
					sb.append(" | ");
					String date1 = time.getStart().format(dateFormat);
					String date2 = time.getEnd().format(dateFormat);
					if(date1.equals(date2)){
						sb.append(date1);
					}//end if dates are equal
					else{
						sb.append(date1 + " - " + date2);
					}//end else we should show date span
				}//end if we should include date
				sb.append(" | " + time.getCurrentGroupName());
				lines.add(sb.toString());
				sb.setLength(0);
				timeCount++;
			}//end looping over times in group
			tempOffset -= times.size();
			if(tempOffset < 0) tempOffset = 0;
		}//end looping over groups
		if(timeCount == 0){
			lines.add("No Timed Instances Yet\n");
		}//end if there are no times yet
		return lines;
	}//end buildTimes()

	public int getUserChoice(String[] options) {
		// refresh regular info
		displayInfo();
		// display options
		displayOptions(options);
		// get option from user
		return getOption(options);
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
		displayInfo();
	}//end refreshView()
	
	@Override
	public void updateTime() {
		try {
			// save previous cursor position
			TerminalPosition cursor = terminal.getCursorPosition();
			terminal.setCursorVisible(false);
			// build array of values to display
			String[] timeInfo = new String[3];
			timeInfo[0] = LocalTime.now().format(this.clockFormat);
			timeInfo[1] = formatDuration(controller.getClockedTime());
			timeInfo[2] = formatDuration(controller
			.getProjectedGroupTotalTime());
			// actually display the update
			this.updateCurrentTime(timeInfo);
			terminal.setCursorVisible(true);
			// reset cursor position
			terminal.setCursorPosition(cursor);
		} catch (IOException e) {}
	}//end updateTime()
	
	@Override
	public String getSelectedGroupName() {
		String groupName = "";
		clearLine(getInputRow());
		String promptForGroupName = "Please enter the name of a group.";
		Set<String> groupNames = new HashSet<String>();
		for(TimeGrouping group : controller.getGroups()){
			groupNames.add(group.getName());
		}//end adding names to groupNames
		while(groupName.equals("")){
			graphics.putString(0, getInputRow(), promptForGroupName);
			try {
				groupName = getInput(getInputRow()+1,":) ");
			} catch (IOException e) {}
			if(!groupNames.contains(groupName)){
				String promptForGroupCreation = "You are about to make "+
				"a new group called "+groupName+". Is that okay?";
				boolean createGroup =
				confirmationMessage(promptForGroupCreation);
				clearLine(getInputRow());
				if(!createGroup){
					groupName = "";
				}//end if we want to prevent making a group
			}//end if making new group
		}//end looping while we still need a groupName
		
		return groupName;
	}//end getSelectedGroupName()
	
	/**
	 * Gets the name of an instance. Returns null if an IOException
	 * occured.
	 */
	@Override
	public String getCurrentInstanceName() {
		String promptForInput = "Please enter the name for " +
		"a time instance.";
		clearLine(getInputRow());
		graphics.putString(0, getInputRow(), promptForInput);
		try {
			return getInput(getInputRow()+1, ":) ");
		} catch (IOException e) {
			return null;
		}//end catching IO errors
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
		clearLine(rowNum);
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
		String response = "";
		boolean confirmation = false;
		String[] yes = {"yes", "y", "true", "t"};
		String[] no = {"no", "n", "false", "f"};
		while(response == ""){
			clearLine(getInputRow());
			graphics.putString(0,getInputRow(), message);
			try{
				// try to get y or n from user
				response = getInput(getInputRow()+1, "(Y/n): ");
				if(Arrays.asList(yes).contains(response.toLowerCase())){
					confirmation = true;
				}//end if response was yes
				else if(Arrays.asList(no).contains(response.toLowerCase())){
					confirmation = false;
				}//end if response was no
				else{
					clearLine(getInputRow());
					graphics.putString(0,getInputRow(),"Unrecognized "+
					"input, please only write yes or no, y or n, etc.");
					try{Thread.sleep(3000);}catch(InterruptedException e){}
					response = "";
				}//end else response invalid
			}//end trying to get input
			catch(IOException e){
				// don't do anything, basically
			}//end catching IOExceptions
		}//end looping while response is invalid
		return confirmation;
	}//end confirmationMessage(message)
	
	@Override
	public void logMessage(String message) {
		savedMessages.add(message);
		updateMessages(buildMessages());
	}//end logmessage(message)

}//end class HourTrackerConsole
