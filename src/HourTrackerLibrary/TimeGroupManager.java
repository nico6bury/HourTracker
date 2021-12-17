package HourTrackerLibrary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.List;

/**
 * Basically serves as an interface of a bunch of TimedInstance objects
 * contained in TimeGrouping objects.
 */
public class TimeGroupManager {
    /**
     * All the groups of times for this object.
     */
    private List<TimeGrouping> groups = new ArrayList<TimeGrouping>();
    /**
     * 
     * @return
     */
    public List<TimeGrouping> getGroups() {
        return groups;
    }//end getGroups()
    /**
     * 
     * @param groups
     */
    public void setGroups(List<TimeGrouping> groups) {
        this.groups = groups;
    }//end setGroups(groups)
    /**
     * Adds a group to the manager.
     * @param group The group to add.
     */
    public void addGroup(TimeGrouping group){
        this.groups.add(group);
    }//end addGroup(group)
    /**
     * Removes a specific group.
     * @param group The group to remove.
     * @return Returns true if the operation succeeded, false if it didn't.
     * Method also returns false if the specified group was not in this
     * manager.
     */
    public boolean removeGroup(TimeGrouping group){
        return this.groups.remove(group);
    }//end removeGroup(group)
    /**
     * Removes a group at the specified index.
     * @param index The index of the group to remove.
     * @return Returns the group that was at the specified position.
     */
    public TimeGrouping removeGroupAt(int index){
        return this.groups.remove(index);
    }//end removeGroupAt(index)

    /**
     * Gets the TimedInstance sorta at the index.
     * @param index
     * @return Returns either the TimedInstance at the index or null
     * if we can't find it.
     */
    public TimedInstance getInstance(int index){
        int counter = 0;
        for(TimeGrouping group : groups){
            for(TimedInstance time : group.getTimes()){
                if(counter == index){
                    return time;
                }//end if we found the right time;
                counter++;
            }//end looping over times
        }//end looping over groups
        return null;
    }//end getInstance(index)

    public List<TimedInstance> getTimes(){
        List<TimedInstance> tempTimes = new ArrayList<TimedInstance>();
        for(TimeGrouping group : groups){
            for(TimedInstance time : group.getTimes()){
                tempTimes.add(time);
            }//end looping over times
        }//end looping over groups
        return tempTimes;
    }//end getTimes()

    /**
     * Initializes the list of groups.
     */
    public TimeGroupManager(){
        groups = new ArrayList<TimeGrouping>();
    }//end no-arg constructor

    /**
     * Adds the specified times to a group with the specified name.
     * If the group doesn't exist, it will be created.
     * @param time The time to add.
     * @param groupName The name of the group.
     * @return Returns true if we found an existing group to add
     * the time to, or false if a new group was created to put the
     * time into.
     */
    public boolean addTime(TimedInstance time, String groupName){
    boolean foundExistingGroup = false;
        int foundGroupIndex = -1;

        for(int i = 0; i < groups.size(); i++){
            if(groupName == groups.get(i).getName()){
                foundExistingGroup = true;
                foundGroupIndex = i;
                break;
            }//end if we found an existing group
        }//end looping over all the groups

        if(foundExistingGroup){
            groups.get(foundGroupIndex).getTimes().add(time);
        }//end if we found an existing group to add to
        else{
            foundGroupIndex = groups.size();
            groups.add(new TimeGrouping(groupName));
            groups.get(foundGroupIndex).getTimes().add(time);
        }//end else we need to make a new group

        return foundExistingGroup;
    }//end addTime()

    /**
     * Removes times from each of the indices supplied.
     * @param indices The indices of TimedInstances to remove.
     */
    public void removeTimes(TreeSet<Integer> indicesToRemove){
        if(indicesToRemove == null || indicesToRemove.size() <= 0) return;
        int[] indices = new int[indicesToRemove.size()];
        int helpfulCounter = 0;
        for(int i : indicesToRemove){
            indices[helpfulCounter] = i;
            helpfulCounter++;
        }//end looping adding everything in indicesToRemove to indices
        int counter = 0;
        int curIndexIndex = 0;

        HashMap<TimedInstance, TimeGrouping> groupTimePairs =
            new HashMap<TimedInstance, TimeGrouping>();
        for(TimeGrouping group : groups){
            for(TimedInstance time : group.getTimes()){
                if(counter == indices[curIndexIndex]){
                    groupTimePairs.put(time, group);
                    if(curIndexIndex < indices.length - 1)
                        curIndexIndex++;
                }//end if we found the right index
                counter++;
            }//end looping over each time
        }//end looping over each group

        for(TimedInstance time : groupTimePairs.keySet()){
            groupTimePairs.get(time).getTimes().remove(time);
        }//end looping over each timed instance
    }//end removeTimes(indices)

    /**
     * When given a list of file paths containing information
     * from TimeGrouping objects (one file per TimeGrouping), reads
     * groups from each of the filepaths given.
     * @param filepaths
     */
    public void deserialize(List<List<String>> fileLines){
        for(List<String> lines : fileLines){
            TimeGrouping nextGroup = new TimeGrouping();
            nextGroup.deserialize(lines);
            this.groups.add(nextGroup);
        }//end looping over lines per file
    }//end deserialize()

    /**
     * Serializes this object into a list of serialized groups, with
     * each List of Strings being a separate group that should be put
     * in a separate file.
     * @return Returns a List of serialized groups, with each group being
     * in the form of a List of strings.
     */
    public List<List<String>> serialize(){
        List<List<String>> serial = new ArrayList<List<String>>();
        for(TimeGrouping group : groups){
            serial.add(group.serialize());
        }//end looping over groups in this manager
        return serial;
    }//end serialize(workingDirectory)

    /**
     * Attempts to merge groups at specified indices.
     * @param index1 The index of first group to merge. Must be smaller
     * than index2 and a valid index in the groups list.
     * @param index2 The index of second group to merge. Must be higher
     * than index1 and a valid index in the groups list.
     * @param newName The name of the new group that will be created
     * by merging the specified groups.
     * @param keepOldGroups Whether or not we should keep the groups
     * used to create a merged group or remove them.
     * @throws ArrayIndexOutOfBoundsException Exception thrown if the
     * index1 and index2 parameters are invalid. They must both be valid
     * indices of the group list, and index1 must be less than index2.
     */
    public void mergeGroups(int index1, int index2,
    String newName, boolean keepOldGroups){
        // check to make sure our input isn't too bad
        if(index1 > 0 || index1 < index2 || index1 < groups.size() ||
            index2 > 0 || index2 < groups.size()){
            throw new ArrayIndexOutOfBoundsException("One or both of the " +
            "provided indices are invalid. Can't find them in groups list" +
            " or index1 might be higher than index2.");
        }//end if we have invalid indice(s)
        // now we can get into the meat of things
        List<TimedInstance> mergedTimes = new ArrayList<TimedInstance>();
        for(TimedInstance time : groups.get(index1).getTimes()){
            mergedTimes.add(time);
        }//end adding all the times from first group
        for(TimedInstance time : groups.get(index2).getTimes()){
            mergedTimes.add(time);
        }//end adding all the times from second group
        TimeGrouping newGroup = new TimeGrouping(newName, mergedTimes);
        if(!keepOldGroups){
            // remove both the indices provided in right order
            this.groups.remove(index2);
            this.groups.remove(index1);
        }//end if we should delete the old groups
        this.groups.add(newGroup);
    }//end mergeGroups(index1, index2, newName, keepOldgroups)

    // TODO: Make sure that merge method auto-merges groups with same name

    /**
     * Attempts to merge groups at specified indices.
     * @param group1 The first group to merge. Must both be in the 
     * groups list and appear before group2 in that list.
     * @param group2 The second group to merge. Must both be in the
     * groups list and appear after group1 in that list.
     * @param newName The name of the new group that will be created
     * by merging the specified groups.
     * @param keepOldGroups Whether or not we should keep the groups
     * used to create a merged group or remove them.
     * @throws IllegalArgumentException This exception is thrown
     * either if we can't find one or both of the groups given, or
     * if group2 appears before group1 in the groups list.
     */
    public void mergeGroups(TimeGrouping group1, TimeGrouping group2,
    String newName, boolean keepOldGroups){
        // check to make sure our input is correct
        if(!groups.contains(group1) || !groups.contains(group2)){
            throw new IllegalArgumentException("One or both of the " +
            "specified groups do not exist. Please supply two groups " +
            "that exist in this TimeGroupManager.");
        }//end if the specified groups don't exist
        // get the indices for the groups
        int index1 = groups.indexOf(group1);
        int index2 = groups.indexOf(group2);
        try{
            mergeGroups(index1, index2, newName, keepOldGroups);
        }//end trying to use the other version of overloaded method
        catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
            throw new IllegalArgumentException("Either we couldn't find " +
            "one or both of the groups given, or group2 appears before " +
            "group1. Given where things went wrong though, it\'s probably" +
            " the latter though.");
        }//end catching ArrayindexOutOfBoundsExceptions
    }//end mergegroups(group1, group2, newName, keepOldGroups)
}//end class TimeGroupManager
