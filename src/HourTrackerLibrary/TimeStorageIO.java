package HourTrackerLibrary;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.IOException;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.prefs.*;

/**
 * A class which handles File I/O for the
 * TimeController class.
 */
public class TimeStorageIO {
	/*
	Things that I should probably include in this class:
	* configuration file for the application that is completely handled
	and read/written from solely from this class.
	* internal listing of files that should be currently managed by this
	class, updated, maybe with a private enum for their purpose, so we'd
	keep like a hashmap of paths maybe, and then some files should be kept,
	while others should be deleted and regenerated a bunch depending on
	their purpose.
	* public-facing methods that make it easy for the controller class
	to initialize this class, read its state from the files we manage,
	and also save stuff back to some files afterwards.
	* and one other thing: make sure that any time we write file information,
	we delete all the files we have registered as already written there.
	This is basically the only way to deal with renaming groups, as that's
	tied to the filename I've decided.
	*/

	/**
	 * Holds all the files currently managed by this class. Each path should
	 * be to a file holding serialized information
	 */
	protected List<Path> managedFiles = new ArrayList<Path>();
	/**
	 * Returns a readonly list of files that are being managed by
	 * this class currently. 
	 * @return
	 */
	public List<String> getManagedFiles(){
		List<String> files = new ArrayList<String>();
		for(Path path : managedFiles){
			files.add(path.toString());
		}//end looping over Paths in managedFiles
		return files;
	}//end getManagedFiles()
	/**
	 * The directory where we should store state storage files
	 */
	protected Path stateStorageDirectory = null;
	/**
	 * Returns string indicating path of directory where this class
	 * stores files. The default is an empty string, so it's best to
	 * change this.
	 * @return 
	 */
	public String getStorageDirectory(){
		return stateStorageDirectory.toString();
	}//end getStorageDirectory()
	/**
	 * Attempts to save a new directory path as the chosen
	 * directory to save files in.
	 * @param path The path of the directory where this class
	 * should save files.
	 * @return Returns true if the path given is a valid directory,
	 * or false if it is not or otherwise fails for any reason.
	 */
	public boolean saveStorageDirectory(String path){
		try{
			Path tempPath = Paths.get(path);
			if(Files.isDirectory(tempPath)) stateStorageDirectory = tempPath;
			else throw new InvalidPathException(path, "Path not a directory.");
			return true;
		}//end trying to get a path object from parameter
		catch (InvalidPathException e){
			e.printStackTrace();
			return false;
		}//end catching InvalidPathExceptions
	}//end saveStorageDirectory(path)
	/**
	 * The preferences for this package. :-)
	 */
	Preferences prefs = Preferences.systemNodeForPackage(this.getClass());
	/**
	 * Key for DefaultSerializedDirectory, or the directory where
	 * we should, by default, store serialized objects in files that
	 * the user could edit.
	 */
	protected static final String PREF_KEY_DSD = "DSD";
	protected static final String PREF_DEFAULT_DSD = "";
	/**
	 * Key for SerializedStateFileNames, a string which contains
	 * the names of all the files we should be performing I/O on
	 * within the DSD. These will be separated by a / (forward slash).
	 */
	protected static final String PREF_KEY_SSFN = "SSFN";
	protected static final String PREF_DEFAULT_SSFN = "";
	
	/**
	 * Initializes this class and retrieves previously stored
	 * system preferences.
	 */
	public TimeStorageIO(){
		// get all our preferences that we need
		String[] expectedFilenames = prefs.get(
			PREF_KEY_SSFN, PREF_DEFAULT_SSFN
		).split("/");
		this.stateStorageDirectory = Paths.get(
			prefs.get(PREF_KEY_DSD, PREF_DEFAULT_DSD)
		);
		managedFiles = new ArrayList<Path>();
		for(String expectedFile : expectedFilenames){
			try{
				managedFiles.add(Paths.get(this.stateStorageDirectory.toString(),
				expectedFile));
			}//end trying to add paths
			catch (InvalidPathException e){
				e.printStackTrace();
			}//end catching invalidPathExceptions
		}//end looping over expected filenames
	}//end sole constructor

	/**
	 * Saves the current configuration in terms of which files to
	 * look for and where to look for them.
	 * @return Returns true if the configuration was successfully
	 * saved, or false if some sort of exception was thrown during
	 * the process that prevented configuration editing.
	 */
	public boolean saveConfiguration(){
		try{
			// save directory configuration
			prefs.put(PREF_KEY_DSD, this.stateStorageDirectory.toString());
			// format managed files to be stored in prefs
			StringBuilder sb = new StringBuilder();
			if(managedFiles.size() > 0) sb.append(managedFiles.get(0).toString());
			for(int i = 1; i < managedFiles.size(); i++){
				sb.append(managedFiles.get(i).toString() + '/');
			}//end looping over managedFiles, skipping first
			// save managed files configuration
			prefs.put(PREF_KEY_SSFN, sb.toString());
			return true;
		}//end trying to save preferences
		catch (NullPointerException e){
			e.printStackTrace();
			return false;
		}//end catching NullPointerException
		catch (IllegalArgumentException e){
			e.printStackTrace();
			return false;
		}//end catching IllegalArgumentException
		catch (IllegalStateException e){
			e.printStackTrace();
			return false;
		}//end catching IllegalStateException
	}//end saveConfiguration()

	/**
	 * Load groups from files into objects.
	 * @return A list of groups that we read from the files.
	 */
	public List<TimeGrouping> loadGroups(){
		List<TimeGrouping> output = new LinkedList<TimeGrouping>();
		for(Path path : managedFiles){
			try {
				// get all the lines from the file
				List<String> lines = Files.readAllLines(path);
				// convert lines into TimeGrouping object
				TimeGrouping group = new TimeGrouping();
				group.deserialize(lines);
				// add this group to our output
				output.add(group);
			}//end trying to read the file
			catch (IOException e) {
				e.printStackTrace();
			}//end catching IOExceptions
		}//end looping over managed files
		return output;
	}//end loadGroups()

	/**
	 * Saves a list of groups to several files in working
	 * directory configured to be used for file storage.
	 * @param groups The groups that should be saved.
	 */
	public void saveGroups(List<TimeGrouping> groups){
		wipeManagedFiles();
		for(TimeGrouping group : groups){
			try{
				// get the path that we'll use
				Path groupFilepath = Paths.get(
				this.stateStorageDirectory.toString(),
				group.getName());
				// write serial to file and save path in managedFiles
				if(saveGroup(group, groupFilepath))
					managedFiles.add(groupFilepath);
			}//end trying to do file I/O stuff
			catch (InvalidPathException e){
				e.printStackTrace();
			}//end catching invalid path exceptions
		}//end looping over groups
	}//end saveGroups(groups)

	/**
	 * Saves a particular group in a particular path using the
	 * group's serialize() method.
	 * @param group The group to save in a file.
	 * @param path The path of the file.
	 * @return Returns true if the operation was successful, or
	 * false if some error occured which prevented the operation
	 * from succeeding.
	 */
	protected boolean saveGroup(TimeGrouping group, Path path){
		try{
			// create the file if it doesn't exist yet
			if(Files.notExists(path)) Files.createFile(path);
			// write group serial to path
			Files.write(path, group.serialize());
			// if we got here, must have succeeded
			return true;
		}//end trying to do file I/O
		catch (IOException e){
			e.printStackTrace();
			return false;
		}//end catching IOExceptions
	}//end saveGroup(group, path)

	/**
	 * Deletes all the files registered in managedFiles.
	 * Also clears managedFiles.
	 */
	protected void wipeManagedFiles(){
		for(Path path : managedFiles){
			try {
				Files.deleteIfExists(path);
			}//end trying to delete existing files
			catch (IOException e) {
				e.printStackTrace();
			}//end catching IOExceptions
		}//end looping over managed files
		managedFiles.clear();
	}//end wipeManagedFiles()
}//end class TimeStorageIO
