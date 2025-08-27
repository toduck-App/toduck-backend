package im.toduck.domain.diary.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;

import im.toduck.domain.diary.persistence.entity.MasterKeyword;
import im.toduck.domain.diary.persistence.repository.MasterKeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MasterKeywordService {
	private final MasterKeywordRepository masterKeywordRepository;

	public List<MasterKeyword> findAll() {
		return masterKeywordRepository.findAll();
	}
}
