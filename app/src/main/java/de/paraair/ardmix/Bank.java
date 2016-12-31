package de.paraair.ardmix;

import java.util.ArrayList;

/**
 * Created by onkel on 19.10.16.
 */

public class Bank implements Cloneable{

//    enum BankType { ALL, AUDIO, MIDI, BUS }

    private final ArrayList<Strip> strips = new ArrayList<>();
    private String name;
//    private BankType type = BankType.ALL;

    ToggleTextButton button = null;

    public Bank() {}

    public Bank(String name) {
        setName(name);
    }

    public class Strip {
        /** remote id of the contained track (1-n) */

        public int id;
        public String name;
        public boolean enabled;
        public Track.TrackType type;
    }

    public ToggleTextButton getButton() {
        return button;
    }

    public int getStripPosition(int iSendsLayout) {
        for(int i = 0; i < strips.size(); i++)
            if (iSendsLayout == strips.get(i).id)
                return i;
        return -1;
    }

    public int getStripCount() {
        return strips.size();
    }


    public void add(String name, int remoteId, boolean enabled, Track.TrackType type) {
        Strip strip = new Strip();
        strip.id = remoteId;
        strip.name = name;
        strip.enabled = enabled;
        strip.type = type;
        int insert_index = 0;
        for(Strip p: strips) {
            if (p.id < remoteId)
                insert_index++;
        }
        strips.add(insert_index, strip);
    }

    public boolean contains(int remoteId) {
        for(Strip s: strips)
            if (s.id == remoteId)
                return true;
        return false;
    }

    public void remove(int id) {
        for( Strip s: strips){
            if( s.id == id) {
                strips.remove(s);
                return;
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if( button != null )
            button.setAllText(name);
        this.name = name;
    }

    public ArrayList<Strip> getStrips() {
        return strips;
    }


    Bank GetClone() {
        Bank clone = new Bank(name);
        for(Strip strip: strips ) {
            clone.add(strip.name, strip.id, strip.enabled, strip.type);
        }
        return clone ;
    }
}
