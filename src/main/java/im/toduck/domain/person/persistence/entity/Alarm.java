package im.toduck.domain.person.persistence.entity;

@Deprecated
/*
 * 추후 삭제 예정
 * */
public enum Alarm {
	TEN(10),
	TIRTY(30),
	SIXTY(60),
	OFF(0);

	private final int value;

	Alarm(int value) {
		this.value = value;
	}
}
