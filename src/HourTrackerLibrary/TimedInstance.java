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
        this.end = Instant.now();
        this.duration = Duration.ofMinutes(minutes +
        (hours * 60));
        this.start = this.end.minus(this.duration);
        handleSpecificBeginEnd = false;
        handleDate = false;
    }//end 2-arg non-specific constructor

    /**
     * Initializes arbitrary start and end times but specifies
     * a duration based on the one provided. Assumes you don't care
     * about specific beginning or end, and also assumes you don't
     * care about date.
     * @param duration The duration to initialize the object with.
     */
    public TimedInstance(Duration duration){
        this.duration = duration;
        this.end = Instant.now();
        this.start = this.end.minus(duration);
        handleSpecificBeginEnd = false;
        handleDate = false;
    }//end 1-arg non-specific constructor

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
     * Initializes this object based on a serialized string.
     * Literally just deserialize method in constructor form.
     * @param serial The serialized TimedInstance object to be
     * used for conversion.
     * @see #deserialize(String)
     * @see #serialize()
     */
    public TimedInstance(String serial){
        deserialize(serial);
    }//end 1-arg deserialization constructor

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

    /**
     * Returns string representation of this object, respecting
     * handleSpecificBeginEnd and handleDate in terms of formatting.
     */
    public String toString(){
        StringBuilder sb = new StringBuilder();
        if(handleSpecificBeginEnd){
            if(handleDate){
                sb.append(start + " to " + end);
            }//end if we should print the date
            else{
                sb.append(start + " to " + end);
            }//end else we shouldn't print the date
        }//end if we're handling specific start or end
        else{
            Duration localDuration = getDuration();
            long hours = localDuration.getSeconds() / 360;
            long minutes = localDuration.getSeconds() / 60;
            sb.append(hours + " hours and " + minutes + " minutes");
        }//end else we just want the duration
        return sb.toString();
    }//end toString()

    /**
     * Serializes this object into a string.
     * @return A serialized string which can be used to rebuild
     * this object with its current values.
     */
    public String serialize(){
        StringBuilder sb = new StringBuilder();

        // add name to string
        sb.append("name:" + this.name + "|");
        // add booleans to string
        sb.append("handleSpecificBeginEnd:" +
            this.handleSpecificBeginEnd + "|");
        sb.append("handleDate:" + this.handleDate + "|");
        // add start to string
        sb.append("start:" + this.start + "|");
        // add end to string
        sb.append("end:" + this.end + "|");

        return sb.toString();
    }//end formatForFile()

    /**
     * Deserializes a string in order to read its contents back
     * into this object.
     * @param serial
     */
    public void deserialize(String serial){
        String[] serialComponents = serial.split("|");
        for(String component : serialComponents){
            String[] componentComponents = component.split(":");
            if(componentComponents.length == 2){
                try{
                    Field componentField = getClass()
                        .getField(componentComponents[0]);
                    if(componentField.canAccess(this)){
                        // figure out the type and convert it accordingly
                        if(componentField.getType() == Instant.class){
                            Instant value = Instant
                                .parse(componentComponents[1]);
                            componentField.set(this, value);
                        }//end if we're working with an instant
                        else if(componentField.getType() == Duration.class){
                            Duration value = Duration
                                .parse(componentComponents[1]);
                            componentField.set(this, value);
                        }//end else if we're working with a duration
                        else if(componentField.getType() == String.class){
                            componentField.set(this, componentComponents[1]);
                        }//end else if we're working with a string
                        else if(componentField.getType() == boolean.class){
                            boolean value = Boolean
                                .parseBoolean(componentComponents[1]);
                            componentField.set(this, value);
                        }//end else if we're working with a boolean
                    }//end if we can access the field in question
                }//end trying to get the corresponding field
                catch(NoSuchFieldException e){
                    e.printStackTrace();
                }//end catching noSuchFieldExceptions
                catch(IllegalAccessException e){
                    e.printStackTrace();
                }//end catching illegalAccessExceptions
            }//end if the component was parsed successfully
        }//end looping over each component of the line
    }//end deserialize(serial)
}//end class TimedInstance
