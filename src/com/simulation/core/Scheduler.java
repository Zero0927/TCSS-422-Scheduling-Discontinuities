//Siyuan Zhou
package com.simulation.core;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.simulation.io.IODevice;
import com.simulation.io.impl.Keyboard;
import com.simulation.io.impl.Monitor;
import com.simulation.io.impl.Mouse;
import com.simulation.io.impl.Printer;
import com.simulation.kernel.KernelService;
import com.simulation.kernel.impl.Mutex;

/**
 * The processor of simulation
 *
 */
public class Scheduler {
	private final int QUANTUM = 300;
	private Queue<MyProcess> readyQueue;

	private IODevice[] ioDevices;
	private KernelService[] kServices;

	private ProcessGen procGenerator;

	public Scheduler() {
		readyQueue = new ConcurrentLinkedQueue<MyProcess>();

		// initial io devices
		ioDevices = new IODevice[4];
		ioDevices[0] = new Keyboard(this);
		ioDevices[1] = new Monitor(this);
		ioDevices[2] = new Mouse(this);
		ioDevices[3] = new Printer(this);

		// initial kernel service devices
		kServices = new KernelService[4];
		kServices[0] = new Mutex(this, "m1");
		kServices[1] = new Mutex(this, "m2");
		kServices[2] = new Mutex(this, "m3");
		kServices[3] = new Mutex(this, "m4");

		procGenerator = new ProcessGen(ioDevices, kServices);
	}

	public void start() {
		// Add initial process
		readyQueue.add(procGenerator.intialProcess());
		while (true) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Generate new process
			procGenerator.buildRandom(readyQueue);

			// Get one ready process from list
			MyProcess p = readyQueue.poll();

			if (p.getStatus() == PCB.STAT_IDLE) {
				readyQueue.add(p);
				continue;
			}

			p.setStatus(PCB.STAT_RUNNING);

			switch (p.doProcess(QUANTUM)) {
			case 0: // work done
				procGenerator.rebuildRandom(readyQueue, p);
				break;
			case 1: // io event
				p.setStatus(PCB.STAT_BLOCK);
				p.getPCB().io.addWaitingProcess(p);
				break;
			case 2: // mutex lock
				p.setStatus(PCB.STAT_BLOCK);
				p.getPCB().ks.addWaitingProcess(p);
				break;
			case 3: // mutex unlock
				p.getPCB().ks.releaseSevice(p);
			case 4:// time consuming
				p.setStatus(PCB.STAT_READY);
				readyQueue.add(p);
				break;
			}

			// do io service
			for (int i = 0; i < ioDevices.length; i++) {
				ioDevices[i].doWork(QUANTUM);
			}
		}
	}

	/**
	 * Add the process into the ready queue
	 * 
	 * @param proc
	 */
	public void callbackNotify(MyProcess proc) {
		proc.setStatus(PCB.STAT_READY);
		readyQueue.add(proc);
	}

	public static void main(String[] args) {
		Scheduler myProcesser = new Scheduler();
		myProcesser.start();
	}
}
