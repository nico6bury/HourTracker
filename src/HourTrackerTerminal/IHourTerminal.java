package HourTrackerTerminal;

import java.lang.reflect.Field;
import java.util.List;

import HourTrackerLibrary.TimeGrouping;
import HourTrackerLibrary.TimedInstance;

/**
 * An interface for terminal stuff used for HourTracker.
 */
public interface IHourTerminal {
	/**
	 * Starts the terminal.
	 * @return Returns true if the terminal is able to
	 * start correctly, or false if it isn't able to start.
	 */
	public boolean startTerminal();
	/**
	 * Stops the terminal.
	 * @return Returns true if the terminal is able to stop,
	 * or false if it fails somehow.
	 */
	public boolean stopTerminal();
	/**
	 * Just displays the main menu selection screen.
	 * @return
	 */
	public void showMainMenu();
	/**
	 * Updates the time display section of the terminal view.
	 * @param times Am array of time statistics to be displayed
	 * on a line.
	 */
	public void updateCurrentTime(String[] times);
	/**
	 * Updates the active group section of the terminal view.
	 * If there is no active group, just pass null, and this line
	 * should be ignored.
	 * @param groupStats An array of group statistics to be displayed
	 * on a row, separated by pipe characters.
	 */
	public void updateActiveGroup(String[] groupStats);
	/**
	 * Updates the list of messages that are currently active.
	 * The terminal should automatically keep track of how many
	 * messages are being displayed and update itself accordingly.
	 * @param messages The list of messages to be displayed. Each
	 * element of the list will be displayed on its own line.
	 */
	public void updateMessages(List<String> messages);
	/**
	 * Updates the list of groups that are currently being displayed.
	 * The terminal should automatically keep track of how many
	 * lines are being displayed and update itself accordingly.
	 * @param groups The list of group information to be
	 * displayed. Each element of the list will be displayed on it's own
	 * line.
	 */
	public void updateGroups(List<String> groups);
	/**
	 * Updates the list of timed instances that are currently being
	 * displayed. The terminal should automatically keep track of how
	 * many instances are being displayed and update itself accordingly.
	 * @param times The list of time information to be displayed.
	 * Each element of the list will be displayed on its own line.
	 */
	public void updateTimes(List<String> times);
	/**
	 * Prompts the user for a particular choice of several things.
	 * @param options The list of options to be displayed to the user.
	 * @return The index of options which the user selected.
	 */
	public int getUserChoice(String[] options);
	/**
	 * Prompts the user to edit a timed instance object, and returns the object
	 * that they give.
	 * @param timeToEdit The timed instance which should be edited by the user.
	 * @param allowCancellation Whether or not to allow the user to cancel.
	 * @param fieldsToEdit The fields of timeToEdit which the user should be
	 * allowed to edit.
	 * @return Returns the timedInstance object created. If allowCancellation
	 * was true, and the user cancelled, then this will be null.
	 */
	public TimedInstance editTimedInstance(TimedInstance timeToEdit,
	boolean allowCancellation, Field[] fieldsToEdit);
	/**
	 * Prompts the user to edit a time grouping object, and returns the
	 * object that they give.
	 * @param groupToEdit The time grouping object which should be
	 * edited by the user.
	 * @param allowCancellation Whether or not to allow the user to cancel.
	 * @param fieldsToEdit The field of groupToEdit which the user should
	 * be allowed to edit.
	 * @return Returns the time grouping object created. If allowCancellation
	 * was true, and the user cancelled, then this will be null.
	 */
	public TimeGrouping editTimeGrouping(TimeGrouping groupToEdit,
	boolean allowCancellation, Field[] fieldsToEdit);
}//end interface IHourTerminal
