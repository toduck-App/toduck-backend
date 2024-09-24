package im.toduck.infra.sms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;

import im.toduck.domain.auth.presentation.dto.VerifyCodeDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CoolSmsCodeUtil extends VerifiyCodeUtil {
	@Value("${coolsms-api-key}")
	private String apiKey;
	@Value("${coolsms-api-secret}")
	private String apiSecretKey;
	@Value("${coolsms-api-caller-number}")
	private String callerNumber;

	private static final String SMS_MESSAGE_FORAMT = "[toduck] 아래의 인증번호를 입력해주세요\n";
	private static final String SMS_DOMAIN = "https://api.coolsms.co.kr";

	private DefaultMessageService messageService;

	@PostConstruct
	private void init() {
		this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecretKey, SMS_DOMAIN);
	}

	@Async("threadSMSTaskExecutor")
	@Override
	public void sendVerifyCodeMessage(VerifyCodeDto verifyCodeDto) {
		log.info("send verify code message: {}", verifyCodeDto);
		Message message = new Message();
		message.setFrom(callerNumber);
		message.setTo(verifyCodeDto.getPhoneNumber());
		message.setText(SMS_MESSAGE_FORAMT + verifyCodeDto.getCode());

		SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
		log.info("send message response: {}", response);
	}
}
