package vn.edu.fpt.transitlink.profile.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
@Profile("!test") // Exclude from test profile
public class FirebaseConfig {

    @PostConstruct
    public void init() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            String configPath = System.getenv("FIREBASE_CONFIG_PATH");
            if (configPath == null || configPath.isEmpty()) {
                throw new IllegalStateException("Missing FIREBASE_CONFIG_PATH environment variable");
            }

            try (FileInputStream serviceAccount = new FileInputStream(configPath)) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
            }
        }
    }
}

