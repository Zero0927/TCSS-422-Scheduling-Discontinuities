//Siyuan Zhou
package com.simulation.io;
import com.simulation.core.MyProcess;

/**
 * Interface of IO device
 *
 */
public interface IODevice {
	/**
	 * Add process of the io waiting queue
	 * @param proc
	 */
	public void addWaitingProcess(MyProcess proc);
	
	/**
	 * Get the device name
	 * @return
	 */
	public String ioDeviceName();

	/**
	 * Do work the of io
	 * @param time
	 */
	public void doWork(int time);
}
