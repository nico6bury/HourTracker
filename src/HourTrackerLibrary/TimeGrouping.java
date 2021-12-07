package HourTrackerLibrary;

import java.util.ArrayList;

/**
 * Serves as a container for holding a bunch of TimedInstance objects
 * together.
 */
public class TimeGrouping {
    /**
     * The times associated with this group.
     */
    private ArrayList<TimedInstance> times = new ArrayList<TimedInstance>();
    /**
     * 
     * @return
     */
    public ArrayList<TimedInstance> getTimes(){
        return times;
    }//end getTimes()
    /**
     * 
     * @param times
     */
    public void setTimes(ArrayList<TimedInstance> times){
        this.times = times;
    }//end setTimes(times)
    
    /**
     * The name of this group.
     */
    private String name;
    /**
     * 
     * @return
     */
    public String getName(){
        return name;
    }//end getName()
    /**
     * 
     * @param name
     */
    public void setName(String name){
        this.name = name;
    }//end setName(name)

    /**
     * Just an empty group, calls empty constructor.
     * @return
     */
    public static TimeGrouping getEmptyGroup(){
        return new TimeGrouping();
    }//end getEmptyGroup()

    /**
     * The number of timed instances that exist in this group.
     * @return
     */
    public int getTimeCount(){
        return times.size();
    }//end getTimeCount()
    
    /**
     * Total number of minutes in class.
     * @return Duration.getSeconds() / 60.0
     */
    public double getTotalMinutes(){
        double total = 0;
        for(TimedInstance time : times){
            total += ((double)time.getDuration().getSeconds()) / 60.0;
        }//end looping over times in times
        return total;
    }//end getTotalMinutes()

    /**
     * Total number of hours in class.
     * @return Duration.getSeconds() / 360.0
     */
    public double getTotalHours(){
        double total = 0;
        for(TimedInstance time : times){
            total = ((double)time.getDuration().getSeconds()) / 360.0;
        }//end looping over times in times
        return total;
    }//end getTotalHours()

    /**
     * Initialize group as empty.
     */
    public TimeGrouping(){
        name = "Ungrouped";
    }//end no-arg constructor

    /**
     * Initialize group with list of instances.
     * @param times The list of TimedInstance objects to add to the group.
     */
    public TimeGrouping(ArrayList<TimedInstance> times){
        for(TimedInstance time : times){
            TimedInstance newTime = new TimedInstance(time);
            newTime.setCurrentGroup(this);
            this.times.add(newTime);
        }//end looping over times in times
    }//end 1-arg constructor

    /**
     * Initialize group with a name.
     * @param name The name of the group.
     */
    public TimeGrouping(String name){
        this.times = new ArrayList<TimedInstance>();
        this.name = name;
    }//end 1-arg constructor

    /**
     * Initialize group with name and list of instances.
     * @param name The name of the group.
     * @param times The list of TimedInstance objects to add to
     * the group.
     */
    public TimeGrouping(String name, ArrayList<TimedInstance> times){
        this.name = name;
        for(TimedInstance time : times){
            TimedInstance newTime = new TimedInstance(time);
            newTime.setCurrentGroup(this);
            this.times.add(newTime);
        }//end looping over times in times
    }//end 2-arg constructor

    /**
     * Checks if name of other TimeGrouping object is the same.
     * @param other The TimeGrouping object you wish to compare against
     * the current object.
     * @return Returns true if the objects have the same name, or
     * false otherwise.
     */
    public boolean equals(TimeGrouping other){
        return this.name.equals(other.name);
    }//end equals(group)

    /**
     * Returns the name of this group.
     */
    public String toString(){
        return this.name;
    }//end toString()

    // TODO: finish off all of the fileI/O stuff at some point
}//end class TimeGrouping
