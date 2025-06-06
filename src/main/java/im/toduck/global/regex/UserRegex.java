package im.toduck.global.regex;

public interface UserRegex {
	String PHONE_NUMBER_REGEXP = "^01\\d{9}$";
	//TODO : 다른 팀원 의견 들어봐야함 현재는 영문 숫자 특수기호 조합 5자리 이상
	String LOGIN_ID_REGEXP = "^[a-z0-9_-]{5,20}$";
	//TODO : 다른 팀원 의견 들어봐야함 현재는 영문 숫자 특수기호 조합 8자리 이상
	String PASSWORD_REGEXP = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$";
	String VERIFIED_CODE_REGEXP = "[0-9]{6}";
	String NICKNAME_REGEXP = "^[a-zA-Z0-9가-힣]{2,8}$";
}
