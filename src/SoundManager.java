import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;

public class SoundManager {
    private final Set<String> warnedSounds = new HashSet<>();

    public void play(String soundName) {
        File file = resolveSoundFile(soundName);

        if (!file.exists()) {
            warnOnce(soundName, "sound file not found: " + file.getPath());
            return;
        }

        try (AudioInputStream stream = AudioSystem.getAudioInputStream(file)) {
            Clip clip = AudioSystem.getClip();
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
            clip.open(stream);
            clip.start();
        } catch (Exception ex) {
            warnOnce(soundName, "could not play sound: " + ex.getMessage());
        }
    }

    private File resolveSoundFile(String soundName) {
        String fileName = soundName.endsWith(".wav") ? soundName : soundName + ".wav";
        return GameConfig.SOUND_DIR.resolve(fileName).toFile();
    }

    private void warnOnce(String soundName, String message) {
        if (warnedSounds.add(soundName)) {
            System.err.println("Warning: " + message);
        }
    }
}
