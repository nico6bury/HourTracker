package HourTrackerTerminal;

import HourTrackerLibrary.TimeController;

public class Program {
	public static void main(String[] args){
		ConsoleTimeView view = new ConsoleTimeView();
		TimeController controller = new TimeController(view);
		view.setController(controller);
		view.refreshView();
	}//end main method
}//end class Program
