package HourTrackerLibrary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * Basically serves as an interface of a bunch of TimedInstance objects
 * contained in TimeGrouping objects.
 */
public class TimeGroupManager {
    /**
     * All the groups of times for this object.
     */
    private ArrayList<TimeGrouping> groups = new ArrayList<TimeGrouping>();
    /**
     * 
     * @return
     */
    public ArrayList<TimeGrouping> getGroups() {
        return groups;
    }//end getGroups()
    /**
     * 
     * @param groups
     */
    public void setGroups(ArrayList<TimeGrouping> groups) {
        this.groups = groups;
    }//end setGroups(groups)

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

    public ArrayList<TimedInstance> getTimes(){
        ArrayList<TimedInstance> tempTimes = new ArrayList<TimedInstance>();
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
}//end class TimeGroupManager
