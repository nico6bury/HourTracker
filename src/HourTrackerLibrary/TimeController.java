package HourTrackerLibrary;

import java.time.*;
import java.util.*;

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
	 * The object responsible for handling file storage stuff.
	 */
	protected TimeStorageIO fileio = new TimeStorageIO();

	/**
	 * Time update interval in mulliseconds.
	 */
	public long timeUpdateInterval = 500;
	/**
	 * Initializes this controller object with the view it should be
	 * yeeting its method calls over to.
	 * @param view The view that should be associated with
	 * this controller.
	 */
	public TimeController(TimeView view){
		this.view = view;
		this.timeTask = new UpdateTimeTask(this.view);
		// make sure we have a good file path
		if(fileio.getStorageDirectory().equals("")){
			// log message about no config file found
			view.logMessage("No existing files found. Getting directory.");
			// get a directory from the user
			String filepath = view.getPathWithMessage("It seems you " +
			"don\'t already have a configured directory for storing " +
			"saved times. Please select a folder where you would like " +
			"this application to save it\'s human-readable files for " +
			"your use.");
			while(!fileio.saveStorageDirectory(filepath)){
				filepath = view.getPathWithMessage("I\'m sorry, " +
				"but it seems that the path you gave was not a valid " +
				"directory or something. Please try again.");
			}//end looping while filepath is unavailable.
		}//end if we have a default storage directory
		else{
			// get groups from files
			List<TimeGrouping> groups = fileio.loadGroups();
			// add them to the manager
			groupManager.setGroups(groups);
			// add log message
			view.logMessage("Found existing files. Reading them.");
		}//end else we have stuff stored already
	}//end sole constructor

	/**
	 * TimerTask for calling updateTime method in view.
	 */
	protected class UpdateTimeTask extends TimerTask{
		TimeView view = null;
		protected UpdateTimeTask(TimeView view){ this.view = view; }
		public void run(){ view.updateTime(); }
	}//end class UpdateTimeTask

	/**
	 * The timer we use for updating time for user.
	 */
	Timer clock = new Timer();
	UpdateTimeTask timeTask;
	/**
	 * Starts the controller's internal clock for updating time in view.
	 */
	public void startClock(){
		clock.scheduleAtFixedRate(timeTask, 0, timeUpdateInterval);
	}//end startClock()

	/**
	 * Stops the controller's internal clock from updating, freeing
	 * up a few more resources.
	 */
	public void stopClock(){
		timeTask.cancel();
		clock.cancel();
		clock.purge();
	}//end stopClock()

	/**
	 * The amount of time user has been clocked in.
	 * @return Amount of time user has been clocked in if they
	 * are clocked in, or 0 otherwise.
	 */
	public Duration getClockedTime(){
		if(currentlyClocked){
			return Duration.between(clockInTime, Instant.now());
		}//end if currently clocked in
		else{
			return Duration.ofNanos(0);
		}//end else not clocked in
	}//end getClockedTime()

	/**
	 * The projected total time of current group if you clock out
	 * with the time you have currently.
	 * @return Projected total time for group if group is valid,
	 * normal clockedTime behavior if no group found.
	 */
	public Duration getProjectedGroupTotalTime(){
		try{
			TimeGrouping curGroup = groupManager
			.getGroups().get(curGroupIndex);
			return curGroup.getTotalTime().plus(getClockedTime());
		}//end trying to get group stuff
		catch (ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			return getClockedTime();
		}//end catching ArrayIndexOutOfBoundsExceptions
	}//end getProjectedGroupTotalTime()
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

	/**
	 * Saves all the currently loaded groups as files.
	 * @return Returns true if the files were saved
	 * successfully, or false if something failed.
	 */
	public boolean saveCurrentState(){
		return fileio.saveConfiguration();
	}//end saveCurrentState()
	
	/**
	 * Reloads all the groups saved in files.
	 */
	public void reloadSavedGroups(){
		groupManager.setGroups(fileio.loadGroups());
	}//end reloadSavedGroups()

	/**
	 * Updates the directory where we save all our human-readable
	 * files.
	 * @param directory Path of directory where we should start saving
	 * files.
	 * @return Returns true if the operation succeeded or false if
	 * something went wrong somehow.
	 */
	public boolean updateTimeDirectory(String directory){
		return fileio.saveStorageDirectory(directory);
	}//end updateTimeDirectory(directory)
}//end class TimeController
