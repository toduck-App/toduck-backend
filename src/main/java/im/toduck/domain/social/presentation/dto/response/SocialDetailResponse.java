package im.toduck.domain.social.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.Builder;

@Builder
public record SocialDetailResponse(
	Long id,
	OwnerDto owner,
	String content,
	List<SocialCategoryDto> categories,
	boolean hasImages,
	List<SocialImageDto> images,
	LikeDto likeInfo,
	List<CommentDto> comments,
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	LocalDateTime createdAt
) {
}
