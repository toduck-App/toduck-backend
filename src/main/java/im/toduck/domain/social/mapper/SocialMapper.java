package im.toduck.domain.social.mapper;

import java.util.List;

import im.toduck.domain.social.persistence.entity.Comment;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialCategory;
import im.toduck.domain.social.persistence.entity.SocialImageFile;
import im.toduck.domain.social.presentation.dto.response.CommentDto;
import im.toduck.domain.social.presentation.dto.response.LikeDto;
import im.toduck.domain.social.presentation.dto.response.OwnerDto;
import im.toduck.domain.social.presentation.dto.response.SocialCategoryDto;
import im.toduck.domain.social.presentation.dto.response.SocialCreateResponse;
import im.toduck.domain.social.presentation.dto.response.SocialDetailResponse;
import im.toduck.domain.social.presentation.dto.response.SocialImageDto;
import im.toduck.domain.social.presentation.dto.response.SocialResponse;
import im.toduck.domain.user.persistence.entity.User;

public class SocialMapper {
	public static Social toSocial(User user, String content, Boolean isAnonymous) {
		return Social.builder()
			.user(user)
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
		List<Comment> comments,
		boolean isLiked) {
		return SocialDetailResponse.builder()
			.id(socialBoard.getId())
			.owner(getOwner(socialBoard.getUser()))
			.hasImages(!imageFiles.isEmpty())
			.images(getImageDtos(imageFiles))
			.content(socialBoard.getContent())
			.likeInfo(getLikeDto(socialBoard, isLiked))
			.comments(getCommentDtos(comments))
			.createdAt(socialBoard.getCreatedAt())
			.build();

	}

	public static SocialResponse toSocialResponse(
		Social socialBoard,
		List<SocialImageFile> imageFiles,
		int commentCount,
		boolean isLiked) {

		return SocialResponse.builder()
			.id(socialBoard.getId())
			.owner(getOwner(socialBoard.getUser()))
			.content(socialBoard.getContent())
			.hasImages(!imageFiles.isEmpty())
			.images(getImageDtos(imageFiles))
			.likeInfo(getLikeDto(socialBoard, isLiked))
			.commentCount(commentCount)
			.createdAt(socialBoard.getCreatedAt())
			.build();
	}

	private static LikeDto getLikeDto(Social socialBoard, boolean isLiked) {
		return LikeMapper.toLikeDto(socialBoard, isLiked);
	}

	private static List<SocialImageDto> getImageDtos(List<SocialImageFile> imageFiles) {
		return imageFiles.stream()
			.map(SocialImageFileMapper::toSocialImageDto)
			.toList();
	}

	private static List<CommentDto> getCommentDtos(List<Comment> comments) {
		return comments.stream()
			.map(CommentMapper::toCommentDto)
			.toList();
	}

	private static List<SocialCategoryDto> getCategoryDtos(List<SocialCategory> categories) {
		return categories.stream()
			.map(SocialCategoryMapper::toSocialCategoryDto)
			.toList();
	}

	private static OwnerDto getOwner(User user) {
		return OwnerDto.builder()
			.id(user.getId())
			.nickname(user.getNickname())
			.build();
	}

}
