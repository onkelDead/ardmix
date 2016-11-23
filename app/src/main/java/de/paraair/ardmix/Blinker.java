package de.paraair.ardmix;

import android.os.Handler;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;



class Blinker extends Thread {

	private Handler handler = null;

	private boolean blinkState = false;
	private boolean blinking = false;

	private Set<Blinkable> blinkers = new HashSet<>();

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {

		blinking = true;

		while (blinking) {

			blinkState = !blinkState;

			try {

				handler.sendEmptyMessage(5000);
				Thread.sleep(300);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		Log.d("Ardroid", "Stopping Blinker thread");
	}

	/**
	 * Stop the blinker.
	 */
	public void stopBlinker(){
		this.blinking = false;
	}


	public void addBlinker(Blinkable b){
		this.blinkers.add(b);
	}


	/**
	 * @param handler the handler to set
	 */
	public void setHandler(Handler handler) {
		this.handler = handler;
	}


	public void doBlink(){
		for (Blinkable b : blinkers){
			b.blink();
		}
	}
}
