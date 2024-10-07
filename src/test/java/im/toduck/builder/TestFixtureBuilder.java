package im.toduck.builder;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import im.toduck.domain.routine.persistence.entity.Routine;
import im.toduck.domain.routine.persistence.entity.RoutineRecord;
import im.toduck.domain.social.persistence.entity.Comment;
import im.toduck.domain.social.persistence.entity.Like;
import im.toduck.domain.social.persistence.entity.Social;
import im.toduck.domain.social.persistence.entity.SocialCategory;
import im.toduck.domain.social.persistence.entity.SocialImageFile;
import im.toduck.domain.user.persistence.entity.Block;
import im.toduck.domain.user.persistence.entity.User;
import im.toduck.infra.redis.phonenumber.PhoneNumber;

@Component
public class TestFixtureBuilder {

	@Autowired
	private BuilderSupporter bs;

	public User buildUser(final User user) {
		return bs.userRepository().save(user);
	}

	public PhoneNumber buildPhoneNumber(final PhoneNumber phoneNumber) {
		return bs.phoneNumberRepository().save(phoneNumber);
	}

	public Routine buildRoutine(final Routine routine) {
		return bs.routineRepository().save(routine);
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

}
