package HourTrackerLibrary;

import java.time.*;

import javax.naming.OperationNotSupportedException;

/*
Stuff we'll drop down here in a sec
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.IOException;
*/

/**
 * 
 */
public class TimeController {
	private Instant clockInTime = Instant.now();
	private boolean currentlyClocked = false;
	public boolean getCurrentlyClocked(){
		return currentlyClocked;
	}//end getCurrentlyClocked()
	
	private TimeGroupManager groupManager = new TimeGroupManager();
	
	private int curGroupIndex = -1;
	public String getCurrentGroupName(){
		try{
			return groupManager.getGroups().get(curGroupIndex).getName();
		}//end trying to return current group name
		catch (ArrayIndexOutOfBoundsException e){
			return "Ungrouped";
		}//end if curGroupIndex is wrong
	}//end getCurrentGroupName()
	public TimeView view;
	
	/**
	 * Initializes this controller object with the view it should be
	 * yeeting its method calls over to.
	 * @param view The view that should be associated with
	 * this controller.
	 */
	public TimeController(TimeView view){
		this.view = view;
	}//end no-arg constructor

	/**
	 * Method that should be called when the user clocks in.
	 */
	public void clockIn(){
		clockInTime = Instant.now();
		this.currentlyClocked = true;
	}//end clockIn()

	public void clockOut(){
		// save the new time
		Instant clockOutTime = Instant.now();
		TimedInstance time = new TimedInstance(clockInTime, clockOutTime);
		// get name for that time
		time.name = view.getCurrentInstanceName();
		// add time to groupManager
		groupManager.addTime(time, view.getSelectedGroupName());
		// update stuff
		currentlyClocked = false;
		view.refreshView();
	}//end clockOut()

	// TODO: Add methods for efficiently handling ticking while clocked

	/**
	 * Adds a time that has been manually entered in by a user. Sets
	 * handleSpecificBeginEnd to true.
	 * @param start The beginning of the time.
	 * @param end The end of the time.
	 * @param handleDate Whether or not we should worry about the date that
	 * the instance allegedly took place in favor of just using raw time
	 * of day.
	 */
	public void addPreviousTime(Instant start, Instant end,
	boolean handleDate){
		TimedInstance prevTime = new TimedInstance(start, end);
		prevTime.setHandleSpecificBeginEnd(true);
		prevTime.setHandleDate(handleDate);
		String curGroupName = getCurrentGroupName();
		// actually add the time into our manager
		groupManager.addTime(prevTime, curGroupName);
	}//end addPreviousTime(start,end,handleDate)

	/**
	 * Adds a time that has been manually entered in by a user. Sets
	 * handleSpecificBeginEnd to false.
	 * @param duration The duration of the time.
	 * @param handleDate Whether or not we should worry about the date
	 * that the instance allegedly took place in favor of just using
	 * raw duration.
	 */
	public void addPreviousTime(Duration duration, boolean handleDate){
		TimedInstance prevTime = new TimedInstance(duration);
		prevTime.setHandleSpecificBeginEnd(false);
		prevTime.setHandleDate(handleDate);
		String curGroupName = getCurrentGroupName();
		// actually add the time into our manager
		groupManager.addTime(prevTime, curGroupName);
	}//end addPreviousTime(duration, handleDate)

	// TODO: Add methods for removing times or groups

	// TODO: Add methods for editing times or groups

	// TODO: Add methods for archiving times or groups

	// TODO: Add methods for saving state to files

	// TODO: Add methods for loading state from files
}//end class TimeController
