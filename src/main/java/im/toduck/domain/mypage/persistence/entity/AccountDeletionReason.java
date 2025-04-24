package im.toduck.domain.mypage.persistence.entity;

public enum AccountDeletionReason {
	HARD_TO_USE("사용 방법이 어려워요"),
	NO_FEATURES("원하는 기능이 없어요"),
	MANY_ERRORS("오류가 자주 발생해요"),
	BETTER_APP("더 좋은 어플이 있어요"),
	SWITCH_ACCOUNT("다른 계정으로 다시 가입하고 싶어요"),
	OTHER("기타");

	private final String description;

	AccountDeletionReason(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}
}
