/**
 * Created by andrew on 13.08.14.
 */
public class Event implements Comparable<Event> {
    private int startTime, endTime;
    private String name;

    public Event(String name, int t0, int t1) {
        this.startTime = t0;
        this.endTime = t1;
        this.name = name;
    }
    /* getters */
    public int from(){return startTime;}
    public int to(){return endTime;}
    public String name(){return name;}

    public String toString() {
        return name + " from " + Integer.toString(startTime) + " to " + Integer.toString(endTime);
    }

    /* for queue comparator */
    public int compareTo(Event e) {
        if (this.startTime < e.startTime) return -1;
        if (this.startTime > e.startTime) return 1;
        return 0;
    }
}