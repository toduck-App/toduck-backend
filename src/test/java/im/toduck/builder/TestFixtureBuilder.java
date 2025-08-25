package im.toduck.builder;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.util.ReflectionTestUtils;

import im.toduck.domain.diary.persistence.entity.KeywordCategory;
import im.toduck.domain.diary.persistence.entity.MasterKeyword;
import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.entity.RoutineRecord;
import im.toduck.domain.schedule.persistence.entity.Schedule;
import im.toduck.domain.schedule.persistence.entity.ScheduleRecord;
import im.toduck.domain.social.persistence.entity.Comment;
import im.toduck.domain.social.persistence.entity.CommentLike;
import im.toduck.domain.social.persistence.entity.Like;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialCategory;
import im.toduck.domain.social.persistence.entity.SocialCategoryLink;
import im.toduck.domain.social.persistence.entity.SocialImageFile;
import im.toduck.domain.user.persistence.entity.Block;
import im.toduck.domain.user.persistence.entity.Follow;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.fixtures.routine.RoutineWithAuditInfo;
import im.toduck.infra.redis.phonenumber.PhoneNumber;

@Component
public class TestFixtureBuilder {

	@Autowired
	private BuilderSupporter bs;

	public User buildUser(final User user) {
		return bs.userRepository().save(user);
	}

	public User buildDeletedUser(final User user, final LocalDateTime deletedAt) {
		User saved = bs.userRepository().save(user);
		ReflectionTestUtils.setField(saved, "deletedAt", deletedAt);
		return bs.userRepository().save(saved);
	}

	public PhoneNumber buildPhoneNumber(final PhoneNumber phoneNumber) {
		return bs.phoneNumberRepository().save(phoneNumber);
	}

	public Routine buildRoutine(final Routine routine) {
		return bs.routineRepository().save(routine);
	}

	public Routine buildRoutineAndUpdateAuditFields(final RoutineWithAuditInfo routineWithAuditInfo) {
		Routine savedRoutine = bs.routineRepository().save(routineWithAuditInfo.getRoutine());

		if (routineWithAuditInfo.requiresAudit()) {
			ReflectionTestUtils.setField(savedRoutine, "createdAt", routineWithAuditInfo.getCreatedAt());
			ReflectionTestUtils.setField(savedRoutine, "scheduleModifiedAt",
				routineWithAuditInfo.getScheduleModifiedAt());
			ReflectionTestUtils.setField(savedRoutine, "deletedAt", routineWithAuditInfo.getDeletedAt());
		}

		return bs.routineRepository()
			.findById(savedRoutine.getId())
			.orElseThrow(() -> new RuntimeException("루틴을 찾을 수 없음"));
	}

	public RoutineRecord buildRoutineRecord(final RoutineRecord routineRecord) {
		return bs.routineRecord().save(routineRecord);
	}

	public Social buildSocial(final Social social) {
		return bs.socialRepository().save(social);
	}

	public List<Social> buildSocials(final List<Social> socials) {
		return bs.socialRepository().saveAll(socials);
	}

	public List<SocialCategory> buildCategories(final List<SocialCategory> categories) {
		return bs.socialCategoryRepository().saveAll(categories);
	}

	public Comment buildComment(final Comment comment) {
		return bs.commentRepository().save(comment);
	}

	public Like buildLike(final Like like) {
		return bs.likeRepository().save(like);
	}

	public List<SocialImageFile> buildSocialImageFiles(final List<SocialImageFile> socialImageFiles) {
		return bs.socialImageFileRepository().saveAll(socialImageFiles);
	}

	public Block buildBlock(final Block block) {
		return bs.blockRepository().save(block);
	}

	public CommentLike buildCommentLike(final CommentLike commentLike) {
		return bs.commentLikeRepository().save(commentLike);
	}

	public void buildSocialCategoryLinks(final SocialCategory socialCategory, final Social social) {
		SocialCategoryLink link = SocialCategoryLink.builder()
			.social(social)
			.socialCategory(socialCategory)
			.build();

		bs.socialCategoryLinkRepository().save(link);
	}

	public Schedule buildSchedule(final Schedule schedule) {
		return bs.scheduleRepository().save(schedule);
	}

	public ScheduleRecord buildScheduleRecord(final ScheduleRecord scheduleRecord) {
		return bs.scheduleRecordRepository().save(scheduleRecord);
	}

	public Follow buildFollow(final User follower, final User followed) {
		Follow follow = Follow.builder()
			.follower(follower)
			.followed(followed)
			.build();
		return bs.followRepository().save(follow);
	}

	public MasterKeyword buildMasterKeyword(KeywordCategory keywordCategory, String keyword) {
		MasterKeyword masterKeyword = MasterKeyword.builder()
			.category(keywordCategory)
			.keyword(keyword)
			.build();
		return bs.masterKeywordRepository().save(masterKeyword);
	}
}
