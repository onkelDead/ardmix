package de.paraair.ardmix;

import java.util.HashSet;
import java.util.Set;

public class ToggleGroup {
	
	private Set<ToggleListener> group = new HashSet<ToggleListener>();
	
	public void addToGroup(ToggleListener listener){
		group.add(listener);
	}

	/**
	 * Toggle the group
	 * 
	 * @param src
	 */
	public void toggle(ToggleListener src){
		
		for (ToggleListener listener : group){
			
			if (src == listener){
				listener.toggle();
			}
		}
	}
	
	/**
	 * Toggle the group and set the state on the src.
	 * @param src
	 */
	public void toggle(ToggleListener src, boolean state){
		
		if (src.getToggleState() == state){
			return;
		}
		
		for (ToggleListener listener : group){
			
			if (src == listener){
				listener.setToggleState(state);
			}
			else {
				listener.setToggleState(!state);
			}
		}
	}

}
