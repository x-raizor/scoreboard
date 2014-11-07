import static org.junit.Assert.assertEquals;

public class EventQueueTest {

    @org.junit.Test
    public void testAdd() throws Exception {
        EventQueue eq = new EventQueue();
        eq.add("test of the world", 10, 20);
        assertEquals(1, eq.size());
        eq.add("and other", 20, 30);
        assertEquals(2, eq.size());
        eq.next();
    }

    @org.junit.Test
    public void testPop() throws Exception {
        EventQueue eq = new EventQueue();
        eq.add("test of the world", 10, 20);
        eq.add("other", 30, 40);
        eq.add("and other", 20, 30);
        assertEquals(10, eq.next().from());
        assertEquals(20, eq.next().from());
        assertEquals(30, eq.next().from());

    }


}