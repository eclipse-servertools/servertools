/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manages a single timer that will run and notify an ActionListener when the
 * timer expires. The dispose() method should be called before the timer object
 * is taken down in order to safely bring down the executor and any associated threads.
 */
public class Timer {

	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private TimerRunnable timerRunnable = new TimerRunnable();
	protected ActionListener listener = null;
	private long delay;

	public Timer(long delay, ActionListener curListener) {
		super();
		this.delay = delay;
		listener = curListener;
	}

	/**
	 * Runs the timer if it is stopped or updates the stop time directly
	 * to effectively restart the timer.
	 * only one command should be executed at a time. 
	 */
	public void runTimer(){
		timerRunnable.setStopTime(System.currentTimeMillis() + delay);

		if(!timerRunnable.isRunning() && !timerRunnable.isScheduled()){
			timerRunnable.setIsScheduled(true);
			executor.execute(timerRunnable);
		}
	}

	/**
	 * Cancels the timer and then kills the executor which brings down the thread.
	 */
	public void dispose(){
		if(timerRunnable.isRunning()) {
			timerRunnable.setIsCancelled(true);
		}
		killExecutor();
	}

	public void killExecutor(){
		executor.shutdown();
	}

	public boolean isRunning(){
		return timerRunnable.isRunning();
	}

	public boolean isScheduled(){
		return timerRunnable.isScheduled();
	}

	/**
	 * This is the Runnable that is called by the executor each time we want to
	 * run the timer.
	 */
	class TimerRunnable implements Runnable {

		private boolean isScheduled = false;
		private boolean isRunning = false;
		private boolean isCancelled = false;
		private long stopTime = 0;
		// default is 50 ms
		private long waitTime = 50;

		public void run() {
			isRunning = true;
			isScheduled = false;
			try {
				while (stopTime - System.currentTimeMillis() > 0 && isRunning && !isCancelled) {
					try {
						synchronized (this) {
							wait(waitTime);
						}
						if (Thread.interrupted())
							throw new InterruptedException();
					} catch (InterruptedException e) {
						// Do nothing
					}
				}
				if (!isCancelled) {
					if (listener != null) {
						listener.actionPerformed(new ActionEvent(Timer.this, 0, "", System.currentTimeMillis(), 0));
					}
				} else {
					setIsCancelled(false);
				}
			} finally {
				isRunning = false;
			}
		}

		public synchronized boolean isRunning() {
			return isRunning;
		}

		public synchronized void setRunning(boolean setting) {
			isRunning = setting;
		}

		public synchronized long getStopTime() {
			return stopTime;
		}

		public synchronized void setStopTime(long time) {
			stopTime = time;
		}

		public synchronized long getWaitTime() {
			return waitTime;
		}

		public synchronized void setIsCancelled(boolean cancelled) {
			isCancelled = cancelled;
		}

		public boolean isScheduled() {
			return isScheduled;
		}

		public void setIsScheduled(boolean isScheduled) {
			this.isScheduled = isScheduled;
		}
	}
}
