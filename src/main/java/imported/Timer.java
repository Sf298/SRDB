/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imported;

import java.util.Calendar;

/**
 *
 * @author Saud
 */
public class Timer {
    
    private Calendar startTime;
    private Calendar lastLapStart;
    private String name = null;
    
    public Timer() {
        startTime = Calendar.getInstance();
        lastLapStart = startTime;
    }
    
    public Timer(String name) {
        this();
        System.out.println("Started timer: \""+name+"\"");
        this.name = name;
    }
    
    public long split() {
        long time = splitTime();
        if(name!=null) System.out.println("Timer Split: \""+name+"\" ("+time+"ms)");
        return time;
    }
    private long splitTime() {
        return Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis();
    }
    
    public long lap() {
        long time = lapTime();
        if(name!=null) System.out.println("Timer Lap: \""+name+"\" ("+time+"ms)");
        return time;
    }
    private long lapTime() {
        Calendar curr = Calendar.getInstance();
        long out = curr.getTimeInMillis() - lastLapStart.getTimeInMillis();
        lastLapStart = curr;
        return out ;
    }
    
    public void print() {
        System.out.println("Timer: \""+name+"\" (Lap: "+lapTime()+", Split: "+splitTime()+")ms");
    }
    
}
