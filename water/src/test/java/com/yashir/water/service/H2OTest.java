package com.yashir.water.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class H2OTest {
	private final H2O h2o = new H2O();
	 	
	@Test
	void testSingleMolecule() throws Exception {
		log.info("Starting testSingleMolecule");
		
		List<String> output = Collections.synchronizedList(new ArrayList<>());
	
		Thread h1 = new Thread(() -> runSafely(() -> h2o.hydrogen(() -> output.add("H"))));
		Thread h2 = new Thread(() -> runSafely(() -> h2o.hydrogen(() -> output.add("H"))));
		Thread o  = new Thread(() -> runSafely(() -> h2o.oxygen(() -> output.add("O"))));
	
		h1.start();
		h2.start();
		o.start();
	
		h1.join();
		h2.join();
		o.join();
		
		assertThat(output).hasSize(3);
		
		assertThat(output).containsExactlyInAnyOrder("H", "H", "O");
	}

	@Test
	void testMultipleMolecules() throws Exception {
		log.info("Starting testMultipleMolecules");
		
		List<String> output = Collections.synchronizedList(new ArrayList<>());

		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(6);
		CountDownLatch latch = new CountDownLatch(6);

		Runnable releaseH = () -> { output.add("H"); latch.countDown(); };
		Runnable releaseO = () -> { output.add("O"); latch.countDown(); };

		executor.execute(() -> runSafely(() -> h2o.hydrogen(releaseH)));
		executor.execute(() -> runSafely(() -> h2o.hydrogen(releaseH)));
		executor.execute(() -> runSafely(() -> h2o.oxygen(releaseO)));
		executor.execute(() -> runSafely(() -> h2o.hydrogen(releaseH)));
		executor.execute(() -> runSafely(() -> h2o.hydrogen(releaseH)));
		executor.execute(() -> runSafely(() -> h2o.oxygen(releaseO)));

		latch.await(2, TimeUnit.SECONDS);
		
		assertThat(output).hasSize(6);
		assertThat(Collections.frequency(output, "H")).isEqualTo(4);
		assertThat(Collections.frequency(output, "O")).isEqualTo(2);
	}
	
	@Test
	void testorder() throws Exception {
		log.info("Starting testorder");
		
		List<String> output = Collections.synchronizedList(new ArrayList<>());

		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(9);
		CountDownLatch latch = new CountDownLatch(9);

		Runnable releaseH = () -> { output.add("H"); latch.countDown(); };
		Runnable releaseO = () -> { output.add("O"); latch.countDown(); };

		executor.execute(() -> runSafely(() -> h2o.oxygen(releaseO)));
		executor.execute(() -> runSafely(() -> h2o.oxygen(releaseO)));
		executor.execute(() -> runSafely(() -> h2o.hydrogen(releaseH)));
		executor.execute(() -> runSafely(() -> h2o.hydrogen(releaseH)));
		executor.execute(() -> runSafely(() -> h2o.hydrogen(releaseH)));
		executor.execute(() -> runSafely(() -> h2o.hydrogen(releaseH)));
		executor.execute(() -> runSafely(() -> h2o.hydrogen(releaseH)));
		executor.execute(() -> runSafely(() -> h2o.oxygen(releaseO)));
		executor.execute(() -> runSafely(() -> h2o.hydrogen(releaseH)));

		latch.await(2, TimeUnit.SECONDS);
		
		assertThat(output).hasSize(9);
		assertThat(Collections.frequency(output, "H")).isEqualTo(6);
		assertThat(Collections.frequency(output, "O")).isEqualTo(3);
	}

	private void runSafely(ThrowingRunnable r) {
		try {
			r.run();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@FunctionalInterface
	interface ThrowingRunnable {
		void run() throws Exception;
	}
	
}
