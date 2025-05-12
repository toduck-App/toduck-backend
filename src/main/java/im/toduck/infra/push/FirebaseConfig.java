package im.toduck.infra.push;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class FirebaseConfig {

	@Value("${firebase.config-path}")
	private String firebaseConfigPath;

	@PostConstruct
	public void initialize() {
		try {
			if (FirebaseApp.getApps().isEmpty()) {
				ClassPathResource resource = new ClassPathResource(firebaseConfigPath);
				InputStream serviceAccount = resource.getInputStream();

				FirebaseOptions options = FirebaseOptions.builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount))
					.build();

				FirebaseApp.initializeApp(options);
				log.info("Firebase 애플리케이션 초기화 완료");
			}
		} catch (IOException e) {
			log.error("Firebase 초기화 오류", e);
			throw new RuntimeException("Firebase 초기화 중 오류가 발생했습니다", e);
		}
	}
}
