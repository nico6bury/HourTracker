package HourTrackerLibrary;

/**
 * 
 */
public interface TimeView {
	public void refreshView();
	public String getSelectedGroupName();
	public String getCurrentInstanceName();
	public String getPathWithMessage(String message);
}//end interface TimeView
