/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.test.content.controller;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.recyclerview.controller.SoundController;
import org.catrobat.catroid.uiespresso.util.FileTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.common.Constants.BACKPACK_SOUND_DIRECTORY;
import static org.catrobat.catroid.uiespresso.util.FileTestUtils.assertFileDoesNotExist;
import static org.catrobat.catroid.uiespresso.util.FileTestUtils.assertFileExists;
import static org.catrobat.catroid.utils.Utils.buildProjectPath;

@RunWith(AndroidJUnit4.class)
public class SoundControllerTest {

	private Project project;
	private Scene scene;
	private Sprite sprite;
	private SoundInfo soundInfo;

	@Before
	public void setUp() throws IOException {
		clearBackPack();
		createProject();
	}

	@After
	public void tearDown() throws IOException {
		deleteProject();
		clearBackPack();
	}

	@Test
	public void testCopySound() throws IOException {
		SoundController controller = new SoundController();
		SoundInfo copy = controller.copy(soundInfo, scene, scene, sprite);

		assertEquals(1, sprite.getSoundList().size());
		assertSoundFileExists(copy.getFileName());
	}

	@Test
	public void testDeleteSound() throws IOException {
		SoundController controller = new SoundController();
		String deletedSoundFileName = soundInfo.getFileName();
		controller.delete(soundInfo, scene);

		assertEquals(1, sprite.getSoundList().size());
		assertSoundFileDoesNotExist(deletedSoundFileName);
	}

	@Test
	public void testPackSound() throws IOException {
		SoundController controller = new SoundController();
		SoundInfo packedSound = controller.pack(soundInfo, scene);

		assertEquals(0, BackPackListManager.getInstance().getBackPackedSounds().size());
		assertFileExists(new File(BACKPACK_SOUND_DIRECTORY, packedSound.getFileName()));
	}

	@Test
	public void testDeleteSoundFromBackPack() throws IOException {
		SoundController controller = new SoundController();
		SoundInfo packedSound = controller.pack(soundInfo, scene);
		controller.deleteFromBackpack(packedSound);

		assertEquals(0, BackPackListManager.getInstance().getBackPackedSounds().size());
		assertFileDoesNotExist(new File(BACKPACK_SOUND_DIRECTORY, packedSound.getFileName()));

		assertEquals(1, sprite.getSoundList().size());
		assertSoundFileExists(soundInfo.getFileName());
	}

	@Test
	public void testUnpackSound() throws IOException {
		SoundController controller = new SoundController();
		SoundInfo packedSound = controller.pack(soundInfo, scene);
		SoundInfo unpackedSound = controller.unpack(packedSound, scene, sprite);

		assertEquals(0, BackPackListManager.getInstance().getBackPackedSounds().size());
		assertFileExists(new File(BACKPACK_SOUND_DIRECTORY, packedSound.getFileName()));

		assertEquals(1, sprite.getSoundList().size());
		assertSoundFileExists(unpackedSound.getFileName());
	}

	@Test
	public void testDeepCopySound() throws IOException {
		SoundController controller = new SoundController();
		SoundInfo copy = controller.copy(soundInfo, scene, scene, sprite);

		assertSoundFileExists(copy.getFileName());

		controller.delete(copy, scene);

		assertSoundFileDoesNotExist(copy.getFileName());
		assertSoundFileExists(soundInfo.getFileName());
	}

	private void assertSoundFileExists(String fileName) {
		assertFileExists(new File(new File(scene.getPath(), Constants.SOUND_DIRECTORY), fileName));
	}

	private void assertSoundFileDoesNotExist(String fileName) {
		assertFileDoesNotExist(new File(new File(scene.getPath(), Constants.SOUND_DIRECTORY), fileName));
	}

	private void clearBackPack() throws IOException {
		if (BACKPACK_SOUND_DIRECTORY.exists()) {
			StorageHandler.deleteDir(BACKPACK_SOUND_DIRECTORY);
		}
		BACKPACK_SOUND_DIRECTORY.mkdirs();
	}

	private void createProject() throws IOException {
		project = new Project(InstrumentationRegistry.getTargetContext(), "SoundControllerTest");
		scene = project.getDefaultScene();
		ProjectManager.getInstance().setCurrentProject(project);

		sprite = new Sprite("testSprite");
		scene.addSprite(sprite);

		File soundFile = FileTestUtils.copyResourceFileToProject(
				project.getName(), scene.getName(),
				"longsound.mp3",
				org.catrobat.catroid.test.R.raw.longsound,
				InstrumentationRegistry.getContext(),
				FileTestUtils.FileTypes.SOUND
		);

		soundInfo = new SoundInfo("testSound", soundFile.getName());
		sprite.getSoundList().add(soundInfo);

		StorageHandler.getInstance().saveProject(project);
	}

	private void deleteProject() throws IOException {
		File projectDir = new File(buildProjectPath(project.getName()));
		if (projectDir.exists()) {
			StorageHandler.deleteDir(projectDir);
		}
	}
}
