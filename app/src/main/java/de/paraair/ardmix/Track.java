/**
 * 
 */
package de.paraair.ardmix;

/**
 * @author lincoln
 *
 */
public class Track extends Fader {


    public int bank;

    enum TrackType  {
		MASTER, AUDIO, MIDI, BUS, SEND, RECEIVE, PAN
    }

	public int remoteId;

	public TrackType type;
	public String name;
	public int source_id = 0;
	public int trackVolume = 0;
    public float panPosition = 0.5f;
	public int meter;
	
	public boolean recEnabled = false;
	public boolean soloEnabled = false;
	public boolean muteEnabled = false;
	public boolean stripIn = false;
	public boolean soloIsolateEnabled = false;

	// private
	private boolean trackVolumeOnSeekBar = false;
	//helper

	public int sendCount = 0;


	public void setTrackVolumeOnSeekBar(boolean val){
		trackVolumeOnSeekBar = val;
	}
	public boolean getTrackVolumeOnSeekBar(){
		return trackVolumeOnSeekBar;
	}

}
