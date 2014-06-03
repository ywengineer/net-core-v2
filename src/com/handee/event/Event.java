package com.handee.event;

import com.handee.event.emitter.EventEmitter;

public class Event {
	public static final String EVENT_NEW_LISTENER = "newListener";
	public static final String EVENT_REMOVE_LISTENER = "removeListener";
	protected String type;
	protected EventEmitter target;

	public Event(String type) {
		this.type = type;
	}

	public String getType() {
		return this.type;
	}

	public void setEmitter(EventEmitter emmiter) {
		this.target = emmiter;
	}

	public EventEmitter emitter() {
		return this.target;
	}
}
