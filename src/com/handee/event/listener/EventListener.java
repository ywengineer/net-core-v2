package com.handee.event.listener;

import com.handee.event.Event;

public interface EventListener<T extends Event> {
	void on(T event);
}
