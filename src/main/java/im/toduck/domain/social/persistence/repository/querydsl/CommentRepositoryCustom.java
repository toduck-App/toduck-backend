package im.toduck.domain.social.persistence.repository.querydsl;

import java.util.List;

import org.springframework.data.domain.Pageable;

import im.toduck.domain.mypage.presentation.dto.response.MyCommentsResponse;

public interface CommentRepositoryCustom {
	List<MyCommentsResponse> findMyCommentsWithProjection(
		Long userId,
		Long cursor,
		Pageable pageable
	);
}
