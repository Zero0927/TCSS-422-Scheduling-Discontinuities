//Siyuan Zhou
package com.simulation.core;

import com.simulation.io.IODevice;
import com.simulation.kernel.KernelService;

/**
 * Class of process simulation
 */
public class MyProcess {

	/**
	 * The pcb of process
	 */
	private PCB pcb;

	/**
	 * The count of quanta has done
	 */
	private int doneWorkTime;

	private boolean requestMutex;

	/**
	 * Create a new process with it's total execute time
	 * 
	 * @param execute
	 */
	public MyProcess(int pid, int execute) {
		if (pid != 0)
			LogPrinter.out("PID " + pid + " New - Created with job quantum "
					+ execute);
		pcb = new PCB();
		pcb.pid = pid;
		pcb.liftTime = execute;
		doneWorkTime = 0;
	}

	/**
	 * Do work of process
	 * 
	 * @param quantum
	 * @return return 1 for io interrupt, return 2 for ks lock, return 3 for ks
	 *         unlock, return 4 for time consuming over, return 0 for done of
	 *         work
	 */
	public int doProcess(int quantum) {
		// End of the time slot
		int thisEnd = Math.min(quantum, pcb.liftTime - doneWorkTime);
		for (int i = doneWorkTime+1; i <= thisEnd; i++) {
			// check io event
			for (int j = 0; j < pcb.ioEvent.length; j++) {
				if (i >= pcb.ioEvent[j] && i % pcb.ioEvent[j] == 0) {
					doneWorkTime = i;
					LogPrinter.out(String.format(
							"PID %d %s - Blocked at quantium %d of %d",
							pcb.pid, pcb.io.ioDeviceName(), i, pcb.liftTime));
					return 1;
				}
			}
			// check kernel service event
			for (int j = 0; j < pcb.ksEvent.length; j++) {
				if (i >= pcb.ksEvent[j] && i % pcb.ksEvent[j] == 0) {
					doneWorkTime = i;
					if (!requestMutex) {
						LogPrinter
								.out(String
										.format("PID %d Request MutexLock(%s) - Blocked at quantium %d of %d",
												pcb.pid,
												pcb.ks.kernelServiceName(), i,
												pcb.liftTime));
						return 2;

					} else {
						LogPrinter
								.out(String
										.format("PID %d Realese MutexLock(%s) - Blocked at quantium %d of %d",
												pcb.pid,
												pcb.ks.kernelServiceName(), i,
												pcb.liftTime));
						return 3;
					}

				}
			}
		}

		LogPrinter.out("PID " + pcb.pid + "Execution - Running quantum "
				+ thisEnd);
		// check time slot
		if (doneWorkTime + quantum >= pcb.liftTime) {
			return 0;
		} else {

			pcb.runThreads = (int) (System.currentTimeMillis() % 100);
			doneWorkTime += quantum;
			return 4;
		}
	}

	/**
	 * Add new io event
	 * 
	 * @param ioDevices
	 * 
	 * @param deviceEvent
	 */
	public void newIODevice(IODevice ioDevices, int[] deviceEvent) {
		pcb.io = ioDevices;
		pcb.ioEvent = deviceEvent;
	}

	/**
	 * Add new kernel service event
	 * 
	 * @param kServices
	 * 
	 * @param ksEvent
	 */
	public void newKernelServiceDevice(KernelService kServices, int[] ksEvent) {
		pcb.ks = kServices;
		pcb.ksEvent = ksEvent;
	}

	/**
	 * Restart of this process
	 * 
	 * @param execute
	 */
	public void restart(int execute) {
		LogPrinter.out("PID " + pcb.pid + " - Restart with job " + execute);
		pcb.liftTime = execute;
		doneWorkTime = 0;
	}

	/**
	 * 
	 */
	public void close() {
		LogPrinter.out("PID " + pcb.pid + " - Destroy");
	}

	/**
	 * @return the pStatus
	 */
	public int getStatus() {
		return pcb.pStatus;
	}

	/**
	 * @param pStatus
	 *            the pStatus to set
	 */
	public void setStatus(int pStatus) {
		this.pcb.pStatus = pStatus;
	}

	/**
	 * @return the pid
	 */
	public int getPid() {
		return pcb.pid;
	}

	/**
	 * @return the liftTime
	 */
	public int getLiftTime() {
		return pcb.liftTime;
	}

	/**
	 * @return the liftTime
	 */
	public int getcursorTime() {
		return doneWorkTime;
	}

	/**
	 * Return the process control block
	 * 
	 * @return
	 */
	public PCB getPCB() {
		return pcb;
	}
}
