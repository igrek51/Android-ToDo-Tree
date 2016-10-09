package igrek.todotree.logic.controller.dispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import igrek.todotree.logger.Logs;

//TODO ? dispatcher bez kolejki, natychmiastowe wykonanie eventu + odpowiedź, IRequest

public class EventDispatcher {

    private Map<Class<? extends IEvent>, List<IEventObserver>> eventObservers;

    private List<IEvent> eventsQueue;

    private boolean dispatching = false;

    public EventDispatcher() {
        eventObservers = new HashMap<>();
        eventsQueue = new ArrayList<>();
    }

    public void registerEventObserver(Class<? extends IEvent> eventClass, IEventObserver observer) {
        List<IEventObserver> observers = eventObservers.get(eventClass);
        if (observers == null) {
            observers = new ArrayList<>();
        }
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
        eventObservers.put(eventClass, observers);
    }

    public void sendEvent(IEvent event) {
        eventsQueue.add(event);
        dispatchEvents();
    }

    public void clearEventObservers(Class<? extends IEvent> eventClass) {
        List<IEventObserver> observers = eventObservers.get(eventClass);
        if (observers != null) {
            observers.clear();
        }
        eventObservers.put(eventClass, null);
    }

    private void dispatchEvents() {
        if (dispatching) return;
        dispatching = true;

        while (!eventsQueue.isEmpty()) {
            dispatch(eventsQueue.get(0));
            eventsQueue.remove(0);
        }

        dispatching = false;
    }

    private void dispatch(IEvent event) {
        List<IEventObserver> observers = eventObservers.get(event.getClass());
        if (observers == null || observers.isEmpty()) {
            Logs.warn("no observer for event " + event.getClass().getName());
        }
        if (observers != null) {
            for (IEventObserver observer : observers) {
                observer.onEvent(event);
            }
        }
    }
}
