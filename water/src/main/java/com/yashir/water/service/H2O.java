package com.yashir.water.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class H2O {

	private final BlockingQueue<Runnable> hydrogenQueue = new LinkedBlockingQueue<>(2);
    private Object hLock = new Object();
    private Object oLock = new Object();
    
    public H2O() {
    }

    /**
     * Adding new hydrogen atom to the hydrogen queue.
     * Only 2 are allowed.
     * Releasing the available H atoms ad block if queue is full.
     * @throws InterruptedException
     */
    public void hydrogen (Runnable releaseHydrogen) throws InterruptedException {
    	synchronized (hLock) {
	    	hydrogenQueue.put(releaseHydrogen); // Add H to queue and block if queue is full
	    	
	    	log.info("New H atom is in");
	        
	    	releaseHydrogen.run();
    	}
    }

    /**
     * Trying to bond water molecule.
     * Blocking process for other "O" atoms. 
     * @throws InterruptedException
     */
    public void oxygen (Runnable releaseOxygen) throws InterruptedException {
        synchronized (oLock) {
        	log.info("New O atom is in");
            
        	//Wait for 2 hydrogens to be available, then consume them
        	hydrogenQueue.take();
        	log.debug("First H atom bonded");
            
        	hydrogenQueue.take();
        	log.debug("Second H atom bonded");

            releaseOxygen.run();
            log.debug("O atom release");
            log.info("Water molecule completed");

        }
    }

}
