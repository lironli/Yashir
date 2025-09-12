package com.yashir.oxygen.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class H2OService {

	private final BlockingQueue<Runnable> hydrogens = new LinkedBlockingQueue<>();
    private final BlockingQueue<Runnable> oxygens = new LinkedBlockingQueue<>();
    private final AtomicInteger waterMolecules = new AtomicInteger(0);

    /**
     * Adding new hydrogen atom to the hydrogen queue.
     * Calling @tryBond() function to check if we can create H2O molecule.
     * @throws InterruptedException
     */
    public void hydrogen() throws InterruptedException {
        hydrogens.put(() -> log.debug("H atom added"));
        log.info("Hydrogen entered. Total H={}", hydrogens.size());
        tryBond();
    }

    /**
     * Adding new oxygen atom to the oxygen queue.
     * Calling @tryBond() function to check if we can create H2O molecule.
     * @throws InterruptedException
     */
    public void oxygen() throws InterruptedException {
        oxygens.put(() -> log.debug("O atom added"));
        log.info("Oxygen entered. Total O={}", oxygens.size());
        tryBond();
    }

    /**
     * Attempt to bond 2 hydrogen and one oxygen.
     * 
     * This method is synchronized to prevent multiple threads taking atoms out of the queues at the same time, 
     * making sure the condition to create water molecule will not break after it was checked.
     * 
     * This method calls the hydrogen and oxygen threads run().
     * 
     * @throws InterruptedException
     */
    private synchronized void tryBond() throws InterruptedException {
        if (hydrogens.size() >= 2 && oxygens.size() >= 1) {
        	try {
        		log.debug("Watter molecule is available. Trying to bond.");
                
        		Runnable h1 = hydrogens.take();
                Runnable h2 = hydrogens.take();
                Runnable o  = oxygens.take();

                h1.run();
                h2.run();
                o.run();

                log.info("Water molecule created. Remaining H={}, O={}", hydrogens.size(), oxygens.size());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Bonding interrupted", e);
            }
        } else {
        	log.debug("Not enough atoms yet. H={}, O={}", hydrogens.size(), oxygens.size());
        }
    }

    public int getWaterMoleculeCount() {
        return waterMolecules.get();
    }

    public int getHydrogenQueueSize() {
        return hydrogens.size();
    }

    public int getOxygenQueueSize() {
        return oxygens.size();
    }
	
}
