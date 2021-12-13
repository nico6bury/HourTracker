package HourTrackerTerminal;

import java.io.IOException;
import java.util.*;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.*;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.*;
import com.googlecode.lanterna.input.*;
import com.googlecode.lanterna.screen.*;
import com.googlecode.lanterna.terminal.*;

import HourTrackerLibrary.TimeController;
import HourTrackerLibrary.TimeView;

public class Program {
	public static void main(String[] args){
		// sets up controller
		TimeController controller = new TimeController();
		TimeView view = new HourTrackerConsole(controller);
		// do stuff?
	}//end main method

	public static void lanterna1(){
		DefaultTerminalFactory defaultTerminalFactory =
		new DefaultTerminalFactory();
		Terminal terminal = null;
		try{
			terminal = defaultTerminalFactory.createTerminal();
			terminal.putCharacter('H');
			terminal.putCharacter('e');
			terminal.putCharacter('l');
			terminal.putCharacter('l');
			terminal.putCharacter('o');
			terminal.putCharacter('\n');
			terminal.flush();

			Thread.sleep(2000);
			
			TerminalPosition startPosition = terminal.getCursorPosition();
			terminal.setCursorPosition(startPosition.withRelativeColumn(3)
			.withRelativeRow(2));
			terminal.flush();
			
			Thread.sleep(2000);

			terminal.setBackgroundColor(TextColor.ANSI.BLUE);
			terminal.setForegroundColor(TextColor.ANSI.YELLOW);

			terminal.putCharacter('Y');
			terminal.putCharacter('e');
			terminal.putCharacter('l');
			terminal.putCharacter('l');
			terminal.putCharacter('o');
			terminal.putCharacter('w');
			terminal.putCharacter(' ');
			terminal.putCharacter('o');
			terminal.putCharacter('n');
			terminal.putCharacter(' ');
			terminal.putCharacter('b');
			terminal.putCharacter('l');
			terminal.putCharacter('u');
			terminal.putCharacter('e');
			terminal.flush();
			
			Thread.sleep(2000);

			terminal.setCursorPosition(startPosition.withRelativeColumn(3).withRelativeRow(3));
			terminal.flush();

			Thread.sleep(2000);
			
			terminal.enableSGR(SGR.BOLD);
			terminal.putCharacter('Y');
			terminal.putCharacter('e');
			terminal.putCharacter('l');
			terminal.putCharacter('l');
			terminal.putCharacter('o');
			terminal.putCharacter('w');
			terminal.putCharacter(' ');
			terminal.putCharacter('o');
			terminal.putCharacter('n');
			terminal.putCharacter(' ');
			terminal.putCharacter('b');
			terminal.putCharacter('l');
			terminal.putCharacter('u');
			terminal.putCharacter('e');
			terminal.flush();
			
			Thread.sleep(2000);

			terminal.resetColorAndSGR();
			terminal.setCursorPosition(terminal.getCursorPosition().withColumn(0).withRelativeRow(1));
			terminal.putCharacter('D');
			terminal.putCharacter('o');
			terminal.putCharacter('n');
			terminal.putCharacter('e');
			terminal.putCharacter('\n');
			terminal.flush();
		
			Thread.sleep(2000);

			terminal.bell();
			terminal.flush();
			Thread.sleep(200);

			terminal.exitPrivateMode();

			for(char ch : "Print this on the terminal.".toCharArray()){
				terminal.putCharacter(ch);
			}//end looping over string
		}//end trying to do terminal stuff
		catch(IOException | InterruptedException e){
			e.printStackTrace();
		}//end catching IOExceptions
		finally{
			if(terminal != null){
				try{
					terminal.close();
				}//end trying to clsoe the terminal
				catch(IOException e){
					e.printStackTrace();
				}//end catching IOExceptions
			}//end if terminal not null
		}//end finally closing terminal
	}//end lanterna1()

	public static void lanterna2(){
		DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
		Terminal terminal = null;
		try {
			terminal = defaultTerminalFactory.createTerminal();

			terminal.enterPrivateMode();
			
			terminal.clearScreen();
			
			terminal.setCursorVisible(false);
			
			final TextGraphics textGraphics = terminal.newTextGraphics();
			
			textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
			textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);
			
			textGraphics.putString(2, 1, "Lanterna Tutorial 2 - Press ESC to exit", SGR.BOLD);
			textGraphics.setForegroundColor(TextColor.ANSI.DEFAULT);
			textGraphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
			textGraphics.putString(5, 3, "Terminal Size: ", SGR.BOLD);
			textGraphics.putString(5 + "Terminal Size: ".length(), 3, terminal.getTerminalSize().toString());
			terminal.flush();
			
			terminal.addResizeListener(new TerminalResizeListener() {
				@Override
				public void onResized(Terminal terminal, TerminalSize newSize) {
					// Be careful here though, this is likely running on a separate thread. Lanterna is threadsafe in 
					// a best-effort way so while it shouldn't blow up if you call terminal methods on multiple threads, 
					// it might have unexpected behavior if you don't do any external synchronization
					textGraphics.drawLine(5, 3, newSize.getColumns() - 1, 3, ' ');
					textGraphics.putString(5, 3, "Terminal Size: ", SGR.BOLD);
					textGraphics.putString(5 + "Terminal Size: ".length(), 3, newSize.toString());
					try {
						terminal.flush();
					}
					catch(IOException e) {
						// Not much we can do here
						throw new RuntimeException(e);
					}//end catching IOExceptions
				}//end onResized
			});
	
			textGraphics.putString(5, 4, "Last Keystroke: ", SGR.BOLD);
			textGraphics.putString(5 + "Last Keystroke: ".length(), 4, "<Pending>");
			terminal.flush();
			
			KeyStroke keyStroke = terminal.readInput();
			while(keyStroke.getKeyType() != KeyType.Escape) {
				textGraphics.drawLine(5, 4, terminal.getTerminalSize().getColumns() - 1, 4, ' ');
				textGraphics.putString(5, 4, "Last Keystroke: ", SGR.BOLD);
				textGraphics.putString(5 + "Last Keystroke: ".length(), 4, keyStroke.toString());
				terminal.flush();
				keyStroke = terminal.readInput();
			}//end looping while user doesn't escape
		}//end trying to do terminal stuff
		catch(IOException e) {
			e.printStackTrace();
		}//end catching IOExceptions
		finally {
			if(terminal != null) {
				try {
					terminal.close();
				}//end trying to close the terminal
				catch(IOException e) {
					e.printStackTrace();
				}//end catching IOExceptions
			}//end if terminal isn't null
		}//end finally
	}//end lanterna2()

	public static void lanterna3(){
		DefaultTerminalFactory defaultTerminalFactory =
		new DefaultTerminalFactory();
		Screen screen = null;
		try {
			Terminal terminal = defaultTerminalFactory.createTerminal();
			screen = new TerminalScreen(terminal);
			
			screen.startScreen();
			
			screen.setCursorPosition(null);
			
			Random random = new Random();
			TerminalSize terminalSize = screen.getTerminalSize();
			for(int column = 0; column < terminalSize.getColumns(); column++) {
				for(int row = 0; row < terminalSize.getRows(); row++) {
					screen.setCharacter(column, row, new TextCharacter(
						' ',
						TextColor.ANSI.DEFAULT,
						// This will pick a random background color
						TextColor.ANSI.values()[random
						.nextInt(TextColor.ANSI.values().length)]));
				}//end for looping
			}//end for looping
			
			screen.refresh(); Thread.sleep(2000);
			
			long startTime = System.currentTimeMillis();
			while(System.currentTimeMillis() - startTime < 2000) {
				// The call to pollInput() is not blocking, unlike readInput()
				if(screen.pollInput() != null) {
					break;
				}//end if screen isn't null
				try {
					Thread.sleep(1);
				}//end trying to sleep
				catch(InterruptedException ignore) {
					break;
				}//end catching interruptions
			}//end while loop
			while(true) {
				KeyStroke keyStroke = screen.pollInput();
				if(keyStroke != null && (keyStroke.getKeyType() ==
				KeyType.Escape || keyStroke.getKeyType() == KeyType.EOF)) {
					break;
				}//end if

				TerminalSize newSize = screen.doResizeIfNecessary();
				if(newSize != null) {
					terminalSize = newSize;
				}//end if newSize != null

				// Increase this to increase speed
				final int charactersToModifyPerLoop = 1;
				for(int i = 0; i < charactersToModifyPerLoop; i++) {
                    TerminalPosition cellToModify = new TerminalPosition(
                    random.nextInt(terminalSize.getColumns()),
                    random.nextInt(terminalSize.getRows()));
					
					TextColor.ANSI color = TextColor.ANSI.values()
					[random.nextInt(TextColor.ANSI.values().length)];
					
					TextCharacter characterInBackBuffer = screen
					.getBackCharacter(cellToModify);
					characterInBackBuffer = characterInBackBuffer
					.withBackgroundColor(color);
					characterInBackBuffer = characterInBackBuffer
					.withCharacter(' ');   // Because of the label box further down, if it shrinks
					screen.setCharacter(cellToModify, characterInBackBuffer);
				}//end for loop
				
				String sizeLabel = "Terminal Size: " + terminalSize;
				TerminalPosition labelBoxTopLeft = new TerminalPosition(1, 1);
				TerminalSize labelBoxSize =
				new TerminalSize(sizeLabel.length() + 2, 3);
				TerminalPosition labelBoxTopRightCorner =
				labelBoxTopLeft.withRelativeColumn(labelBoxSize.getColumns() - 1);
				TextGraphics textGraphics = screen.newTextGraphics();
				//This isn't really needed as we are overwriting everything below anyway, but just for demonstrative purpose
				textGraphics.fillRectangle(labelBoxTopLeft, labelBoxSize, ' ');
				
				textGraphics.drawLine(
					labelBoxTopLeft.withRelativeColumn(1),
					labelBoxTopLeft.withRelativeColumn
					(labelBoxSize.getColumns() - 2),
					Symbols.DOUBLE_LINE_HORIZONTAL);
				textGraphics.drawLine(
				labelBoxTopLeft.withRelativeRow(2).withRelativeColumn(1),
				labelBoxTopLeft.withRelativeRow(2)
				.withRelativeColumn(labelBoxSize.getColumns() - 2),
				Symbols.DOUBLE_LINE_HORIZONTAL);
				
				textGraphics.setCharacter(labelBoxTopLeft,
				Symbols.DOUBLE_LINE_TOP_LEFT_CORNER);
				textGraphics.setCharacter(labelBoxTopLeft.withRelativeRow(1),
				Symbols.DOUBLE_LINE_VERTICAL);
				textGraphics.setCharacter(labelBoxTopLeft.withRelativeRow(2),
				Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER);
				textGraphics.setCharacter(labelBoxTopRightCorner,
				Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER);
				textGraphics.setCharacter(labelBoxTopRightCorner
				.withRelativeRow(1), Symbols.DOUBLE_LINE_VERTICAL);
				textGraphics.setCharacter(labelBoxTopRightCorner
				.withRelativeRow(2), Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER);
				
				textGraphics.putString(labelBoxTopLeft.withRelative(1, 1),
				sizeLabel);
				
				screen.refresh();
				Thread.yield();
			}//end while loop
		}//end try
		catch(IOException | InterruptedException e) {
			e.printStackTrace();
		}//end catching IOExceptions
		finally {
			if(screen != null) {
				try {
					screen.close();
				}//end trying to close screen
				catch(IOException e) {
					e.printStackTrace();
				}//end catch IOExceptions
			}//end if screen isn't null
		}//end finally
	}//end lanterna3

	public static void lanterna4(){
		DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
		Screen screen = null;
		
		try {
			screen = terminalFactory.createScreen();
			screen.startScreen();

			final WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);

			final Window window = new BasicWindow("My Root Window");

			Panel contentPanel = new Panel(new GridLayout(2));

			GridLayout gridLayout = (GridLayout)contentPanel.getLayoutManager();
			gridLayout.setHorizontalSpacing(3);

			Label title = new Label("This is a label that spans two columns");
			title.setLayoutData(GridLayout.createLayoutData(
				GridLayout.Alignment.BEGINNING, // Horizontal alignment in the grid cell if the cell is larger than the component's preferred size
				GridLayout.Alignment.BEGINNING, // Vertical alignment in the grid cell if the cell is larger than the component's preferred size
				true,       // Give the component extra horizontal space if available
				false,        // Give the component extra vertical space if available
				2,                  // Horizontal span
				1));                  // Vertical span
			contentPanel.addComponent(title);

			contentPanel.addComponent(new Label("Text Box (aligned)"));
			contentPanel.addComponent(
			new TextBox()
			.setLayoutData(GridLayout.createLayoutData(
				GridLayout.Alignment.BEGINNING, GridLayout.Alignment.CENTER)));

			contentPanel.addComponent(new Label("Password Box (right aligned)"));
			contentPanel.addComponent(
			new TextBox()
				.setMask('*')
				.setLayoutData(GridLayout
				.createLayoutData(GridLayout.Alignment.END,
				GridLayout.Alignment.CENTER)));

			contentPanel.addComponent(new Label("Read-only Combo Box (forced size)"));
			List<String> timezonesAsStrings = new ArrayList<String>();
			for(String id: TimeZone.getAvailableIDs()) {
				timezonesAsStrings.add(id);
			}
			ComboBox<String> readOnlyComboBox = new ComboBox<String>(timezonesAsStrings);
			readOnlyComboBox.setReadOnly(true);
			readOnlyComboBox.setPreferredSize(new TerminalSize(20, 1));
			contentPanel.addComponent(readOnlyComboBox);
			
			contentPanel.addComponent(new Label("Editable Combo Box (filled)"));
			contentPanel.addComponent(
			new ComboBox<String>("Item #1", "Item #2", "Item #3", "Item #4")
			.setReadOnly(false)
			.setLayoutData(GridLayout.createHorizontallyFilledLayoutData(1)));

			contentPanel.addComponent(new Label("Button (centered)"));
			contentPanel.addComponent(new Button("Button", new Runnable() {
				@Override
				public void run() {
					MessageDialog.showMessageDialog(textGUI, "MessageBox", "This is a message box", MessageDialogButton.OK);
				}
			}).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER)));

			contentPanel.addComponent(
				new EmptySpace()
					.setLayoutData(
						GridLayout.createHorizontallyFilledLayoutData(2)));
			contentPanel.addComponent(
				new Separator(Direction.HORIZONTAL)
					.setLayoutData(
						GridLayout.createHorizontallyFilledLayoutData(2)));
			contentPanel.addComponent(
				new Button("Close", new Runnable() {
					@Override
					public void run() {
						window.close();
					}
				}).setLayoutData(
					GridLayout.createHorizontallyEndAlignedLayoutData(2)));

			window.setComponent(contentPanel);

			textGUI.addWindowAndWait(window);

		}//end try
		catch (IOException e) {
			e.printStackTrace();
		}//end catch 
		finally {
			if(screen != null) {
				try {
					screen.stopScreen();
				}//end try
				catch(IOException e) {
					e.printStackTrace();
				}//end catch
			}//end if
		}//end finally
	}//end lanterna4
}//end class Program
