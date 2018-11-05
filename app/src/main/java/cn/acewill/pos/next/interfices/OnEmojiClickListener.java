package cn.acewill.pos.next.interfices;

import cn.acewill.pos.next.model.dish.DishType;

/**
 * Created by DHH on 2016/12/23.
 */

public interface OnEmojiClickListener {
    void onEmojiDelete();

    void onEmojiClick(DishType dishType);
}
