package im.toduck.builder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import im.toduck.domain.social.persistence.repository.CommentRepository;
import im.toduck.domain.social.persistence.repository.SocialCategoryRepository;
import im.toduck.domain.social.persistence.repository.SocialRepository;
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

}
