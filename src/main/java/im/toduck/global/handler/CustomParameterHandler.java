package im.toduck.global.handler;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomParameterHandler {
	/**
	 * WebDataBinder 를 초기화하여 문자열 매개변수에서 자동으로 공백을 제거합니다.
	 * @param dataBinder 커스터마이즈할 WebDataBinder 인스턴스
	 */
	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		StringTrimmerEditor ste = new StringTrimmerEditor(true);
		dataBinder.registerCustomEditor(String.class, ste);
	}
}
