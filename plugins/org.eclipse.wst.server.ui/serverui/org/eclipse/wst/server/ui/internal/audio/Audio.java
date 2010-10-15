/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.audio;

/**
 * Audio is the main interface to the audio plugin.<p>
 *
 * Adding a new sound to your plugin is as easy as ABC:
 * <ul>
 * <li>A: Add the following requires to your plugin.xml:<br>
 *   <pre>&lt;import plugin="org.eclipse.wst.server.util"&gt;</pre></li>
 *
 * <li>B: Define a new sound extension point in your plugin.xml:<br>
 *   <pre>&lt;extension point="org.eclipse.wst.server.util.sound"&gt;
 *     &lt;category id="org.eclipse.myPlugin"
 *       name="My Plugin Name"/&gt;
 *     &lt;sound id="org.eclipse.myPlugin.mySound"
 *       category="org.eclipse.myPlugin"
 *       name="Something Happened"
 *       location="sounds/mySound.wav"/&gt;
 *     &lt;sound id="org.eclipse.myPlugin.myOtherSounds"
 *       category="org.eclipse.myPlugin"
 *       name="Another Event Happened"/&gt;
 *   &lt;/extension&gt;</pre><br>
 *   (the location is optional. If it is not specified, the sound
 *    will not play until the user specifies an audio file)</li>
 *
 * <li>C: Call the sounds when the appropriate events occur within
 *   your plugin:<br>
 *   <pre>org.eclipse.wst.audio.Audio.playSound("org.eclipse.myPlugin.mySound");</pre></li>
 * </ul>
 */
public class Audio {
	/**
	 * AudioCore constructor comment.
	 */
	private Audio() {
		super();
	}

	/**
	 * Plays the sound with the given id.
	 *
	 * @param id java.lang.String
	 */
	public static void playSound(String id) {
		AudioCore.getInstance().playSound(id);
	}
}