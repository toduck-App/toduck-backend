package im.toduck.domain.diary.domain.service;

import org.springframework.stereotype.Service;

import im.toduck.domain.diary.persistence.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryService {
	private final DiaryRepository diaryRepository;

}
