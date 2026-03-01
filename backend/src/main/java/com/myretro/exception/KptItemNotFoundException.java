package com.myretro.exception;

/**
 * 指定された KPT アイテムが見つからない場合にスローされる例外。
 */
public class KptItemNotFoundException extends RuntimeException {

    /**
     * @param id 見つからなかった KPT アイテムの ID
     */
    public KptItemNotFoundException(Long id) {
        super("KptItem not found: " + id);
    }
}
