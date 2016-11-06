package de.paraair.ardmix;

/**
 * Created by onkel on 16.09.16.
 */
public class Fader {
    public int remoteId;
    public String name;
    public int Volume = 0;

    public final static double minpos = 0.0;
    public final static double maxpos = 1000.0;
    public final static double minlval = Math.cbrt(0.0000000001);
    public final static double maxlval = Math.cbrt(2000.0);

    public final static double scale = (maxlval - minlval) / (maxpos - minpos);

    public final static int valueToSlider(double value){
        return  (int)(minpos + (Math.cbrt(value) - minlval) / scale);
    }
    public final static double sliderToValue(int sliderposition){
        return Math.pow((sliderposition - minpos) * scale + minlval, 3.0);
    }

}
