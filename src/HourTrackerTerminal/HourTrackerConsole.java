package HourTrackerTerminal;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.lanterna.graphics.TextGraphics;
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
	boolean terminalStarted = false;
	protected static final String SEPARATOR =
        "-:-:-:-:-:-:-:-:-:-:-:" +
        "-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-:-";

	protected final TimeController controller;

	public HourTrackerConsole(TimeController controller){
		this.controller = controller;
		this.controller.setView(this);
		startTerminal();
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

	public void showMainMenu() {
		// render current time info
		updateTime();
		graphics.putString(1, getCurTimeRow() + 1, SEPARATOR);
		// render active group info
		updateActiveGroup(buildActiveGroup());
		graphics.putString(1, getGroupRow() + 1, SEPARATOR);
		// render messages
		updateMessages(buildMessages());
		graphics.putString(1, getMessageRow()+curMessageCount+1, SEPARATOR);
		// render groups
		updateGroups(buildGroups());
		graphics.putString(1, getGroupRow()+curGroupCount+1, SEPARATOR);
		// render times
		updateTimes(buildTimes());
		graphics.putString(1, getTimeRow()+curTimeCount+1, SEPARATOR);
	}//end showMainMenu()

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
		// Generate the actual string from the parameter to display
		lastMessages = messages;
		curMessageCount = messages.size();
		// actually render the text to the screen
		for(int i = 0; i < curMessageCount; i++){
			graphics.putString(1, i + getMessageRow(), messages.get(i));
		}//end looping over the messages
	}//end updateMessages()

	protected List<String> buildMessages(){
		List<String> messages = new ArrayList<String>();
		int messageCount = 0;
		int messageLimit = 5;
		for(int i = messages.size() - 1;
		messageCount < messageLimit && i > 0; i--){
			messages.add(messages.get(i) + "\n");
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
			graphics.putString(1, i + getGroupRow(), groups.get(i));
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
			graphics.putString(1, i + getTimeRow(), times.get(i));
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
	
	@Override
	public String getPathWithMessage(String message) {
		// TODO Auto-generated method stub
		return "/home/nicholas/Documents/Programming Stuff/Mischellanious Projects/HourTracker/HourTracker/bin/FileStorage";
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
	
	@Override
	public void displayMessage(String message) {
		// TODO Auto-generated method stub
		
	}//end displayMessage()
	
	@Override
	public boolean confirmationMessage(String message) {
		// TODO Auto-generated method stub
		return true;
	}//end confirmationMessage(message)
	
	@Override
	public void logMessage(String message) {
		// TODO Auto-generated method stub
		
	}//end logmessage(message)

}//end class HourTrackerConsole
