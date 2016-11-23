package de.paraair.ardmix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by onkel on 20.10.16.
 */

public  class ArdourPlugin {
    private int trackId;
    private int pluginId;

    private String name;

    private ArrayList<InputParameter> parameterList = new ArrayList<>();

    public ArdourPlugin() {}

    public ArdourPlugin(int trackId, int pluginId, String name) {
        this.name = name;
        this.trackId = trackId;
        this.pluginId = pluginId;
    }

    public InputParameter addParameter(InputParameter parameter) {
        parameterList.add(parameter);
        return parameter;
    }

    public String getName() {
        return name;
    }

    public ArrayList<InputParameter> getParameters() {
        return parameterList;
    }

    public InputParameter getParameter(int pi) {
        return parameterList.get(pi);
    }

    public int getTrackId() {
        return trackId;
    }

    public int getPluginId() {
        return pluginId;
    }


    public static  class InputParameter {
        int parameter_index;
        String name;

        int type;
        int flags;
        float min;
        float max;
        double current;
        String print_fmt;
        int scaleSize;

        SortedMap<Integer, String> scale_points = new TreeMap<>();


        public InputParameter(int index, String name) {
            parameter_index = index;
            this.name = name;
        }

        public int getFaderFromCurrent(int base) {

            float Range = max - min;
            return (int)(base / Range * (current - min));
        }

        public void setCurrentFromFader(int val, int base) {
            float range = max - min;

            if( (flags & 0x1) == 0x1 )
                current = Math.round((float)(range * val) / base + min);
            else
                current = (float)(range * val) / base + min;

        }

        public String getTextFromCurrent() {
            if( print_fmt == null || print_fmt.isEmpty() )
                return String.format("%.2f", current);
            else
                return String.format(print_fmt, current);
        }

        public int getIndexFromScalePointKey(int key) {
            int index = 0;

            for(Map.Entry<Integer,String> entry : scale_points.entrySet()) {
                if( entry.getKey() == key )
                    return index;
                index++;
            }
            return 0;
        }

        public void addScalePoint(int val, String name) {
            scale_points.put(val, name);
        }
    }


}
