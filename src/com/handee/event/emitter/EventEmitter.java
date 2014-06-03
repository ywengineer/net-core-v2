package com.handee.event.emitter;

import com.handee.event.Event;
import com.handee.event.listener.EventListener;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public abstract class EventEmitter {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(EventEmitter.class);
    private static final int defaultMaxListeners = 30;
    protected volatile Map<String, List<EventListener>> events;
    protected int maxListeners;

    // this.events = this.events || {};
    // this._maxListeners = this._maxListeners || defaultMaxListeners;
    public EventEmitter() {
        this.events = new HashMap<>();
        this.maxListeners = defaultMaxListeners;
    }

    public void setMaxListeners(int value) {
        if (value > 0) {
            this.maxListeners = value;
        }
    }

    @SuppressWarnings("unchecked")
    public void emmit(Event event) {
        if (event == null) {
            logger.warn("Event can not be null.");
            return;
        }

        // If there is no 'error' event listener then throw.
        if (events.containsKey(event.getType())) {
            for (EventListener listener : events.get(event.getType())) {
                event.setEmitter(this);
                listener.on(event);
            }
        } else {
            logger.warn("Uncaught, unspecified '" + event.getType() + "' event.");
        }
    }

    /**
     * @param type     事件类型
     * @param listener 事件监听器
     */
    public void addListener(String type, EventListener listener) {

        if (listener == null) {
            logger.warn("listener can not be null");
            return;
        }

        if (maxListeners > 0 && this.listenerCount(type) >= maxListeners) {
            logger.warn("warning: possible EventEmitter memory leak detected. " + maxListeners + " listeners added. Use emitter.setMaxListeners() to increase limit.");
            return;
        }

        // To avoid recursion in the case that type === "newListener"! Before
        // adding it to the listeners, first emit "newListener".
        if (events.containsKey(Event.EVENT_NEW_LISTENER)) {
            Event event = new Event(Event.EVENT_NEW_LISTENER);
            event.setEmitter(this);
            this.emmit(event);
        }

        if (!events.containsKey(type)) {
            events.put(type, new ArrayList<EventListener>());
        }

        events.get(type).add(listener);
    }

    /**
     * @param type     事件类型
     * @param listener 事件监听器
     */
    public void removeListener(String type, EventListener listener) {
        if (!this.events.containsKey(type)) {
            return;
        }

        List<EventListener> listeners = this.listeners(type);

        if (listeners.indexOf(listener) > -1) {
            listeners.remove(listener);
        }
        Event event = new Event(Event.EVENT_REMOVE_LISTENER);
        event.setEmitter(this);
        this.emmit(event);
    }

    /**
     * @param type 事件类型
     */
    public void removeListeners(String type) {
        List<EventListener> listeners = this.listeners(type);
        if (listeners != null) {
            listeners.clear();
        }
        this.events.remove(type);
    }

    /**
     *
     */
    public void removeAllListeners() {
        for (String type : this.events.keySet()) {
            List<EventListener> listeners = this.events.get(type);
            if (listeners != null) {
                listeners.clear();
            }
        }
        this.events.clear();
    }

    public void on(String type, EventListener listener) {
        this.addListener(type, listener);
    }

    public List<EventListener> listeners(String type) {
        return this.events.get(type);
    }

    public int listenerCount(String type) {
        List<EventListener> listeners = this.listeners(type);
        if (listeners == null) {
            return 0;
        } else {
            return listeners.size();
        }
    }
}
