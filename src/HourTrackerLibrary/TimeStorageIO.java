package HourTrackerLibrary;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;

/**
 * A class which handles File I/O for the
 * TimeController class.
 */
public class TimeStorageIO {
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
	 * @deprecated
	 */
	protected Path stateStorageDirectory = null;
	/**
	 * Returns Path of the directory which the jar is located in.
	 * @throws URISyntaxException
	 * @deprecated
	 */
	public Path getStorageDirectory() throws URISyntaxException{
		return Paths.get(getClass().getProtectionDomain()
			.getCodeSource().getLocation().toURI().getPath());
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
			if(Files.isDirectory(tempPath)){
				// transfer all of our currently managed files to new directory
				List<TimeGrouping> groups = loadGroups();
				saveGroups(groups, tempPath);
				// wipe files in previous location so as to not leave dupes
				wipeManagedFiles();
				// save over stateStorageDirectory to complete transfer
				stateStorageDirectory = tempPath;				
			}//end if new path is a directory
			else throw new InvalidPathException(path, "Path not a directory.");
			return true;
		}//end trying to get a path object from parameter
		catch (InvalidPathException e){
			e.printStackTrace();
			return false;
		}//end catching InvalidPathExceptions
	}//end saveStorageDirectory(path)
	
	/**
	 * Initializes this class and retrieves previously stored
	 * system preferences.
	 */
	public TimeStorageIO(){
		// load configuration stuff
		try {
			Path configPath = getStorageDirectory().resolve(configFilename);
			if(Files.exists(configPath)){
				for(String line : Files.readAllLines(configPath)){
					managedFiles.add(Paths.get(line));
				}//end adding each line in file as managedFile
			}//end if config file exists
			else{
				Files.createFile(configPath);
			}//end if we have to make the config file
		}//end trying to save config
		catch (URISyntaxException | IOException e) {
			//e.printStackTrace();
		}//end catching exceptions
	}//end sole constructor

	public static final String configFilename = "config.txt";

	/**
	 * Saves the current configuration in terms of which files to
	 * look for and where to look for them.
	 * @return Returns true if the configuration was successfully
	 * saved, or false if some sort of exception was thrown during
	 * the process that prevented configuration editing.
	 */
	public boolean saveConfiguration(){
		try {
			Path configPath = getStorageDirectory().resolve(configFilename);
			Files.write(configPath, getManagedFiles());
			return true;
		}//end trying to save config
		catch (URISyntaxException | IOException e) {
			e.printStackTrace();
			return false;
		}//end catching exceptions
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
	public boolean saveGroups(List<TimeGrouping> groups){
		wipeManagedFiles();
		try {
			saveGroups(groups, getStorageDirectory());
			return true;
		}//end trying to save groups
		catch (URISyntaxException e) {
			e.printStackTrace();
			return false;
		}//end catching URISyntaxExceptions
	}//end saveGroups(groups)

	/**
	 * Saves groups to a specific directory without wiping any files.
	 * @param groups The list of gorups to save.
	 * @param directory The directory that the groups should be saved in.
	 */
	protected void saveGroups(List<TimeGrouping> groups, Path directory){
		for(TimeGrouping group : groups){
			try{
				// get the path that we'll use
				Path groupFilepath = Paths.get(
				directory.toString(),
				group.getName());
				// write serial to file and save path in managedFiles
				if(saveGroup(group, groupFilepath))
					managedFiles.add(groupFilepath);
			}//end trying to do file I/O stuff
			catch (InvalidPathException e){
				e.printStackTrace();
			}//end catching invalid path exceptions
		}//end looping over groups
	}//end saveGroups(groups, directory)

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

	/**
	 * When given the name of a subdirectory and a list of filenames,
	 * will create a directory of the specified name and ensure a file
	 * exists in that directory with each name specified. Gives a list
	 * of Paths to each of the files in the directory.
	 * @param filenames The list of filenames to ensure exist. These should
	 * be just the name of the file, but include an extension if needed.
	 * @param foldername The name fo the subdirectory.
	 * @return Returns a list of Paths to each of the files specified,
	 * or null if the operation fails at any point.
	 */
	public List<Path> setUpDirectories(List<String> filenames,
	String foldername){
		List<Path> filepaths = new ArrayList<Path>();
		try {
			// get current directory of jar
			Path currentDirectory = Paths.get(getClass().getProtectionDomain()
			.getCodeSource().getLocation().toURI().getPath());
			// make new directory for files
			Path newDirectory = currentDirectory.resolve(foldername);
			Files.createDirectories(newDirectory);
			// make file for each filename
			for(String filename : filenames){
				// figure out path for the new file
				Path newFile = newDirectory.resolve(filename);
				if(Files.notExists(newFile)){
					// if file doesn't exist, then make it
					Files.createFile(newFile);
				}//end if file doesn't exist
				// file should exist either way, so add to list
				filepaths.add(newFile);
			}//end looping over given filenames
		}//end trying to do file I/O stuff
		catch (URISyntaxException | IOException e) {
			e.printStackTrace();
			return null;
		}//end catching URISyntaxExceptions
		return filepaths;
	}//end setUpDirectories(filenames, foldername)
}//end class TimeStorageIO
