package HourTrackerLibrary;

import java.util.ArrayList;

public class TimeGrouping {
    /**
     * 
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
     * 
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
     * 
     * @return
     */
    public int getTimeCount(){
        return times.size();
    }//end getTimeCount()
    
    /**
     * 
     * @return
     */
    public double getTotalMinutes(){
        double total = 0;
        for(TimedInstance time : times){
            total += ((double)time.getDuration().getSeconds()) / 60.0;
        }//end looping over times in times
        return total;
    }//end getTotalMinutes()

    /**
     * 
     * @return
     */
    public double getTotalHours(){
        double total = 0;
        for(TimedInstance time : times){
            total = ((double)time.getDuration().getSeconds()) / 360.0;
        }//end looping over times in times
        return total;
    }//end getTotalHours()

    /**
     * 
     */
    public TimeGrouping(){
        name = "Ungrouped";
    }//end no-arg constructor

    /**
     * 
     * @param times
     */
    public TimeGrouping(ArrayList<TimedInstance> times){
        for(TimedInstance time : times){
            TimedInstance newTime = new TimedInstance(time);
            newTime.setCurrentGroup(this);
            this.times.add(newTime);
        }//end looping over times in times
    }//end 1-arg constructor

    /**
     * 
     * @param name
     */
    public TimeGrouping(String name){
        this.times = new ArrayList<TimedInstance>();
        this.name = name;
    }//end 1-arg constructor

    /**
     * 
     * @param name
     * @param times
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
     * @param other
     * @return
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
