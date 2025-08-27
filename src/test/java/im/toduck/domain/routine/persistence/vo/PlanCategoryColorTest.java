package im.toduck.domain.routine.persistence.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import im.toduck.global.exception.VoException;

class PlanCategoryColorTest {

	@Test
	void plantCategoryColorFromTest() {
		assertThat(PlanCategoryColor.from("#FF5733")).isEqualTo(PlanCategoryColor.from("#FF5733"));
		assertThat(PlanCategoryColor.from(null)).isEqualTo(PlanCategoryColor.from(null));

		assertThatThrownBy(() ->
			PlanCategoryColor.from("#22")
		).isInstanceOf(VoException.class);
	}

}
