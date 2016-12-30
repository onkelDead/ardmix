package de.paraair.ardmix;

/**
 * Created by onkel on 16.09.16.
 */
class Fader {
    public int remoteId;
    public String name;
    public int Volume = 0;

    private final static double minpos = 0.0;
    private final static double maxpos = 1000.0;
    private final static double minlval = Math.cbrt(0.0000000001);
    private final static double maxlval = Math.cbrt(2000.0);

    private final static double scale = (maxlval - minlval) / (maxpos - minpos);

    public static int valueToSlider(double value){
        return  (int)(minpos + (Math.cbrt(value) - minlval) / scale);
    }
    public static double sliderToValue(int sliderposition){
        return Math.pow((sliderposition - minpos) * scale + minlval, 3.0);
    }

}
