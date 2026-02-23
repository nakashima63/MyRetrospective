package com.myretro.exception;

/**
 * 指定された振り返りが見つからない場合にスローされる例外。
 * 存在しない ID や、他ユーザーの振り返りへのアクセス時に使用する。
 */
public class RetrospectiveNotFoundException extends RuntimeException {

    /**
     * @param id 見つからなかった振り返りの ID
     */
    public RetrospectiveNotFoundException(Long id) {
        super("Retrospective not found: " + id);
    }
}
