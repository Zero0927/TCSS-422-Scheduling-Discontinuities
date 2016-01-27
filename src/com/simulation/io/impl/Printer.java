//Siyuan Zhou
package com.simulation.io.impl;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.simulation.core.LogPrinter;
import com.simulation.core.MyProcess;
import com.simulation.core.Scheduler;
import com.simulation.io.IODevice;


public class Printer implements IODevice {
	/**
	 * 
	 */
	private Scheduler callback;

	private String printerName;

	private Queue<MyProcess> waitingQueue;
	
	private int currentWorkTime;

	public Printer(Scheduler scheduler) {
		callback = scheduler;
		printerName = "Printer output";
		waitingQueue = new ConcurrentLinkedQueue<MyProcess>();
		
		currentWorkTime = 0;
	}

	@Override
	public void addWaitingProcess(MyProcess proc) {
		if (proc == null)
			return;
		waitingQueue.add(proc);
		currentWorkTime = new Random(System.currentTimeMillis()).nextInt(2000) ;
	}

	@Override
	public String ioDeviceName() {
		return printerName;
	}

	@Override
	public void doWork(int time) {
		if (waitingQueue.size() == 0)
			return;
		if (currentWorkTime < time){
			MyProcess proc = waitingQueue.poll();
			LogPrinter.out(String.format(
					"PID %d I/O completion interrupt %s - Back in ready queue at %d of %d",
					proc.getPCB().pid, printerName, proc.getcursorTime(), proc.getLiftTime()));
			callback.callbackNotify(proc);
		}else 
			currentWorkTime -= time;
	}

}
