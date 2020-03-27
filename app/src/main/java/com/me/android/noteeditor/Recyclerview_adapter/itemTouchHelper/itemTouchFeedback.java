package com.me.android.noteeditor.Recyclerview_adapter.itemTouchHelper;

import com.me.android.noteeditor.contract.content_class;

public interface itemTouchFeedback {

    void remove(int position);

    void restore(content_class data, int position);

    void replace(content_class data, int position);

}
