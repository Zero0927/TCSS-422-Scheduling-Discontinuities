//Siyuan Zhou
package com.simulation.core;

import com.simulation.io.IODevice;
import com.simulation.kernel.KernelService;

/**
 * Process control block simulation
 */
public class PCB {
	public static final int STAT_IDLE = 0x000;
	public static final int STAT_RUNNING = 0x001;
	public static final int STAT_BLOCK = 0x002;
	public static final int STAT_READY = 0x003;
	public static final int STAT_CLOSE = 0x999;

	/**
	 * process id
	 */
	public int pid;

	public int runThreads;
	
	/**
	 * status of process
	 */
	public int pStatus;

	/**
	 * the count of the number of quanta that have been used by the process
	 */
	public int liftTime;
	
	/**
	 * IO device event
	 */
	public int[] ioEvent;
	
	/**
	 * Kernel service event
	 */
	public int[] ksEvent;
	/**
	 * The io device of this process
	 */
	public IODevice io;
	
	/**
	 * The kernel service of this process
	 */
	public KernelService ks;
}
