package im.toduck.domain.social.common.mapper;

import java.util.List;
import java.util.Map;

import im.toduck.domain.routine.common.mapper.RoutineMapper;
import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.presentation.dto.response.RoutineDetailResponse;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialImageFile;
import im.toduck.domain.social.presentation.dto.response.CommentDto;
import im.toduck.domain.social.presentation.dto.response.OwnerDto;
import im.toduck.domain.social.presentation.dto.response.SocialCategoryResponse.SocialCategoryDto;
import im.toduck.domain.social.presentation.dto.response.SocialCreateResponse;
import im.toduck.domain.social.presentation.dto.response.SocialDetailResponse;
import im.toduck.domain.social.presentation.dto.response.SocialImageDto;
import im.toduck.domain.social.presentation.dto.response.SocialLikeDto;
import im.toduck.domain.social.presentation.dto.response.SocialResponse;
import im.toduck.domain.social.presentation.dto.response.SocialWithDetailsDto;
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
		List<SocialCategoryDto> socialCategoryDtos,
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
			.categories(socialCategoryDtos)
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

	public static SocialWithDetailsDto toSocialWithDetailsDto(
		Social social,
		List<SocialImageFile> imageFiles,
		Integer commentCount,
		Boolean isLikedByCurrentUser,
		List<SocialCategoryDto> categories
	) {
		return SocialWithDetailsDto.builder()
			.social(social)
			.imageFiles(imageFiles != null ? imageFiles : List.of())
			.commentCount(commentCount != null ? commentCount : 0)
			.isLikedByCurrentUser(isLikedByCurrentUser != null ? isLikedByCurrentUser : false)
			.categories(categories != null ? categories : List.of())
			.build();
	}

	public static List<SocialWithDetailsDto> toSocialWithDetailsDtoList(
		List<Social> socials,
		Map<Long, List<SocialImageFile>> imageFilesMap,
		Map<Long, Integer> commentCountsMap,
		Map<Long, Boolean> likesMap,
		Map<Long, List<SocialCategoryDto>> categoriesMap
	) {
		return socials.stream()
			.map(social -> toSocialWithDetailsDto(
				social,
				imageFilesMap.getOrDefault(social.getId(), List.of()),
				commentCountsMap.getOrDefault(social.getId(), 0),
				likesMap.getOrDefault(social.getId(), false),
				categoriesMap.getOrDefault(social.getId(), List.of())
			))
			.toList();
	}
}
