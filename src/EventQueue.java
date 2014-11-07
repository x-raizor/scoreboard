import java.util.PriorityQueue;

/**
 * Created by andrew on 12.08.14.
 */

public class EventQueue {
    private PriorityQueue<Event> events; // MinPQ

    public EventQueue() {
        events = new PriorityQueue<Event>();
    }

    public int size(){
        return events.size();
    }

    public boolean isEmpty(){
        return events.isEmpty();
    }

    public void add(Event event) {
        events.add(event);
    }

    public void add(String text, int t0, int t1) {
        events.add(new Event(text, t0, t1));
    }

    public Event next() { // return and delete the extremum from the queue
        return events.poll();
    } // return the nearest event and delete it from the queue


}
