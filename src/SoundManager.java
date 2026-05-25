import java.io.File;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;

public class SoundManager {
    private final Set<String> warnedSounds = new HashSet<>();

    public void play(String soundName) {
        String fileName = normalizeFileName(soundName);

        if (playFromResource(fileName, soundName)) {
            return;
        }

        playFromFile(fileName, soundName);
    }

    private boolean playFromResource(String fileName, String soundName) {
        String resourcePath = "/assets/sounds/" + fileName;
        InputStream inputStream = SoundManager.class.getResourceAsStream(resourcePath);

        if (inputStream == null) {
            return false;
        }

        try (AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(inputStream))) {
            playClip(stream);
            return true;
        } catch (Exception ex) {
            warnOnce(soundName, "could not play bundled sound: " + ex.getMessage());
            return true;
        }
    }

    private void playFromFile(String fileName, String soundName) {
        File file = GameConfig.SOUND_DIR.resolve(fileName).toFile();

        if (!file.exists()) {
            warnOnce(soundName, "sound file not found: " + file.getPath());
            return;
        }

        try (AudioInputStream stream = AudioSystem.getAudioInputStream(file)) {
            playClip(stream);
        } catch (Exception ex) {
            warnOnce(soundName, "could not play sound: " + ex.getMessage());
        }
    }

    private void playClip(AudioInputStream stream) throws Exception {
        Clip clip = AudioSystem.getClip();
        clip.addLineListener(event -> {
            if (event.getType() == LineEvent.Type.STOP) {
                clip.close();
            }
        });
        clip.open(stream);
        clip.start();
    }

    private String normalizeFileName(String soundName) {
        return soundName.endsWith(".wav") ? soundName : soundName + ".wav";
    }

    private void warnOnce(String soundName, String message) {
        if (warnedSounds.add(soundName)) {
            System.err.println("Warning: " + message);
        }
    }
}
