package de.paraair.ardmix;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static de.paraair.ardmix.ArdourConstants.*;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "ArdourControl";

    private Context context;

    // application settings
    private String oscHost = "127.0.0.1";
    private int oscPort = 3819;
    private int bankSize = 8;
    private boolean useSendsLayout = false;

    private OscService oscService = null;


    // Ardour session values
    private Long maxFrame;
    private Long frameRate;
    private byte transportState = TRANSPORT_STOPPED;

    private BankLoadDialog dfBankLoad = null;

    // top level IO elements
    private ImageButton gotoStartButton = null;
    private ImageButton gotoEndButton = null;

    private ToggleImageButton loopButton = null;
    private ToggleImageButton playButton = null;
    private ToggleImageButton stopButton = null;
    private ToggleImageButton recordButton = null;
    Blinker blinker = null;

    private ToggleGroup transportToggleGroup = null;
    private SeekBar sbLocation;

    private LinearLayout llStripList;
    private List<StripLayout> strips = new ArrayList<>();

    // key/value array to store strips plugins temporaily. Used to display Plugin Layout per strip
//    public HashMap<Integer, String> plugins = new HashMap<>();

    // some layouts for Sends, Receives, Panning, FX may be get more
    private int iAuxLayout = -1;
    private int iReceiveLayout = -1;
    private int iPanLayout = -1;
    private int iPluginLayout = -1;
    private int iSendsLayout = -1;

    private int iSelectStrip = -1;


    private PluginLayout pluginLayout;

    private StripElementMask stripElementMask = new StripElementMask();

    private StripLayout masterStrip;
    private LinearLayout llMain;
    private StripSelectLayout stripSelect;
    private HorizontalScrollView mainSroller;
    private LinearLayout llMaster;
    private LinearLayout llBankList;
    private SendsLayout sendsLayout;

    // xome elements for strip bankking
    private ArrayList<Bank> banks = new ArrayList<>();
    private Bank selectBank;
    private Bank currentBank;
    private int bankId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // enable networking in main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // restore preferences
        SharedPreferences settings = getSharedPreferences(TAG, 0);
        oscHost = settings.getString("oscHost", "127.0.0.1"); //if oscHost setting not found default to 127.0.0.1
        oscPort = settings.getInt("oscPort", 3819); //if oscPort setting not found default to 3819
        bankSize = settings.getInt("bankSize", 8);
        useSendsLayout = settings.getBoolean("useSendsLayout", false);

        stripElementMask.bTitle = settings.getBoolean("mskTitle", true);
        stripElementMask.bMeter = settings.getBoolean("mskMeter", true);

        mainSroller = (HorizontalScrollView) findViewById(R.id.main_scoller);
        llStripList = (LinearLayout) findViewById(R.id.strip_list);
        llMain = (LinearLayout) findViewById(R.id.main_lauyout);
        llMaster = (LinearLayout) findViewById(R.id.master_view);

        //Create the transport button listeners
        gotoStartButton = (ImageButton) this.findViewById(R.id.bGotoStart);
        gotoStartButton.setOnClickListener(this);

        gotoEndButton = (ImageButton) this.findViewById(R.id.bGotoEnd);
        gotoEndButton.setOnClickListener(this);

        playButton = (ToggleImageButton) this.findViewById(R.id.bPlay);
        playButton.setOnClickListener(this);
        playButton.setAutoToggle(false);

        stopButton = (ToggleImageButton) this.findViewById(R.id.bStop);
        stopButton.setOnClickListener(this);
        stopButton.setAutoToggle(false);
        stopButton.toggle(); //Set stop to toggled state

        recordButton = (ToggleImageButton) this.findViewById(R.id.bRec);
        recordButton.setOnClickListener(this);
        recordButton.setAutoToggle(false);

        transportToggleGroup = new ToggleGroup();

        transportToggleGroup.addToGroup(playButton);
        transportToggleGroup.addToGroup(stopButton);
//        transportToggleGroup.addToGroup(loopButton);

        sbLocation = (SeekBar) this.findViewById(R.id.locationBaR);
        sbLocation.setMax(10000);
        sbLocation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onProgressChanged(SeekBar sb, int pos, boolean fromUser) {

                    if (fromUser){

                        if (!(RECORD_ENABLED == (transportState & RECORD_ENABLED)
                        )){
// && TRANSPORT_RUNNING == (transportState & TRANSPORT_RUNNING)

                            int loc = Math.round(( sbLocation.getProgress() * maxFrame) / 10000);
                            oscService.transportAction(OscService.LOCATE, loc, TRANSPORT_RUNNING == (transportState & TRANSPORT_RUNNING) );

                            transportState = TRANSPORT_STOPPED;

                            transportToggleGroup.toggle(stopButton, true);
                            recordButton.toggleOff();
                        }
                    }
                }
            }
        );
    }

    /**
     * Activity is has become visible
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {

        super.onResume();
        Log.d(TAG, "Resuming...");

        if (!oscService.isConnected()){
            Log.d(TAG, "Not connected to OSC server... Connect");
            this.startConnectionToArdour();
        }

        blinker = new Blinker();
        blinker.setHandler(topLevelHandler);
        blinker.addBlinker(recordButton);
        blinker.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        if( oscService == null ) {
            oscService = new OscService(oscHost, oscPort);
            oscService.setTransportHandler(topLevelHandler);
        }
    }

    @Override
    protected void onDestroy() {
        stopConnectionToArdour();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {

            case R.id.action_connection:
                resetLayouts();
                SettingsDialogFragment dfSettings = new SettingsDialogFragment ();
                Bundle settingsBundle = new Bundle();
                settingsBundle.putString("host", oscHost);
                settingsBundle.putInt("port", oscPort);
                settingsBundle.putInt("bankSize", bankSize);
                settingsBundle.putBoolean("useSendsLayout", useSendsLayout);
                dfSettings.setArguments(settingsBundle);
                dfSettings.show(getSupportFragmentManager(), "Connection Settings");
                break;
            case R.id.action_mask:
                stripElementMask.config(this);
                break;
            case R.id.action_connect:
                startConnectionToArdour();
                break;
            case R.id.action_disconnect:
                stopConnectionToArdour();
                break;

// Banking menu
            case R.id.action_newbank:
                newBank();
                break;
            case R.id.action_editbank:
                EditBank(banks.indexOf(currentBank));
                break;
            case R.id.action_removebank:
                RemoveBank(banks.indexOf(currentBank));
                break;
            case R.id.action_savebank:
                SaveBank(currentBank);
                break;
            case R.id.action_loadbank:
                LoadBank();
                break;

// Record button menu
            case R.id.action_allrecenable:
                oscService.transportAction(OscService.ALL_REC_ENABLE);
                break;

            case R.id.action_allrecdisable:
                oscService.transportAction(OscService.ALL_REC_DISABLE);
                break;

            case R.id.action_allrectoggle:
                oscService.transportAction(OscService.ALL_REC_TOGGLE);
                break;

// Strip In menu
            case R.id.action_allstripin_enable:
                oscService.transportAction(OscService.ALL_STRIPIN_ENABLE);
                break;

            case R.id.action_allstripin_disable:
                oscService.transportAction(OscService.ALL_STRIPIN_DISABLE);
                break;

            case R.id.action_allstripin_toggle:
                oscService.transportAction(OscService.ALL_STRIPIN_TOGGLE);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void newBank() {
        BankSettingDialogFragment dfBankSetting = new BankSettingDialogFragment ();
        Bundle bankBundle = new Bundle();
        bankBundle.putInt("bankIndex", -1);
        dfBankSetting.setArguments(bankBundle);
        dfBankSetting.show(getSupportFragmentManager(), "Bank Settings");
    }

    void LoadBank() {
        File dir = new File(context.getFilesDir().getPath());
        File[] files = dir.listFiles();

        HashMap<String,String> mapFileNames = new HashMap<>();
        for (File f: files
             ) {
            if (f.getName().endsWith(".bank")) {
                mapFileNames.put(f.getName(), f.getAbsolutePath());
                Log.d(TAG, "filename: " + f.getAbsolutePath());
            }
        }
        Bundle bankBundle = new Bundle();
        bankBundle.putStringArrayList("files", new ArrayList<String>(mapFileNames.keySet()));
        dfBankLoad = new BankLoadDialog();
        bankBundle.putInt("bankIndex", -1);
        dfBankLoad.setArguments(bankBundle);
        dfBankLoad.show(getSupportFragmentManager(), "Load Bank");

    }

    private void SaveBank(Bank bank) {
        try {

            StringWriter stringWriter = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(stringWriter);
            jsonWriter.beginObject();
            jsonWriter.name("Bank").value(bank.getName());
            jsonWriter.name("Strips");
            jsonWriter.beginArray();
            for ( Bank.Strip strip : bank.getStrips() ) {

                jsonWriter.beginObject().name(strip.name).value(strip.id);
                jsonWriter.endObject();

            }
            jsonWriter.endArray();

            jsonWriter.endObject();
            jsonWriter.close();

            System.out.print(stringWriter.toString());
            FileOutputStream outputStream;
            String strFilename = bank.getName() + ".bank";
            outputStream = openFileOutput(strFilename, Context.MODE_PRIVATE);
            outputStream.write(stringWriter.toString().getBytes());
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void RemoveBank(int iBankIndex) {
        if( iBankIndex != 0 ) {
            final Bank _b = banks.get(iBankIndex);
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            llBankList.removeView(_b.button);
                            banks.remove(_b);
                            showBank(0);
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure to remove the current bank?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener)
                    .show();
        }
    }

    public void onSettingDlg(String host, int port, int bankSize, boolean useSendsLayout) {
        this.oscHost = host;
        this.oscPort = port;
        this.bankSize = bankSize;
        this.useSendsLayout = useSendsLayout;
        savePreferences();
    }

    public void savePreferences(){

        SharedPreferences settings = getSharedPreferences(TAG, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("oscHost", oscHost);
        editor.putInt("oscPort", oscPort);
        editor.putInt("bankSize", bankSize);
        editor.putBoolean("useSendsLayout", useSendsLayout);

        editor.apply();
    }

    private void stopConnectionToArdour() {

        if (oscService.isConnected()){
            oscService.disconnect();
        }
        llStripList.removeAllViews();
        strips.clear();
        llMaster.removeView(masterStrip);
        if( llBankList != null)
            llBankList.removeAllViews();
        banks.clear();
    }

    private void startConnectionToArdour() {
        oscService.setHost(oscHost);
        oscService.setPort(oscPort);

        llStripList.removeAllViews();
        strips.clear();


        if (oscService.isConnected()){
            oscService.disconnect();
        }
        oscService.connect();

        oscService.initSurfaceFeedback1();

        oscService.requestStripList();


    }

    int nLastVolume = -1;
    private Handler topLevelHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            int iRemoteId;
            StripLayout _StripLayout;

            Track _track;

            switch (msg.what) {

                case 5000: // perform blink
                    blinker.doBlink();
                    break;

                case StripLayout.MSG_WHAT_FADER_CHANGED:
                    _track = oscService.getTrack(msg.arg1 );
                    if ( iAuxLayout == msg.arg1) {
                        for( StripLayout _sl: strips) {
                            if( _sl.getShowType() == Track.TrackType.RECEIVE && _sl.getTrack().muteEnabled == true ) {
                                if( nLastVolume != -1) {
                                    _sl.changeVolume(_track.trackVolume - nLastVolume);
                                }
                            }
                        }
                        nLastVolume = _track.trackVolume;
                    }
                    else {
                        if (_track != null)
                            oscService.trackListAction(OscService.FADER_CHANGED, _track );
                    }
                    break;

                case SendsLayout.MSG_WHAT_RESET_LAYOUT:
                    resetLayouts();
                    break;

                case SendsLayout.MSG_WHAT_NEXT_SEND_LAYOUT:
                    int nl = currentBank.getStripPosition(iSendsLayout);
                    if( nl++ < currentBank.getStrips().size()-1 ) {
                        resetLayouts();
                        enableSendsLayout(currentBank.getStrips().get(nl).id, true);
                    }
                    break;

                case SendsLayout.MSG_WHAT_PREV_SEND_LAYOUT:
                    int pl = currentBank.getStripPosition(iSendsLayout);
                    if( pl-- > 0) {
                        resetLayouts();
                        enableSendsLayout(currentBank.getStrips().get(pl).id, true);
                    }
                    break;

                case SendsLayout.MSG_WHAT_SEND_CHANGED:
                    Track sendTrack =    oscService.getTrack(msg.arg1 + 1);
                    if (sendTrack != null)
                        oscService.trackSendAction(OscService.SEND_CHANGED, sendTrack, msg.arg2, (int)msg.obj );
                    break;

                case SendsLayout.MSG_WHAT_SEND_ENABLED:
                    Track sendEnableTrack =    oscService.getTrack(msg.arg1 + 1);
                    if (sendEnableTrack != null)
                        oscService.trackSendAction(OscService.SEND_ENABLED, sendEnableTrack, msg.arg2, (boolean)msg.obj ? 1 : 0 );
                    break;

                case StripLayout.MSG_WHAT_AUX_CHANGED:
                    Track auxTrack =    oscService.getTrack(msg.arg1 );
                    if (auxTrack != null)
                        oscService.trackListAction(OscService.AUX_CHANGED, auxTrack );
                    break;

                case StripLayout.MSG_WHAT_RECEIVE_CHANGED:
                    Track receiveTrack = oscService.getTrack(iAuxLayout);
                    if (receiveTrack != null)
                        oscService.recvListVolumeAction( receiveTrack, msg.arg1 );
                    break;

                case StripLayout.MSG_WHAT_PAN_CHANGED:
                    Track panTrack = oscService.getTrack(iPanLayout);
                    if (panTrack != null)
                        oscService.panAction( panTrack, msg.arg1 );
                    break;

                case PluginLayout.MSG_WHAT_PLUGIN_CHANGED:
                    Track pluginTrack = oscService.getTrack( msg.arg1 );
                    if( pluginTrack != null ) {
                        Object[] plargs = (Object[]) msg.obj;
                        oscService.pluginFaderAction(pluginTrack, msg.arg2, (int) plargs[0], (float) plargs[1]);
                    }
                    break;

                case PluginLayout.MSG_WHAT_PLUGIN_REQUEST:
                    oscService.requestPlugin(msg.arg1, msg.arg2);
                    break;

                case PluginLayout.MSG_WHAT_PLUGIN_ENABLE:
                    Track pluginTrack2 = oscService.getTrack( msg.arg1 );
                    if( pluginTrack2 != null ) {
                        oscService.pluginEnable(pluginTrack2, msg.arg2, (int) msg.obj == 1 );
                    }
                    break;

                case PluginLayout.MSG_WHAT_PLUGIN_RESET:
                    Track pluginTrack1 = oscService.getTrack( msg.arg1 );
                    if( pluginTrack1 != null ) {
                        oscService.pluginAction(OscService.PLUGIN_RESET, pluginTrack1, msg.arg2 );
                        resetLayouts();
                    }
                    break;

                case PluginLayout.MSG_WHAT_PLUGIN_NEXT:
                    int np = currentBank.getStripPosition(iPluginLayout);
                    if( np++ < currentBank.getStrips().size()-1 ) {
                        enablePluginLayout(currentBank.getStrips().get(np).id, true);
                    }
                    break;

                case PluginLayout.MSG_WHAT_PLUGIN_PREV:
                    int pp = currentBank.getStripPosition(iPluginLayout);
                    if( pp-- > 0) {
                        enablePluginLayout(currentBank.getStrips().get(pp).id, true);
                    }
                    break;


                case MSG_WHAT_FRAMERATE:
                    frameRate = (Long) msg.obj;
                    break;

                case MSG_WHAT_MAXFRAMES:
                    maxFrame = (Long) msg.obj;
                    break;

                case MSG_WHAT_STRIPLIST:

                    updateStripList();
                    oscService.initSurfaceFeedback2();

                    break;

                case MSG_WHAT_NEWSTRIP:
                    addStrip((Track)msg.obj);
                    break;

                case MSG_WHAT_STRIP_NAME:
                    iRemoteId = msg.arg1;
                    if( strips.size() > iRemoteId )
                        getStripLayout(iRemoteId).nameChanged();
                    break;

                case MSG_WHAT_STRIP_REC:
                    iRemoteId = msg.arg1;
                    if (strips.size() > iRemoteId)
                        getStripLayout(iRemoteId).recChanged();
                    break;

                case MSG_WHAT_STRIP_MUTE:
                    iRemoteId = msg.arg1;
                    if (strips.size() > iRemoteId)
                        getStripLayout(iRemoteId).muteChanged();
                    break;

                case MSG_WHAT_STRIP_SOLO:
                    iRemoteId = msg.arg1;
                    if (strips.size() > iRemoteId)
                        getStripLayout(iRemoteId).soloChanged();
                    break;

                case MSG_WHAT_STRIP_PAN:
                    iRemoteId = msg.arg1;
                    if (strips.size() > iRemoteId)
                        getStripLayout(iRemoteId).panChanged();
                    break;

                case MSG_WHAT_STRIP_VOLUME:
                    iRemoteId = msg.arg1;
                    if (strips.size() > iRemoteId-1)
                        getStripLayout(iRemoteId).volumeChanged();
                    break;

                case MSG_WHAT_STRIP_RECEIVES:
                    Object args[] = (Object[]) msg.obj;

                    for( int i = 0; i < args.length; i+=5 ) {
                        iRemoteId = (int)args[i];
                        StripLayout strip = getStripLayout(iRemoteId);
                        strip.setType(Track.TrackType.SEND, (Float)args[i+3], (int)args[i+2], (int)args[i+4] == 1);

                    }
                    break;

                case MSG_WHAT_STRIP_SELECT:
                    if( msg.arg2  == 1 ) {
                        iSelectStrip = msg.arg1;
                    }
                    else {
                        iSelectStrip = -1;
                    }
                    break;

                case MSG_WHAT_STRIP_SENDS:
                    iRemoteId = msg.arg1;
                    Object sargs[] = (Object[]) msg.obj;

                    StripLayout strip = getStripLayout(iRemoteId);

                    if (!useSendsLayout) {
                        int next = 1;
                        for (int i = 0; i < sargs.length; i += 5) {
                            if( (int)sargs[i] > 0 ) {
                                StripLayout receiveStrip = getStripLayout((int) sargs[i]);
                                receiveStrip.setType(Track.TrackType.RECEIVE, (Float) sargs[i + 3], (int) sargs[i + 2], (int) sargs[i + 4] == 1);
                                if (!currentBank.contains(receiveStrip.getRemoteId())) {
                                    receiveStrip.setPosition(strip.getPosition() + next);
                                    llStripList.addView(receiveStrip, strip.getPosition() + (next++));
                                }
                            }
                        }
                    }
                    else {
                        showSends(strip, sargs);
                    }
                    break;

                case MSG_WHAT_STRIP_SEND_FADER:
                    if( sendsLayout != null && iSelectStrip == iSendsLayout  ) {
                        Object sfargs[] = (Object[]) msg.obj;
                        sendsLayout.sendChanged((int)sfargs[0], (float) sfargs[1]);
                    }
                    break;

                case MSG_WHAT_STRIP_SEND_ENABLE:
                    if( sendsLayout != null && iSelectStrip == iSendsLayout  ) {
                        Object sfargs[] = (Object[]) msg.obj;
                        sendsLayout.sendEnable((int)sfargs[0], (float) sfargs[1]);
                    }
                    break;

                case MSG_WHAT_STRIP_SEND_NAME:
                    if( sendsLayout != null && iSelectStrip == iSendsLayout ) {
                        String newName = (String) msg.obj;
                        if( !newName.equals(" ") )
                            sendsLayout.sendName(msg.arg1, (String) msg.obj);
                    }
                    break;


                case MSG_WHAT_STRIP_METER:
                    iRemoteId = msg.arg1;
                    _StripLayout = getStripLayout(iRemoteId);
                    if ( _StripLayout!= null ) {
                        _StripLayout = getStripLayout(iRemoteId);
                        _StripLayout.meterChange();
                    }
                    break;

                case MSG_WHAT_PLUGIN_LIST:
                    Object plargs[] = (Object[]) msg.obj;
                    Track track = oscService.getTrack((int)plargs[0]);
                    track.plugins.clear();
                    for( int pli = 1; pli < plargs.length; pli+=2 ) {
                        track.plugins.put((int)plargs[pli], (String)plargs[pli+1]);
                    }
                    showPluginLayout(track);
                    break;

                case MSG_WHAT_PLUGIN_DESCRIPTOR:
                    Object pdargs[] = (Object[]) msg.obj;

                    ArdourPlugin pluginDes = new ArdourPlugin((int)pdargs[0], (int)pdargs[1], (int)pdargs[2]);

                    for( int pi = 3; pi < pdargs.length; pi+=9 ) {
                        ArdourPlugin.InputParameter parameter = new ArdourPlugin.InputParameter((int)pdargs[pi], (String)pdargs[pi+1]);

                        parameter.flags = (int)pdargs[pi+2];
                        parameter.type = (String)pdargs[pi+3];
                        parameter.min = (float)pdargs[pi+4];
                        parameter.max = (float)pdargs[pi+5];
                        parameter.print_fmt = (String) pdargs[pi+6];
                        parameter.scaleSize = (int)pdargs[pi+7];
                        for( int spi = 0; spi < parameter.scaleSize; spi++ ) {
                            parameter.addScalePoint((float)pdargs[pi+8], (String)pdargs[pi+9]);
                            pi+=2;
                        }
                        parameter.current = (float)pdargs[pi+8];
                        pluginDes.addParameter(parameter);
                    }

                    showPlugin(pluginDes, true);

                    break;

                case MSG_WHAT_UPDATE_CLOCK:
                    long clock = (long)msg.obj;

                    if( transportState == (transportState & RECORD_ENABLED)) {
                        maxFrame = clock;
                    }
                    sbLocation.setProgress(Math.round(( (float) clock/ (float) maxFrame) * 10000));
                    sbLocation.refreshDrawableState();
                    break;

                case MSG_WHAT_RECORD:
                    if( msg.arg1 == 1 ) {
                        transportState = (byte) (transportState | RECORD_ENABLED);
                        recordButton.setToggleState(true, true);
                    }
                    else {
                        transportState = (byte) (transportState ^ RECORD_ENABLED);
                        recordButton.setToggleState(false, false);
                    }
                    break;
                case MSG_WHAT_PLAY:
                    if( msg.arg1 == 1 ) {
                        if (RECORD_ENABLED == (RECORD_ENABLED & transportState)) {
                            transportState = TRANSPORT_RUNNING | RECORD_ENABLED;
                            recordButton.toggleOn();
                        } else {
                            transportState = TRANSPORT_RUNNING;

                        }
                        transportToggleGroup.toggle(playButton, true);
                    }
                    else {
                        if (RECORD_ENABLED == (RECORD_ENABLED & transportState)) {
                            transportState = TRANSPORT_RUNNING ^ RECORD_ENABLED;
                            recordButton.toggleOff();
                        } else {
                            transportState ^= TRANSPORT_RUNNING;

                        }
                        transportToggleGroup.toggle(playButton, false);
                    }
                    break;

                case MSG_WHAT_STOP:
                    if( msg.arg1 == 1 ) {
                        transportState = 0;
                    }
                    break;
            }
        }

    };

    public void addStrip(Track t) {

        final StripLayout stripLayout = new StripLayout(this, t);
        LinearLayout.LayoutParams stripLP = new LinearLayout.LayoutParams(
                StripLayout.STRIP_WIDTH,
                LinearLayout.LayoutParams.MATCH_PARENT);
        if( t.type == Track.TrackType.MASTER ) {
            masterStrip = stripLayout;
            llMaster.addView(masterStrip);
            stripLP.weight = 1;
        }
        stripLP.gravity = Gravity.CENTER_HORIZONTAL;
        stripLayout.setPadding(0, 0, 0, 0);
        stripLayout.setLayoutParams(stripLP);
        stripLayout.setBackgroundColor(getResources().getColor(R.color.VeryDark, null));

        stripLayout.setId(t.remoteId-1);

        stripLayout.setOnClickListener(this);
        stripLayout.setOnChangeHandler(topLevelHandler);

        stripLayout.init(context, stripElementMask);
        strips.add(stripLayout);

        System.out.printf("adding strip %s with id %d\n", t.name, t.remoteId-1);

    }

    public void updateStripList() {

        createBanklist();
        updateBanklist();
        showBank(0);

    }

    private void createBanklist() {

        int iTrackInBank = 0;
        int iBusBegin = 1;
        int iBusEnd = 1;
        Track.TrackType lt = Track.TrackType.AUDIO;

        for( Bank b: banks) {
            b.getStrips().clear();
        }

        banks.add (new Bank("All" ));

        Bank allBank = banks.get(0);
        Bank nb = new Bank();
        nb.setType(Bank.BankType.AUDIO);
        banks.add(nb);

        for( StripLayout sl: strips ) {
            Track t = sl.getTrack();

            if( t.type != Track.TrackType.MASTER ) {
                allBank.add(t.name, t.remoteId, true);

                if ( lt != t.type || iTrackInBank++ == bankSize) {
                    nb = new Bank();
                    banks.add(nb);
                    switch (t.type) {
                        case AUDIO:
                            nb.setType(Bank.BankType.AUDIO);
                            break;
                        case BUS:
                            nb.setType(Bank.BankType.BUS);
                            break;
                    }
                    iTrackInBank = 0;
                }

                nb.add(t.name, t.remoteId, true);
                lt = t.type;
            }
        }

        for( Bank b: banks) {
            switch(b.getType()) {
                case AUDIO:
                    b.setName(String.format("IN %d-%d", iBusEnd, iBusEnd + (b.getStrips().size())-1));
                    iBusEnd += b.getStrips().size() ;
                    break;
                case BUS:
                    b.setName(String.format("BUS %d-%d", iBusBegin, iBusBegin + (b.getStrips().size())-1));
                    iBusBegin += b.getStrips().size() - 1;
                    break;
            }
        }

    }

    private void updateBanklist() {

        bankId = 1000; //llBankList.generateViewId();
        llBankList = (LinearLayout) findViewById(R.id.bank_list);

        llBankList.removeAllViews();
        for( int iBankIndex = 0; iBankIndex < banks.size() ; iBankIndex++) {
            Bank _bank = banks.get(iBankIndex);
            _bank.button = new ToggleTextButton(this);
            LinearLayout.LayoutParams bankLP = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    32);
            bankLP.setMargins(1,1,1,1);
            bankLP.gravity = Gravity.RIGHT;
            _bank.button.setLayoutParams(bankLP);
            _bank.button.setPadding(0,0,0,0);
            _bank.button.setAllText(_bank.getName());
            _bank.button.setTag(iBankIndex);
            _bank.button.setId(bankId + iBankIndex);
            _bank.button.setOnClickListener(this);
            _bank.button.setOnLongClickListener(this);
            _bank.button.setAutoToggle(true);
            _bank.button.setToggleState(false);
            llBankList.addView(_bank.button);
        }
        ToggleTextButton ttbAddBank = new ToggleTextButton(this);
        LinearLayout.LayoutParams bankLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                32);
        bankLP.setMargins(1,1,1,1);
        bankLP.gravity = Gravity.RIGHT;
        ttbAddBank.setLayoutParams(bankLP);
        ttbAddBank.setPadding(0,0,0,0);
        ttbAddBank.setAllText("+");
        ttbAddBank.setId(bankId + banks.size());
        ttbAddBank.setOnClickListener(this);
        ttbAddBank.setAutoToggle(false);
        ttbAddBank.setToggleState(false);
        llBankList.addView(ttbAddBank);
        
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.bGotoStart:
                if (transportState == TRANSPORT_RUNNING) {
                    transportToggleGroup.toggle(stopButton, true);
                    transportState = TRANSPORT_STOPPED;
                } else if (TRANSPORT_RUNNING == (transportState & TRANSPORT_RUNNING)
                        && RECORD_ENABLED == (transportState & RECORD_ENABLED)) {
                    break;
                }
                oscService.transportAction(OscService.GOTO_START);
                break;

            case R.id.bGotoEnd:
                if (transportState == TRANSPORT_RUNNING) {
                    transportToggleGroup.toggle(stopButton, true);
                    transportState = TRANSPORT_STOPPED;
                } else if (TRANSPORT_RUNNING == (transportState & TRANSPORT_RUNNING)
                        && RECORD_ENABLED == (transportState & RECORD_ENABLED)) {
                    break;
                }
                oscService.transportAction(OscService.GOTO_END);
                break;

            case R.id.bPlay:
                oscService.transportAction(OscService.TRANSPORT_PLAY);
                if (RECORD_ENABLED == (RECORD_ENABLED & transportState)) {
                    transportState = TRANSPORT_RUNNING | RECORD_ENABLED;
                    recordButton.toggleOn();
                } else {
                    transportState = TRANSPORT_RUNNING;

                }
                transportToggleGroup.toggle(playButton, true);
                break;

            case R.id.bStop:
                boolean wasRunning = (transportState == (TRANSPORT_RUNNING | RECORD_ENABLED));
                oscService.transportAction(OscService.TRANSPORT_STOP);

                transportState = TRANSPORT_STOPPED;
                transportToggleGroup.toggle(stopButton, true);
                if (wasRunning)
                    recordButton.toggleOff();
                break;

            case R.id.bRec:
                oscService.transportAction(OscService.REC_ENABLE_TOGGLE);
                if (RECORD_ENABLED != (transportState & RECORD_ENABLED)) {
                    if (TRANSPORT_STOPPED == (transportState & TRANSPORT_STOPPED)) {
                        recordButton.toggleOnAndBlink();
                    } else {
                        recordButton.toggleOn();
                    }
                    transportState = (byte) (transportState | RECORD_ENABLED);
                } else {
                    transportState = (byte) (transportState ^ RECORD_ENABLED);
                    recordButton.toggleOff();
                }
                break;

            case R.id.bLoopEnable:
                if (!(TRANSPORT_RUNNING == (transportState & TRANSPORT_RUNNING)
                        && RECORD_ENABLED == (transportState & RECORD_ENABLED))) {

                    oscService.transportAction(OscService.LOOP_ENABLE_TOGGLE);
                    transportToggleGroup.toggle(loopButton, true);
                }
                break;

            case R.id.bankSelectOk:
                EditText bankName = (EditText) stripSelect.getChildAt(0);
                selectBank.setName(bankName.getText().toString());
                llStripList.removeView(stripSelect);
                break;

            default:
                int i = v.getId();

                if (i >= bankId && i < bankId + banks.size() ) {
                    showBank((int)v.getTag());
                    break;
                }

                if( i == bankId + banks.size()) {
                    newBank();
                    break;
                }


                switch ((String) v.getTag()) {
                    case "strip":
                        showStripDialog(i);
                        break;
                    case "rec":
                        if (TRANSPORT_RUNNING != (transportState & TRANSPORT_RUNNING))
                            oscService.trackListAction(OscService.REC_CHANGED, oscService.getTrack(i));
                        break;

                    case "mute":
                        StripLayout sl = getStripLayout(i);
                        if (sl.getShowType() == Track.TrackType.RECEIVE) {
                            ToggleTextButton b = (ToggleTextButton)v;
                            System.out.printf("set plugin param strip:%d, piid:%d\n", iAuxLayout, oscService.getTrack(sl.getId()).source_id);
                            oscService.setSendEnable(iAuxLayout, oscService.getTrack(sl.getId()).source_id, b.getToggleState() ? 0f : 1f);
                        }
                        else if(sl.getShowType() == Track.TrackType.SEND) {
                            ToggleTextButton b = (ToggleTextButton)v;
                            System.out.printf("set plugin param strip:%d, piid:%d\n", i, oscService.getTrack(sl.getId()+1).source_id);
                            oscService.setSendEnable(i, oscService.getTrack(sl.getId()+1).source_id-1, b.getToggleState() ? 1f : 0f);
                        }
                        else
                            oscService.trackListAction(OscService.MUTE_CHANGED, oscService.getTrack(i));
                        break;

                    case "solo":
                        oscService.trackListAction(OscService.SOLO_CHANGED, oscService.getTrack(i));
                        break;

                    case "in":
                        enableBusFaderIn(i, ((ToggleTextButton) v).getToggleState());
                        break;

                    case "aux":
                        if( !useSendsLayout )
                            enableBusFaderOut(i,((ToggleTextButton) v).getToggleState());
                        else
                            enableSendsLayout(i, ((ToggleTextButton) v).getToggleState());
                        break;

                    case "pan":
                        enablePanFader(i, ((ToggleTextButton) v).getToggleState());
                        break;

                    case "fx":
                        enablePluginLayout(i, ((ToggleTextButton) v).getToggleState());
                        break;
                }
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {

        EditBank((int)v.getTag());
        return true;
    }

    private void enableSendsLayout(int iStripIndex, boolean bState) {
        StripLayout strip = getStripLayout(iStripIndex);
        if (bState) {

            resetLayouts();
//            oscService.requestSends(iStripIndex);

            sendsLayout = new SendsLayout(this);
            sendsLayout.init(strip, new Object[0]);
            sendsLayout.setOnChangeHandler(topLevelHandler);
            llStripList.addView(sendsLayout, strip.getPosition() + 1);

            strip.setBackgroundColor(getResources().getColor(R.color.BUS_AUX_BACKGROUND, null));
            strip.pushVolume();
            iSendsLayout = iStripIndex;
            forceVisible(sendsLayout);

            oscService.selectStrip(iStripIndex, true);
        }
        else {
            nLastVolume = -1;
            if(!useSendsLayout) {
                for (StripLayout sl : strips) {
                    if (sl.showtype == Track.TrackType.SEND || sl.showtype == Track.TrackType.RECEIVE) {
                        sl.ResetType();
                        if (currentBank == null || !currentBank.contains(sl.getId() + 1))
                            llStripList.removeView(sl);
                    }
                }
            }
            else {
                if( sendsLayout != null ) {
                    sendsLayout.deinit();
                    llStripList.removeView(sendsLayout);
                    sendsLayout = null;
                }
            }
            strip.resetBackground();
            strip.sendChanged(false);
            strip.pullVolume();
            iSendsLayout = -1;
        }
    }

    private void showSends(StripLayout strip, Object[] sargs) {
//        resetLayouts();
        sendsLayout = new SendsLayout(this);
        sendsLayout.init(strip, sargs);
        sendsLayout.setOnChangeHandler(topLevelHandler);
        llStripList.addView(sendsLayout, strip.getPosition() + 1);
        forceVisible(sendsLayout);
    }

    private void enablePanFader(int iStripIndex, boolean bState) {
        StripLayout strip = getStripLayout(iStripIndex);

        if (bState) {
            resetLayouts();

            iPanLayout = iStripIndex;
            strip.setBackgroundColor(0x40ffbb33);
            strip.setType(Track.TrackType.PAN, 0f, 0, false);
            strip.panChanged();

        }
        else {
            strip.ResetPan();
            strip.resetBackground();

            iPanLayout = -1;
            strip.volumeChanged();
        }
    }

    private void enableBusFaderIn(int iStripIndex, boolean bState) {
        StripLayout strip = getStripLayout(iStripIndex);
        if( bState ) {
            resetLayouts();

            oscService.requestReceives(iStripIndex);

            strip.setBackgroundColor(getResources().getColor(R.color.BUS_IN_BACKGROUND, null));
            iReceiveLayout = iStripIndex;
        }
        else {
            for(StripLayout receiveLayout: strips) {
                if( receiveLayout.showtype == Track.TrackType.SEND || receiveLayout.showtype == Track.TrackType.RECEIVE ) {
                    receiveLayout.ResetType();
                    if (currentBank == null || !currentBank.contains(receiveLayout.getId() + 1))
                        llStripList.removeView(receiveLayout);
                }
            }
            strip.resetBackground();
            oscService.getTrack(iStripIndex).recEnabled = false;
            strip.recChanged();
            iReceiveLayout = -1;
        }
    }

    private void enableBusFaderOut(int iStripIndex, boolean bState) {
        StripLayout strip = strips.get(iStripIndex-1);
        if( bState ) {

            resetLayouts();
            oscService.requestSends(iStripIndex);

            strip.setBackgroundColor(getResources().getColor(R.color.BUS_AUX_BACKGROUND, null));
            strip.pushVolume();
            iAuxLayout = iStripIndex;
        }
        else {
            nLastVolume = -1;
            for(StripLayout sl: strips) {
                if( sl.showtype == Track.TrackType.SEND || sl.showtype == Track.TrackType.RECEIVE ) {
                    sl.ResetType();
                    if(  currentBank == null || !currentBank.contains(sl.getId()+1))
                        llStripList.removeView(sl);
                }
            }
            strip.resetBackground();
            strip.sendChanged(false);
            strip.pullVolume();
            iAuxLayout = -1;
        }
    }

    private void enablePluginLayout(int iStripIndex, boolean bState) {
        StripLayout strip = getStripLayout(iStripIndex);
        if( bState ) {

            resetLayouts();
            pluginLayout = new PluginLayout(this);
            pluginLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            pluginLayout.setOrientation(LinearLayout.VERTICAL);
            pluginLayout.setBackgroundColor(0x20FF40FF);
            pluginLayout.setPadding(1, 0, 1, 0);
            pluginLayout.setId(iStripIndex);
            pluginLayout.setOnChangeHandler(topLevelHandler);
            //place layout in strip list
            if (iStripIndex == strips.size()) // its the master strip
                llStripList.addView(pluginLayout);
            else
                llStripList.addView(pluginLayout, strip.getPosition() + 1);


            iPluginLayout = iStripIndex;
            // we are ready to receive plugin list
            oscService.requestPluginList(iStripIndex);
            strip.setBackgroundColor(0x20FF40FF);
        }
        else {
            if(pluginLayout != null ) {
                pluginLayout.removeAllViews();
                llStripList.removeView(pluginLayout);
            }
            strip.fxOff();
            strip.resetBackground();

            pluginLayout = null;
            iPluginLayout = -1;
        }
    }

    private void showPluginLayout(Track track) {
        pluginLayout.initLayout(true, track.plugins);
        if( track.plugins.size() == 0)
            forceVisible(pluginLayout);
    }

    private void forceVisible(final View v) {
        v.post(new Runnable() {
            @Override
            public void run() {
                Rect rectScrollBounderies = new Rect();
                mainSroller.getDrawingRect(rectScrollBounderies);
                int sl = mainSroller.getScrollX();
                if( v.getRight() - rectScrollBounderies.right > 0  )
                    mainSroller.smoothScrollTo(v.getRight() - rectScrollBounderies.right + sl, 0);
            }
        });
    }

    private void showPlugin(ArdourPlugin pluginDes, boolean bState) {

        if( bState ) {
            if( pluginLayout == null )
                resetLayouts();
            else {
                pluginLayout.init(oscService.getTrack(pluginDes.getTrackId()), pluginDes);
                pluginLayout.setTag(pluginDes);
            }
            forceVisible(pluginLayout);
        }
        else {
            if( pluginLayout != null ) {
                getStripLayout(pluginLayout.getId()).fxOff();
                llStripList.removeView(pluginLayout);
                pluginLayout = null;
            }
        }
    }

    private void resetLayouts() {

        if( iAuxLayout != -1  )
            enableBusFaderOut(iAuxLayout, false);

        if( iReceiveLayout != -1  )
            enableBusFaderIn(iReceiveLayout, false);

        if( iPanLayout != -1  )
            enablePanFader(iPanLayout, false);

        if( iPluginLayout != -1 )
            enablePluginLayout(iPluginLayout, false);

        if( iSendsLayout != -1 )
            enableSendsLayout(iSendsLayout, false);

    }

    private void showStripDialog(int iStripIndex) {
        Track t = oscService.getTrack(iStripIndex);
        StripSettingDialogFragment dfStripSetting = new StripSettingDialogFragment ();
        Bundle settingsBundle = new Bundle();
        settingsBundle.putString("stripName", t.name);
        settingsBundle.putInt("stripIndex", iStripIndex);
        if(t.type == Track.TrackType.AUDIO) {
            settingsBundle.putBoolean("stripIn", t.stripIn);
            settingsBundle.putBoolean("stripRecord", t.recEnabled);
        }
        settingsBundle.putBoolean("stripMute", t.muteEnabled);
        settingsBundle.putBoolean("stripSolo", t.soloEnabled);


        dfStripSetting.setArguments(settingsBundle);
        dfStripSetting.show(getSupportFragmentManager(), "Strip Settings");

    }

    private StripLayout getStripLayout(int iRemoteid) {
        if( iRemoteid-1 < strips.size() )
            return strips.get(iRemoteid-1);
        return null;
    }

    public void onStripDlg(int iStripIndex, String strName, boolean bStripIn, boolean bRecord, boolean bMute, boolean bSolo) {
        Track t = oscService.getTrack(iStripIndex);
        t.name = strName;
        oscService.trackListAction(OscService.NAME_CHANGED, oscService.getTrack(iStripIndex));
        if(t.stripIn != bStripIn)
            oscService.trackListAction(OscService.STRIPIN_CHANGED, oscService.getTrack(iStripIndex));
        if(t.recEnabled != bRecord)
            oscService.trackListAction(OscService.REC_CHANGED, oscService.getTrack(iStripIndex));
        if(t.muteEnabled != bMute)
            oscService.trackListAction(OscService.MUTE_CHANGED, oscService.getTrack(iStripIndex));
        if(t.soloEnabled != bSolo)
            oscService.trackListAction(OscService.SOLO_CHANGED, oscService.getTrack(iStripIndex));
    }

    public void onBankDlg(int iBankIndex, Bank bank) {
        if (selectBank != null && iBankIndex != -1) {
            selectBank.setName(bank.getName());
            selectBank.getStrips().clear();
            for( Bank.Strip strip: bank.getStrips()) {
                selectBank.add(strip.name, strip.id, strip.enabled);
            }
            showBank((int)selectBank.getButton().getTag());
        }
        else {
            banks.add(bank);
            updateBanklist();
            showBank((int)bank.getButton().getTag());
        }
    }

    private void EditBank(int iBankIndex) {
        selectBank = banks.get(iBankIndex);
        BankSettingDialogFragment dlg = new BankSettingDialogFragment ();
        Bundle settingsBundle = new Bundle();
        settingsBundle.putString("bankName", selectBank.getName());
        settingsBundle.putInt("bankIndex", iBankIndex);
        dlg.setArguments(settingsBundle);
        dlg.show(getSupportFragmentManager(), "Bank Settings");
    }

    public Bank getBank(int iBankIndex) {
        if (iBankIndex == -1) {
            return new Bank("new bank");
        }
        return banks.get(iBankIndex);
    }

    private void showBank(int iBankIndex) {
        resetLayouts();
        ToggleTextButton _bankButton;

        Bank _bank = banks.get(iBankIndex);
        llStripList.removeAllViews();

        for( int i = 0; i < banks.size(); i++ ) {
            banks.get(i).getButton().setToggleState(false);
        }

        int c = 0;
        for( Bank.Strip strip: _bank.getStrips()) {
            try {
                llStripList.addView(getStripLayout(strip.id), c);


            } catch (Exception e) {
                e.printStackTrace();
            }
            getStripLayout(strip.id).setPosition(c++);
        }

        _bankButton = (ToggleTextButton) llBankList.getChildAt(iBankIndex);
        _bankButton.toggleOn();
        currentBank = _bank;
    }

    public ArrayList<Track> getRoutes() {
        return oscService.getRoutes();
    }

    public void LoadBankFile(Object tag) {
        if( dfBankLoad != null ) {
            dfBankLoad.dismiss();
            Bank bank = new Bank();
            int nBytesRead = 0;
            try {
                FileInputStream inputStream;
                StringBuffer content = new StringBuffer();
                byte[] buffer = new byte[1024];
                inputStream = openFileInput((String) tag);
                while ((nBytesRead = inputStream.read(buffer)) != -1)
                {
                    content.append(new String(buffer, 0, nBytesRead));
                }
                inputStream.close();
                Log.d(TAG, "load file content: " + content.toString());

                StringReader stringReader = new StringReader(content.toString());
                JsonReader reader = new JsonReader(stringReader);
                reader.beginObject();
                if( reader.nextName().equals("Bank"))
                    bank.setName(reader.nextString());
                if( reader.nextName().equals("Strips")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        reader.beginObject();
                        bank.add(reader.nextName(), reader.nextInt(), true);
                        reader.endObject();
                    }
                    reader.endArray();
                }
                reader.endObject();
                banks.add(bank);
                updateBanklist();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            showBank(banks.indexOf(bank));
        }
        dfBankLoad = null;
    }

    public void onStripMaskDlg() {
        for( StripLayout sl : strips) {
            sl.init(context, stripElementMask);
        }
        showBank((int)currentBank.getButton().getTag());

        SharedPreferences settings = getSharedPreferences(TAG, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean("mskTitle", stripElementMask.bTitle);
        editor.putBoolean("mskMeter", stripElementMask.bMeter);

        editor.commit();
    }

    public void getProcessors(int iStripIndex) {
        oscService.getProcessors(iStripIndex );
    }
}
