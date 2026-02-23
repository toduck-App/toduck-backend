package im.toduck.domain.auth.domain.usecase;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.springframework.transaction.annotation.Transactional;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import im.toduck.domain.auth.domain.service.JwtService;
import im.toduck.domain.auth.presentation.dto.request.WebLoginAuthorizeRequest;
import im.toduck.domain.auth.presentation.dto.response.WebSessionCreateResponse;
import im.toduck.domain.auth.presentation.dto.response.WebSessionStatusResponse;
import im.toduck.global.annotation.UseCase;
import im.toduck.infra.redis.weblogin.WebLoginSession;
import im.toduck.infra.redis.weblogin.WebLoginSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class WebLoginUseCase {
	private static final String UNIVERSAL_LINK_BASE = "https://toduck.app/_ul/w/";
	private static final int QR_CODE_SIZE = 300;

	private final WebLoginSessionService webLoginSessionService;
	private final JwtService jwtService;

	@Transactional
	public WebSessionCreateResponse createWebSession() {
		WebLoginSession session = webLoginSessionService.createSession();
		String qrCodeUrl = UNIVERSAL_LINK_BASE + session.getSessionToken();
		String qrImageBase64 = generateQrCodeBase64(qrCodeUrl);

		log.info("웹 로그인 세션 생성 - SessionToken: {}", session.getSessionToken());

		return WebSessionCreateResponse.builder()
			.sessionToken(session.getSessionToken())
			.qrImageBase64(qrImageBase64)
			.build();
	}

	@Transactional
	public boolean authorizeWebSession(final Long userId, final String role, final WebLoginAuthorizeRequest request) {
		return webLoginSessionService.findBySessionToken(request.sessionToken())
			.map(session -> {
				session.approve(userId, role);
				webLoginSessionService.save(session);
				log.info("웹 로그인 세션 승인 - UserId: {}, SessionToken: {}", userId, request.sessionToken());
				return true;
			})
			.orElseGet(() -> {
				log.info("웹 로그인 세션 없음/만료 - UserId: {}, SessionToken: {}", userId, request.sessionToken());
				return false;
			});
	}

	@Transactional
	public WebSessionStatusResponse getWebSessionStatus(final String sessionToken) {
		return webLoginSessionService.findBySessionToken(sessionToken)
			.map(session -> {
				if (session.isApproved()) {
					String accessToken = jwtService.createWebAccessToken(
						session.getApprovedUserId(),
						session.getApprovedUserRole()
					);
					webLoginSessionService.deleteSession(sessionToken);
					log.info("웹 로그인 토큰 발급 완료 - UserId: {}, SessionToken: {}",
						session.getApprovedUserId(), sessionToken);
					return WebSessionStatusResponse.approved(accessToken, session.getApprovedUserId());
				}
				return WebSessionStatusResponse.pending();
			})
			.orElseGet(() -> {
				log.info("웹 로그인 세션 만료 또는 미존재 - SessionToken: {}", sessionToken);
				return WebSessionStatusResponse.expired();
			});
	}

	private String generateQrCodeBase64(final String content) {
		try {
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE);
			BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ImageIO.write(qrImage, "PNG", outputStream);
			byte[] imageBytes = outputStream.toByteArray();

			return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
		} catch (WriterException | IOException e) {
			log.error("QR 코드 생성 실패", e);
			throw new RuntimeException("QR 코드 생성 중 오류가 발생했습니다.", e);
		}
	}
}
