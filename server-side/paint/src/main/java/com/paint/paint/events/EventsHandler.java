package com.paint.paint.events;

import java.util.ArrayList;

public class EventsHandler {
    private int currentEventIndex = -1;
    private ArrayList<Event> events = new ArrayList<>();


    public void addEvent(Event event){
        // remove all events after the currentEventIndex
        if (currentEventIndex < events.size() - 1){
            for (int i = events.size() - 1; i > currentEventIndex; i--){
                events.remove(i);
            }
        }

        currentEventIndex++;
        events.add(event);
    }

    public void undo(){
        if (currentEventIndex >= 0){

            Event currentEvent = events.get(currentEventIndex);
            currentEvent.revert();
            currentEventIndex--;

            // check if currentEvent is of type SelectionChangeEvent
            if (currentEventIndex >= 0 && currentEvent instanceof SelectionChangeEvent){
                events.get(currentEventIndex).revert();
                currentEventIndex--;
            }
        }
    }

    public void redo(){
        if (currentEventIndex < events.size() - 1){
            currentEventIndex++;
            Event currentEvent = events.get(currentEventIndex);
            currentEvent.apply();
            // check if currentEvent is of type SelectionChangeEvent
            if (currentEventIndex < events.size() - 1 && currentEvent instanceof SelectionChangeEvent){
                currentEventIndex++;
                events.get(currentEventIndex).apply();
            }
        }
    }

    public void clear(){
        events.clear();
        currentEventIndex = -1;
    }

    public boolean canUndo(){
        return currentEventIndex >= 0;
    }

    public boolean canRedo(){
        return currentEventIndex < events.size() - 1;
    }

    

    public int size(){
        return events.size();
    }

    public ArrayList<Event> getEvents() {
        return events;
    }
}
