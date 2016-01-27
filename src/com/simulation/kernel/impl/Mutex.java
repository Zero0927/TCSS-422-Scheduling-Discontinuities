//Siyuan Zhou
package com.simulation.kernel.impl;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.simulation.core.MyProcess;
import com.simulation.core.Scheduler;
import com.simulation.kernel.KernelService;

public class Mutex implements KernelService {

	/**
	 * 
	 */
	private Scheduler callback;

	private String mutexName;

	private MyProcess possess;

	private Queue<MyProcess> waitingQueue;

	public Mutex(Scheduler scheduler, String name) {
		mutexName = name;
		callback = scheduler;

		waitingQueue = new ConcurrentLinkedQueue<MyProcess>();
	}

	@Override
	public void addWaitingProcess(MyProcess proc) {
		if (proc == null)
			return;
		waitingQueue.add(proc);
	}

	@Override
	public String kernelServiceName() {
		return mutexName;
	}

	@Override
	public void releaseSevice(MyProcess p) {
		if (p.equals(possess)) {
			possess = null;
			MyProcess newPossess = waitingQueue.poll();
			if (newPossess == null)
				return;
			possess = newPossess;
			callback.callbackNotify(newPossess);
		}
	}

	@Override
	public void enterSevice(MyProcess p) {
		if (p != null)
			possess = p;
	}

	@Override
	public boolean isPossess() {
		return possess != null;
	}

}
