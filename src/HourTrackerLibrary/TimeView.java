package HourTrackerLibrary;

/**
 * 
 */
public interface TimeView {
	public void refreshView();
	public void updateTime();
	public String getSelectedGroupName();
	public String getCurrentInstanceName();
	public String getPathWithMessage(String message);
	public TimedInstance editInstance(TimedInstance instance);
	public TimeGrouping editGroup(TimeGrouping group);
	public void displayMessage(String message);
	public boolean confirmationMessage(String message);
	public void logMessage(String message);
}//end interface TimeView
