package HourTrackerLibrary;

import java.time.*;
import java.lang.reflect.*;

/**
 * This class represents one instance of time over which
 * something took place.
 */
public class TimedInstance {
    /**
     * The start time for this instance.
     */
    protected Instant start = Instant.now();
    /**
     * 
     * @return
     */
    public Instant getStart() {
        if(handleSpecificBeginEnd){
            return start;
        }//end if we're handling specific start or end times
        else{
            return null;
        }//end else we're not handling specific start or end
    }//end getStart()
    /**
     * 
     * @param start
     */
    public void setStart(Instant start){
        this.start = start;
    }//end setStart(instant)

    /**
     * The end time for this instance.
     */
    protected Instant end = Instant.now();
    /**
     * 
     * @return
     */
    public Instant getEnd(){
        if(handleSpecificBeginEnd){
            return end;
        }//end if we're handling specific start or end
        else{
            return null;
        }//end else we're not handlling specific start or end
    }//end getEnd()
    /**
     * 
     * @param end
     */
    public void setEnd(Instant end){
        this.end = end;
    }//end setEnd(end)

    /**
     * The duration of this instance.
     */
    protected Duration duration = Duration.ofSeconds(0);
    /**
     * 
     * @return
     */
    public Duration getDuration(){
        if(handleSpecificBeginEnd){
            return duration;
        }//end if we're handling specific start or end
        else{
            return Duration.between(start, end);
        }//end else we're not handling specific start or end
    }//end getDuration()
    /**
     * 
     * @param duration
     */
    public void setDuration(Duration duration){
        if(handleSpecificBeginEnd){
            this.end = start.plus(duration);
        }//end if we're handling specific start or end
        else{
            this.duration = duration;
        }//end else we're not handling specific start or end
    }//end setDuration(duration)
    
    /**
     * The name of this instance, likely as set arbitrarily
     * by the user.
     */
    protected String name = "Unnamednstance";
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
     */
    private TimeGrouping currentGroup = TimeGrouping.getEmptyGroup();
    /**
     * 
     * @return
     */
    public TimeGrouping getCurrentGroup(){
        return currentGroup;
    }//end getCurrentGroup()
    /**
     * 
     * @param group
     */
    public void setCurrentGroup(TimeGrouping group){
        this.currentGroup = group;
    }//end setCurrentGroup(group)
    /**
     * 
     * @return
     */
    public String getCurrentGroupName(){
        if(currentGroup == null) return null;
        else return currentGroup.getName();
    }//end getCurrentGroupName()

    /**
     * Whether or not we should worry about the exact beginning
     * or end of this instance. If this is false, we just care
     * about the duration basically.
     */
    protected boolean handleSpecificBeginEnd = true;
    /**
     * 
     * @return
     */
    public boolean getHandleSpecificBeginEnd(){
        return handleSpecificBeginEnd;
    }//end getHandleSpecificBeginEnd()
    /**
     * 
     * @param handleSpecificBeginEnd
     */
    public void setHandleSpecificBeginEnd(
        boolean handleSpecificBeginEnd){
        this.handleSpecificBeginEnd = handleSpecificBeginEnd;
    }//end setHandleSpecificBeginEnd(handleSpecificBeginEnd)

    /**
     * Whether or not we should worry about the date when
     * outputting strings and stuff. If this is false, we'll
     * just do time instead of doing date.
     */
    protected boolean handleDate = true;
    /**
     * 
     * @return
     */
    public boolean getHandleDate(){
        return handleDate;
    }//end getHandleDate()
    /**
     * 
     * @param handleDate
     */
    public void setHandleDate(boolean handleDate){
        this.handleDate = handleDate;
    }//end setHandleDate(handleDate)

    /**
     * Initializes specific start and end times for this instance.
     * Assumes you are using a specific beginning and end for this object.
     * @param start The starting time for this instance.
     * @param end The ending time for this instance.
     */
    public TimedInstance(Instant start, Instant end){
        this.start = start;
        this.end = end;
        handleSpecificBeginEnd = true;
    }//end 2-arg specific constructor

    /**
     * Initializes arbitrary start and end times but specifies
     * a duration based on inputted hours and minutes. Assumes you
     * don't cate about specific beginning or end, and also assumes
     * you don't care about date.
     * @param hours The number of whole hours in the duration.
     * @param minutes The number of leftover minutes after hours
     * for this duration.
     */
    public TimedInstance(int hours, int minutes){
        this.start = Instant.now();
        this.duration = Duration.ofMinutes(minutes +
        (hours * 60));
        this.end = this.start.plus(this.duration);
        handleSpecificBeginEnd = false;
        handleDate = false;
    }//end 2-arg non-specific constructor

    /**
     * Creates this object as a copy of the supplied instance.
     * @param instance The object you wish to copy.
     */
    public TimedInstance(TimedInstance instance){
        for(Field field : getClass().getFields()){
            try{
                field.set(this, field.get(instance));
            }//end trying to set field
            catch(IllegalAccessException e){
                e.printStackTrace();
            }//end catching illegal access exceptions
        }//end looping over fields in this class
    }//end copy constructor
    
    /**
     * 
     * @param other
     * @return
     */
    public boolean equals(TimedInstance other){
        return this.handleDate == other.handleDate &&
            this.handleSpecificBeginEnd == other.handleSpecificBeginEnd &&
            this.start.equals(other.start) &&
            this.end.equals(other.end)
            && this.currentGroup == other.currentGroup;
    }//end equals(other)

    // TODO: make sure to do fileIO stuff later
}//end class TimedInstance
