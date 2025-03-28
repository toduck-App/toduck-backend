package im.toduck.domain.social.common.mapper;

import java.util.List;

import im.toduck.domain.routine.common.mapper.RoutineMapper;
import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.presentation.dto.response.RoutineDetailResponse;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialImageFile;
import im.toduck.domain.social.presentation.dto.response.CommentDto;
import im.toduck.domain.social.presentation.dto.response.OwnerDto;
import im.toduck.domain.social.presentation.dto.response.SocialCreateResponse;
import im.toduck.domain.social.presentation.dto.response.SocialDetailResponse;
import im.toduck.domain.social.presentation.dto.response.SocialImageDto;
import im.toduck.domain.social.presentation.dto.response.SocialLikeDto;
import im.toduck.domain.social.presentation.dto.response.SocialResponse;
import im.toduck.domain.user.persistence.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SocialMapper {
	public static Social toSocial(
		final User user,
		final Routine routine,
		final String title,
		final String content,
		final Boolean isAnonymous
	) {
		return Social.builder()
			.user(user)
			.title(title)
			.routine(routine)
			.content(content)
			.isAnonymous(isAnonymous)
			.build();
	}

	public static SocialCreateResponse toSocialCreateResponse(Social socialBoard) {
		return SocialCreateResponse.builder()
			.socialId(socialBoard.getId())
			.build();
	}

	public static SocialDetailResponse toSocialDetailResponse(
		Social socialBoard,
		List<SocialImageFile> imageFiles,
		List<CommentDto> comments,
		boolean isSocialBoardLiked
	) {
		return SocialDetailResponse.builder()
			.socialId(socialBoard.getId())
			.owner(getOwner(socialBoard.getUser()))
			.routine(getSocialRoutineDto(socialBoard.getRoutine()))
			.title(socialBoard.getTitle())
			.content(socialBoard.getContent())
			.hasImages(!imageFiles.isEmpty())
			.images(getImageDtos(imageFiles))
			.socialLikeInfo(getSocialLikeDto(socialBoard, isSocialBoardLiked))
			.comments(comments)
			.createdAt(socialBoard.getCreatedAt())
			.build();

	}

	public static SocialResponse toSocialResponse(
		Social socialBoard,
		List<SocialImageFile> imageFiles,
		int commentCount,
		boolean isLiked
	) {
		return SocialResponse.builder()
			.socialId(socialBoard.getId())
			.owner(getOwner(socialBoard.getUser()))
			.routine(getSocialRoutineDto(socialBoard.getRoutine()))
			.title(socialBoard.getTitle())
			.content(socialBoard.getContent())
			.hasImages(!imageFiles.isEmpty())
			.images(getImageDtos(imageFiles))
			.socialLikeInfo(getSocialLikeDto(socialBoard, isLiked))
			.commentCount(commentCount)
			.createdAt(socialBoard.getCreatedAt())
			.build();
	}

	private static SocialLikeDto getSocialLikeDto(Social socialBoard, boolean isLiked) {
		return SocialLikeMapper.toSocialLikeDto(socialBoard, isLiked);
	}

	private static RoutineDetailResponse getSocialRoutineDto(final Routine routine) {
		if (routine == null) {
			return null;
		}
		return RoutineMapper.toRoutineDetailResponse(routine);
	}

	private static List<SocialImageDto> getImageDtos(List<SocialImageFile> imageFiles) {
		return imageFiles.stream()
			.map(SocialImageFileMapper::toSocialImageDto)
			.toList();
	}

	private static OwnerDto getOwner(final User user) {
		return OwnerDto.builder()
			.ownerId(user.getId())
			.nickname(user.getNickname())
			.profileImageUrl(user.getImageUrl())
			.build();
	}
}
