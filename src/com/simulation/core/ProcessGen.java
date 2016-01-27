//Siyuan Zhou
package com.simulation.core;

import java.util.Queue;
import java.util.Random;

import com.simulation.io.IODevice;
import com.simulation.kernel.KernelService;

/**
 * Process generator
 */
public class ProcessGen {
	private Random rand;

	private int MAXID = 900;
	private int MAXJOB = 1000;

	private IODevice[] ioDevices;
	private KernelService[] kServices;

	public ProcessGen(IODevice[] ioDevices, KernelService[] kServices) {
		rand = new Random(System.currentTimeMillis());
		this.ioDevices = ioDevices;
		this.kServices = kServices;
	}

	/**
	 * Create an idle process when none task
	 * 
	 * @return
	 */
	public MyProcess intialProcess() {
		MyProcess proc = new MyProcess(0, 0);
		proc.setStatus(PCB.STAT_IDLE);
		return proc;
	}

	/**
	 * Create new process
	 * 
	 * @return
	 */
	private MyProcess createProcess() {
		MyProcess proc = new MyProcess(rand.nextInt(MAXID) + 1,
				rand.nextInt(MAXJOB) + 300);
		int n = 4 + rand.nextInt(4);
		int[] ioInerupt = new int[n];
		n = n % 2 == 0 ? n : n + 1;
		int[] ksInerupt = new int[n];

		for (int i = 0; i < ioInerupt.length; i++) {
			ioInerupt[i] = 100 + rand.nextInt(100) + i * 200;
		}

		for (int i = 0; i < ksInerupt.length; i++) {
			ksInerupt[i] = 100 + rand.nextInt(100) + i * 200;
		}
		int k = rand.nextInt(4);
		proc.newIODevice(ioDevices[k], ioInerupt);
		proc.newKernelServiceDevice(kServices[k], ksInerupt);
		proc.setStatus(PCB.STAT_READY);

		return proc;
	}

	public void buildRandom(Queue<MyProcess> readyQueue) {
		if (readyQueue.size() < 10) {
			// Add a bunch of process
			for (int i = 0; i < 25; i++) {
				MyProcess proc = createProcess();
				readyQueue.add(proc);
			}

		} else if (readyQueue.size() < 30) {
			MyProcess proc = createProcess();
			readyQueue.add(proc);
		}

	}

	public void rebuildRandom(Queue<MyProcess> readyQueue, MyProcess p) {
		if (rand.nextInt() % 2 == 0) {
			p.setStatus(PCB.STAT_CLOSE);
			p.close();
		} else {
			p.restart(rand.nextInt(MAXJOB) + 300);
			p.setStatus(PCB.STAT_READY);
			readyQueue.add(p);
		}

	}

}
