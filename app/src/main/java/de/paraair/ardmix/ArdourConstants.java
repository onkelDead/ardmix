package de.paraair.ardmix;

/**
 * Created by onkel on 06.10.16.
 */

final class ArdourConstants {
    // Ardour strip type constants
    public static final int STRIP_TRACK_AUDIO = 1;
    public static final int STRIP_TRACK_MIDI = 2;
    public static final int STRIP_BUS_AUDIO = 4;
    public static final int STRIP_BUS_MIDI = 8;
    public static final int STRIP_VCA = 16;
    public static final int STRIP_MASTER = 32;
    public static final int STRIP_MONITOR = 64;
    public static final int STRIP_AUX = 128;
    public static final int STRIP_SELECTED = 256;
    public static final int STRIP_HIDDEN = 512;

    // Ardout feedback constants
    public static final int FEEDBACK_STRIP_BUTTONS = 1;
    public static final int FEEDBACK_STRIP_VALUES = 2;
    public static final int FEEDBACK_SSID_IN_PATH = 4;
    public static final int FEEDBACK_HEARTBEAT = 8;
    public static final int FEEDBACK_MASTER = 16;
    public static final int FEEDBACK_BAR_BEAT = 32;
    public static final int FEEDBACK_TIMECODE = 64;
    public static final int FEEDBACK_STRIP_METER = 128;
    public static final int FEEDBACK_STRIP_METER_16BIT = 256;
    public static final int FEEDBACK_STRIP_SIGNAL_PRESENT = 512;
    public static final int FEEDBACK_TRANSPORT_POSITION_SAMPLES = 1024;
    public static final int FEEDBACK_TRANSPORT_POSITION_TIME = 2048;
    public static final int FEEDBACK_EXTRA_SELECT = 8192;


    // Ardour msg.what values
    public static final int OSC_STRIP_NAME = 50;
    public static final int OSC_STRIP_REC = 60;
    public static final int OSC_STRIP_MUTE = 70;
    public static final int OSC_STRIP_SOLO = 80;
    public static final int OSC_STRIP_SOLOISO = 81;
    public static final int OSC_STRIP_SOLOSAFE = 82;
    public static final int OSC_STRIP_PAN = 100;
    public static final int OSC_MAXFRAMES = 400;
    public static final int OSC_FRAMERATE = 500;
    public static final int OSC_STRIPLIST = 1000;
    public static final int OSC_STRIP_FADER = 90;
    public static final int OSC_STRIP_INPUT = 110;


    public static final int OSC_UPDATE_CLOCK = 3000;
    public static final int OSC_RECORD = 3500;
    public static final int OSC_PLAY = 3600;
    public static final int OSC_STOP = 3700;

    public static final int OSC_STRIP_METER = 6000;

    public static final int OSC_STRIP_SENDS = 7000;
    public static final int OSC_SELECT_SEND_FADER = 7500;
    public static final int OSC_SELECT_SEND_ENABLE = 7600;

    public static final int OSC_STRIP_RECEIVES = 8000;
    public static final int OSC_NEWSTRIP = 1010;

    public static final int OSC_PLUGIN_LIST = 4010;
    public static final int OSC_PLUGIN_DESCRIPTOR = 4000;


    // constants about Ardour Transport
    public static final byte TRANSPORT_STOPPED = 0x01;
    public static final byte TRANSPORT_RUNNING = 0x02;
    public static final byte RECORD_ENABLED = 0x04;


    public static final int OSC_SELECT_SEND_NAME = 7100;
    public static final int OSC_STRIP_SELECT = 300;
}

