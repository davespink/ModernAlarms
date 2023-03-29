package com.example.modernalarms;
 /*
 * Created by User on 3/14/2017.
 */

public class Alarm {
    private long id;
    private long start;

    private String description;

    private String sound;

    public Alarm(long id, long start,  String description, String sound) {
        this.id = id;
        this.start = start;
        if (sound.length()==0)
            sound = "blockbuster";
        this.sound = sound;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setSound(String sound) { this.sound = sound; }
    public String getSound() { return this.sound; }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
