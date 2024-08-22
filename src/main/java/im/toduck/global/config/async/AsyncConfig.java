package im.toduck.global.config.async;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {
	@Bean(name = "threadSMSTaskExecutor")
	public Executor threadPoolTaskExecutor() { // TODO : 쓰레드 풀 하게 비동기 구성했지만 안에 파라미터 값은 테스트 과정 후 조정 필요
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(3); // 기본 스레드 수
		taskExecutor.setMaxPoolSize(30); // 최대 스레드 수
		taskExecutor.setQueueCapacity(100); // Queue 사이즈
		taskExecutor.setThreadNamePrefix("Executor-");
		return taskExecutor;
	}
}
