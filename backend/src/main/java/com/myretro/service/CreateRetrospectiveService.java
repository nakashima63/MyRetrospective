package com.myretro.service;

import com.myretro.entity.Retrospective;
import com.myretro.entity.User;
import com.myretro.repository.RetrospectiveRepository;
import com.myretro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 振り返りセッションを新規作成するユースケース。
 */
@Service
@RequiredArgsConstructor
public class CreateRetrospectiveService {

    private final RetrospectiveRepository retrospectiveRepository;
    private final UserRepository userRepository;

    /**
     * 新しい振り返りセッションを作成する。
     *
     * @param userId      作成するユーザーの ID
     * @param title       タイトル
     * @param description 説明（任意）
     * @return 作成された振り返りエンティティ
     * @throws IllegalArgumentException 指定されたユーザーが存在しない場合
     */
    @Transactional
    public Retrospective create(Long userId, String title, String description) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Retrospective retrospective = new Retrospective(user, title, description);
        return retrospectiveRepository.save(retrospective);
    }
}
