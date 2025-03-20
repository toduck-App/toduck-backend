package im.toduck.builder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import im.toduck.domain.diary.persistence.repository.DiaryImageRepository;
import im.toduck.domain.diary.persistence.repository.DiaryRepository;
import im.toduck.domain.routine.persistence.repository.RoutineRecordRepository;
import im.toduck.domain.routine.persistence.repository.RoutineRepository;
import im.toduck.domain.schedule.persistence.repository.ScheduleRecordRepository;
import im.toduck.domain.schedule.persistence.repository.ScheduleRepository;
import im.toduck.domain.social.persistence.repository.CommentLikeRepository;
import im.toduck.domain.social.persistence.repository.CommentRepository;
import im.toduck.domain.social.persistence.repository.LikeRepository;
import im.toduck.domain.social.persistence.repository.SocialCategoryLinkRepository;
import im.toduck.domain.social.persistence.repository.SocialCategoryRepository;
import im.toduck.domain.social.persistence.repository.SocialImageFileRepository;
import im.toduck.domain.social.persistence.repository.SocialRepository;
import im.toduck.domain.user.persistence.repository.BlockRepository;
import im.toduck.domain.user.persistence.repository.UserRepository;
import im.toduck.infra.redis.phonenumber.PhoneNumberRepository;

@Component
public class BuilderSupporter {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PhoneNumberRepository phoneNumberRepository;

	@Autowired
	private SocialRepository socialRepository;

	@Autowired
	private SocialCategoryRepository socialCategoryRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private LikeRepository likeRepository;

	@Autowired
	private SocialImageFileRepository socialImageFileRepository;

	@Autowired
	private RoutineRepository routineRepository;

	@Autowired
	private RoutineRecordRepository routineRecordRepository;

	@Autowired
	private BlockRepository blockRepository;

	@Autowired
	private CommentLikeRepository commentLikeRepository;

	@Autowired
	private SocialCategoryLinkRepository socialCategoryLinkRepository;

	@Autowired
	private ScheduleRepository scheduleRepository;

	@Autowired
	private ScheduleRecordRepository scheduleRecordRepository;

	@Autowired
	private DiaryRepository diaryRepository;

	@Autowired
	private DiaryImageRepository diaryImageRepository;

	public UserRepository userRepository() {
		return userRepository;
	}

	public PhoneNumberRepository phoneNumberRepository() {
		return phoneNumberRepository;
	}

	public SocialRepository socialRepository() {
		return socialRepository;
	}

	public SocialCategoryRepository socialCategoryRepository() {
		return socialCategoryRepository;
	}

	public CommentRepository commentRepository() {
		return commentRepository;
	}

	public LikeRepository likeRepository() {
		return likeRepository;
	}

	public SocialImageFileRepository socialImageFileRepository() {
		return socialImageFileRepository;
	}

	public RoutineRepository routineRepository() {
		return routineRepository;
	}

	public RoutineRecordRepository routineRecord() {
		return routineRecordRepository;
	}

	public BlockRepository blockRepository() {
		return blockRepository;
	}

	public CommentLikeRepository commentLikeRepository() {
		return commentLikeRepository;
	}

	public SocialCategoryLinkRepository socialCategoryLinkRepository() {
		return socialCategoryLinkRepository;
	}

	public ScheduleRepository scheduleRepository() {
		return scheduleRepository;
	}

	public ScheduleRecordRepository scheduleRecordRepository() {
		return scheduleRecordRepository;
	}

	public DiaryRepository diaryRepository() {
		return diaryRepository;
	}

	public DiaryImageRepository diaryImageRepository() {
		return diaryImageRepository;
	}
}
