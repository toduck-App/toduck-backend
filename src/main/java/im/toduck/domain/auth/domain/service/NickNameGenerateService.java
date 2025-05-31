package im.toduck.domain.auth.domain.service;

import java.util.Random;

import org.springframework.stereotype.Service;

import im.toduck.domain.user.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NickNameGenerateService { //TODO : 닉네임 랜덤 생성 팀원과 논의 필요
	private final UserRepository userRepository;

	private static final String[] ADJECTIVES = {
		"행복한", "밝은", "활기찬", "용감한", "장난치는", "매력적인", "똑똑한", "힘찬", "유쾌한"
	};
	private static final String[] NOUNS = {
		"오리", "꽥꽥이", "깃털", "연못", "뒤뚱이", "부리", "날개", "둥지", "물갈퀴"
	};

	public String generateRandomNickname() {
		Random random = new Random();
		String adjective;
		String noun;
		int number; // 100부터 999까지의 3자리 숫자
		do {
			adjective = ADJECTIVES[random.nextInt(ADJECTIVES.length)];
			noun = NOUNS[random.nextInt(NOUNS.length)];
			number = 100 + random.nextInt(900); // 100부터 999까지의 3자리 숫자

		} while (userRepository.existsByNickname(adjective + noun + number));
		return adjective + noun + number;

	}
}
