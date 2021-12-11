package HourTrackerLibrary;

import java.time.*;
import java.util.*;
import java.lang.Math;

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
	protected int curGroupIndex = -1;
	public String getCurrentGroupName(){
		try{
			return groupManager.getGroups().get(curGroupIndex).getName();
		}//end trying to return current group name
		catch (ArrayIndexOutOfBoundsException e){
			return "Ungrouped";
		}//end if curGroupIndex is wrong
	}//end getCurrentGroupName()
	public List<TimeGrouping> getGroups(){
		List<TimeGrouping> outputGroups = new ArrayList<TimeGrouping>();
		for(TimeGrouping group : groupManager.getGroups()){
			outputGroups.add(group);
		}//end adding groops in groupManager to outputGroups
		return outputGroups;
	}//end getGroups()
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
			if(curGroupIndex > 0 && curGroupIndex < groupManager.getGroups().size()){
				TimeGrouping curGroup = groupManager
				.getGroups().get(curGroupIndex);
				return curGroup.getTotalTime().plus(getClockedTime());
			}//end if curGroupIndex is valid
			else{
				return Duration.ZERO;
			}//end else return zero duration
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

	/**
	 * Removes a specific time.
	 * @param groupIndex The index of the group which holds the instance
	 * to be removed.
	 * @param instanceIndex The index of the instance within its group
	 * to be removed.
	 */
	public void removeTime(int groupIndex, int instanceIndex){
		// make sure the group index is valid
		List<TimeGrouping> groups = groupManager.getGroups();
		if(groupIndex >= groups.size() || groupIndex < 0){
			throw new ArrayIndexOutOfBoundsException(
				"The value given for groupIndex, " + groupIndex +
				", is out of bounds.");
		}//end if index invalid
		TimeGrouping groupWithTime = groups.get(groupIndex);
		// make sure the instance index is valid
		List<TimedInstance> groupTimes = groups.get(groupIndex).getTimes();
		if(instanceIndex >= groupTimes.size() || instanceIndex < 0){
			throw new ArrayIndexOutOfBoundsException(
				"The value given for instanceIndex, " + instanceIndex +
				", is out of bounds.");
		}//end if instanceIndex is invalid
		TimedInstance instanceToRemove = groupTimes.get(instanceIndex);
		// ask user if they're sure they want to remove the time
		boolean response = view.confirmationMessage("Are you sure that " +
		"you want to remove the time " + instanceToRemove.toString() +
		" in the group " + groupWithTime.toString() + "?");
		if(response){
			// update local store
			groupTimes.remove(instanceIndex);
			groupWithTime.setTimes(groupTimes);
			groups.set(groupIndex, groupWithTime);
			// update groupManager with local store
			groupManager.setGroups(groups);
			// display message to user
			view.logMessage("The timed instance named " +
			instanceToRemove.toString() + " in the group " +
			groupWithTime.toString() + " has been removed.");
		}//end if user really does want to remove the time
		else{
			view.logMessage("Timed instance removal cancelled.");
		}//end else the user doesn't really want to remove the time
	}//end removeTime(groupIndex, instanceIndex)

	/**
	 * Removes some times. Indices should be out of total list of times.
	 * @param indicesToRemove Collection of indices of all the times
	 * to remove.
	 */
	public void removeTimes(Collection<Integer> indicesToRemove){
		// validate all the indices and put them in right object
		TreeSet<Integer> indicesToPass = new TreeSet<Integer>();
		int timeCount = groupManager.getTimes().size();
		for(int index : indicesToRemove){
			if(index < 0 || index >= timeCount){
				throw new ArrayIndexOutOfBoundsException("The index " +
				index + " in indicesToRemove is invalid for the bounds " +
				"of the list with all times across all groups.");
			}//end if index is invalid
			else{
				indicesToPass.add(index);
			}//end else index is valid
		}//end looping adding indicesToRemove to TreeSet
		// get a list of the times specified and ask user if they're sure
		List<TimedInstance> times = groupManager.getTimes();
		StringBuilder sb = new StringBuilder();
		sb.append("Are you sure you want to delete the following times?");
		for(int index : indicesToPass){
			sb.append("\n" + times.get(index));
		}//end adding times to stringbuilder
		boolean response = view.confirmationMessage(sb.toString());
		if(response){
			// remove the specified instances
			groupManager.removeTimes(indicesToPass);
			// tell the user
			view.logMessage(indicesToPass.size() + " times were removed.");
		}//end if user really does want to remove times
		else{
			view.logMessage("Removal of timed instances cancelled.");
		}//end else user doesn't want to remove times
	}//end removeTimes(indicesToRemove)

	/**
	 * removes a group from the application. Gives the option of not
	 * removing the times in the group, and instead merging them into
	 * the Ungrouped group.
	 * @param groupIndex
	 */
	public void removeGroup(int groupIndex){
		// make sure the index is valid
		List<TimeGrouping> groups = groupManager.getGroups();
		if(groupIndex >= groups.size() || groupIndex < 0){
			throw new ArrayIndexOutOfBoundsException(
				"The value given for groupIndex, " + groupIndex +
				", is out of bounds.");
		}//end if index invalid
		TimeGrouping groupToRemove = groups.get(groupIndex);
		// ask user if they're sure they want to delete
		boolean reallyRemove = view.confirmationMessage("Are you sure that " +
		"you want to remove the " + groupToRemove.getName() +
		"group? (Containing " + groupToRemove.getTimeCount() +
		" times)");
		if(reallyRemove){
			if(groupToRemove.getTimeCount() > 0){
				boolean removeTimes = view.confirmationMessage("Since " +
				"the group you're merging has times in it, do you want " +
				"to get rid of all " + groupToRemove.getTimeCount() +
				" times in the group?");
				if(!removeTimes){
					int ungroupedIndex = ensureUngrouped();
					groupManager.mergeGroups(
					Math.min(groupIndex, ungroupedIndex),
					Math.max(groupIndex, ungroupedIndex), "Ungrouped", true);
				}//end if we should merge the times
			}//end if group we're removing has times
			// go ahead and remove the group
			groups.remove(groupIndex);
			// update groupManager with local store
			groupManager.setGroups(groups);
		}//end if user really does want to delete group
		else{
			view.logMessage("Group Removal Cancelled.");
		}//end else user doesn't really want to delete group
	}//end removeGroup(groupIndex)

	/**
	 * Adds the Ungrouped group to the manager if it's not
	 * there already, and then returns the index of it.
	 * @return The index of the ungrouped group within the
	 * group manager.
	 */
	protected int ensureUngrouped(){
		List<TimeGrouping> groups = groupManager.getGroups();
		int index2 = -1;
		for(int i = 0; i < groups.size(); i++){
			if(groups.get(i).getName().equals("Ungrouped")){
				index2 = i;
				break;
			}//end if we found Ungrouped
		}//end looking for Ungrouped in groups
		if(index2 == -1){
			index2 = groups.size();
			groups.add(new TimeGrouping("Ungrouped"));
			groupManager.setGroups(groups);
		}//end if we should create Ungrouped
		return index2;
	}//end ensureUngrouped()

	/**
	 * Allows a group to be edited.
	 * @param groupIndex The index of the group which should be
	 * edited.
	 */
	public void editGroup(int groupIndex){
		// make sure indices are valid and get right index from store
		List<TimeGrouping> groups = groupManager.getGroups();
		if(groupIndex >= groups.size() || groupIndex < 0){
			throw new ArrayIndexOutOfBoundsException(
				"The value given for groupIndex, " + groupIndex +
				", is out of bounds.");
		}//end if groupIndex is invalid
		TimeGrouping groupToEdit = groups.get(groupIndex);
		// go ahead and get an edited valaue from the user
		TimeGrouping editedGroup = view.editGroup(groupToEdit);
		// figure out what the user wanted us to do
		if(editedGroup != null){
			// update our temporary store of groups
			groups.set(groupIndex, editedGroup);
			// set our local store into the groupManager
			groupManager.setGroups(groups);
			// display a log message
			view.logMessage(editedGroup.getName() + " group has been " +
			"successfully edited.");
		}//end if the user actually did edit the group
		else{
			view.logMessage("Group Edit Cancelled.");
		}//end else user cancelled group editting
	}//end editGroup(groupindex)
	
	/**
	 * Edits an instance. Includes call to view to get edited
	 * instance value.
	 * @param groupIndex Index of group that has the instance we want
	 * to edit.
	 * @param instanceIndex Index of instance within group
	 */
	public void editInstance(int groupIndex, int instanceIndex){
		// make sure indices are valid and get right index from store
		List<TimeGrouping> groups = groupManager.getGroups();
		if(groupIndex >= groups.size() || groupIndex < 0){
			throw new ArrayIndexOutOfBoundsException(
				"The value given for groupIndex, " + groupIndex +
				", is out of bounds.");
		}//end if groupIndex is invalid
		List<TimedInstance> groupTimes = groups.get(groupIndex).getTimes();
		if(instanceIndex >= groupTimes.size() || instanceIndex < 0){
			throw new ArrayIndexOutOfBoundsException(
				"The value given for instanceIndex, " + instanceIndex +
				", is out of bounds.");
		}//end if instanceIndex is invalid
		TimedInstance instanceToEdit = groupTimes.get(instanceIndex);
		// get edited value from the view
		TimedInstance editedInstance = view.editInstance(instanceToEdit);
		// figure out what the user wanted us to do
		if(editedInstance != null){
			// update our temportary store of groups
			groupTimes.set(instanceIndex, editedInstance);
			groups.get(groupIndex).setTimes(groupTimes);
			// set our local store into the groupManager
			groupManager.setGroups(groups);
			// display log message
			view.logMessage("Instance " + editedInstance.getName() +
			" in the group " + groups.get(groupIndex).getName() +
			" has been edited successfully.");
		}//end if user did actually edit the instance
		else{
			view.logMessage("Instance Editing Cancelled.");
		}//end else user didn't really want to edit instance
	}//end editInstance(groupIndex, instanceIndex)

	// TODO: Make sure that TimedInstance references to their group get updates
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
