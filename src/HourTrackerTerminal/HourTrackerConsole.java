package HourTrackerTerminal;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.*;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.*;
import com.googlecode.lanterna.input.*;
import com.googlecode.lanterna.screen.*;
import com.googlecode.lanterna.terminal.*;

import HourTrackerLibrary.TimeGrouping;
import HourTrackerLibrary.TimedInstance;

/**
 * This class basically handles all
 * the console stuff in order to make it easier for the View class
 * to do consoele stuff easily.
 */
public class HourTrackerConsole implements IHourTerminal {
	Terminal terminal = null;

	@Override
	public boolean startTerminal() {
		// TODO Auto-generated method stub
		return false;
	}//end startTerminal()

	@Override
	public boolean stopTerminal(){
		// TODO Auto-generated method stub
		return false;
	}//end stopTerminal()

	@Override
	public void showMainMenu() {
		// TODO Auto-generated method stub
		
	}//end showMainMenu()

	@Override
	public void updateCurrentTime(String[] times) {
		// TODO Auto-generated method stub
		
	}//end updateCurrentTime(times)

	@Override
	public void updateActiveGroup(String[] groupStats) {
		// TODO Auto-generated method stub
		
	}//end of updateActiveGroup(groupStats)

	@Override
	public void updateMessages(List<String> messages) {
		// TODO Auto-generated method stub
		
	}//end updateMessages()

	@Override
	public void updateGroups(List<String> groups) {
		// TODO Auto-generated method stub
		
	}//end updateGroups(groups)

	@Override
	public void updateTimes(List<String> times) {
		// TODO Auto-generated method stub
		
	}//end updateTimes(times)

	@Override
	public int getUserChoice(String[] options) {
		// TODO Auto-generated method stub
		return 0;
	}//end getUserChoice(options)

	@Override
	public TimedInstance editTimedInstance(TimedInstance timeToEdit,
	boolean allowCancellation, Field[] fieldsToEdit) {
		// TODO Auto-generated method stub
		return null;
	}//end editTimedInstance(timeToEdit, allowCancellation, fieldsToEdit)

	@Override
	public TimeGrouping editTimeGrouping(TimeGrouping groupToEdit,
	boolean allowCancellation, Field[] fieldsToEdit) {
		// TODO Auto-generated method stub
		return null;
	}//end editTimeGrouping(groupToEdit, allowCancellation, fieldsToEdit)

}//end class HourTrackerConsole
