package com.me.android.noteeditor.Recyclerview_adapter.itemTouchHelper;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;

import com.me.android.noteeditor.BlurUtility.ApplyBlurOnDialog;
import com.me.android.noteeditor.CommonUtility.utilityClass;
import com.me.android.noteeditor.R;
import com.me.android.noteeditor.Recyclerview_adapter.contentClass_adapter;
import com.me.android.noteeditor.contract.DataBaseManager;
import com.me.android.noteeditor.contract.content_class;
import com.me.android.noteeditor.customListener.EmptyData_listener;
import com.me.android.noteeditor.customListener.FolderCountChangeListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleItemTouchHelper extends ItemTouchHelper.Callback {

    private contentClass_adapter mFeedbackAdapter;
    private View container;
    private DataBaseManager dataBaseManager;
    private ApplyBlurOnDialog apr;
    private static boolean shouldItemBeDeleted = true;
    private final utilityClass utilityClass = new utilityClass();

    public SimpleItemTouchHelper(contentClass_adapter itemTouchFeedback, View view, Activity activity){
        mFeedbackAdapter = itemTouchFeedback;
        container = view;
        dataBaseManager = new DataBaseManager(view.getContext());
        apr = new ApplyBlurOnDialog(activity);
    }

    public void removeOnSwipe(content_class item, int position){
        mFeedbackAdapter.remove(position);
    }

    public static void removeOnSwipe(content_class item, int position, itemTouchFeedback itemTouchFeedback){
        itemTouchFeedback.remove(position);
    }

    public void restore(content_class dataItem, int position){
        mFeedbackAdapter.restore(dataItem, position);
    }

    public static void restore(content_class dataItem, int position, itemTouchFeedback itemTouchFeedback){
        itemTouchFeedback.restore(dataItem, position);
    }

    /**
     * Should return a composite flag which defines the enabled move directions in each state
     * (idle, swiping, dragging).
     * <p>
     * Instead of composing this flag manually, you can use {@link #makeMovementFlags(int, * int)}
     * or {@link #makeFlag(int, int)}.
     * <p>
     * This flag is composed of 3 sets of 8 bits, where first 8 bits are for IDLE state, next
     * 8 bits are for SWIPE state and third 8 bits are for DRAG state.
     * Each 8 bit sections can be constructed by simply OR'ing direction flags defined in
     * {@link ItemTouchHelper}.
     * <p>
     * For example, if you want it to allow swiping LEFT and RIGHT but only allow starting to
     * swipe by swiping RIGHT, you can return:
     * <pre>
     *      makeFlag(ACTION_STATE_IDLE, RIGHT) | makeFlag(ACTION_STATE_SWIPE, LEFT | RIGHT);
     * </pre>
     * This means, allow right movement while IDLE and allow right and left movement while
     * swiping.
     *
     * @param recyclerView The RecyclerView to which ItemTouchHelper is attached.
     * @param viewHolder   The ViewHolder for which the movement information is necessary.
     * @return flags specifying which movements are allowed on this ViewHolder.
     * @see #makeMovementFlags(int, int)
     * @see #makeFlag(int, int)
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.START | ItemTouchHelper.END);
    }

    /**
     * Called when ItemTouchHelper wants to move the dragged item from its old position to
     * the new position.
     * <p>
     * If this method returns true, ItemTouchHelper assumes {@code viewHolder} has been moved
     * to the adapter position of {@code target} ViewHolder
     * ({@link ViewHolder#getAdapterPosition()
     * ViewHolder#getAdapterPosition()}).
     * <p>
     * If you don't support drag & drop, this method will never be called.
     *
     * @param recyclerView The RecyclerView to which ItemTouchHelper is attached to.
     * @param viewHolder   The ViewHolder which is being dragged by the user.
     * @param target       The ViewHolder over which the currently active item is being
     *                     dragged.
     * @return True if the {@code viewHolder} has been moved to the adapter position of
     * {@code target}.
     * @see #onMoved(RecyclerView, ViewHolder, int, ViewHolder, int, int, int)
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    /**
     * Called when a ViewHolder is swiped by the user.
     * <p>
     * If you are returning relative directions ({@link #START} , {@link #END}) from the
     * {@link #getMovementFlags(RecyclerView, ViewHolder)} method, this method
     * will also use relative directions. Otherwise, it will use absolute directions.
     * <p>
     * If you don't support swiping, this method will never be called.
     * <p>
     * ItemTouchHelper will keep a reference to the View until it is detached from
     * RecyclerView.
     * As soon as it is detached, ItemTouchHelper will call
     * {@link #clearView(RecyclerView, ViewHolder)}.
     *
     * @param viewHolder The ViewHolder which has been swiped by the user.
     * @param direction  The direction to which the ViewHolder is swiped. It is one of
     *                   {@link #UP}, {@link #DOWN},
     *                   {@link #LEFT} or {@link #RIGHT}. If your
     *                   {@link #getMovementFlags(RecyclerView, ViewHolder)}
     *                   method
     *                   returned relative flags instead of {@link #LEFT} / {@link #RIGHT};
     *                   `direction` will be relative as well. ({@link #START} or {@link
     *                   #END}).
     */
    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
        final int pos = viewHolder.getAdapterPosition();
        final content_class item = mFeedbackAdapter.getData().get(pos);
        mFeedbackAdapter.remove(pos);


        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(container.getContext());
        alertDialog.setMessage(container.getContext().getString(R.string.confirm_delete_message));
        alertDialog.setPositiveButton(container.getContext().getString(R.string.confirm_delete), null);
        alertDialog.setNegativeButton(container.getContext().getString(R.string.confirm_cancel), null);

        final AlertDialog deleteDialog = utilityClass.DialogBlur(container.getContext(), alertDialog);

        deleteDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mFeedbackAdapter.restore(item, pos);
                utilityClass.cancelDialog();
            }
        });

        CharSequence charSequence = "Note deleted";
        final Snackbar snackbar = Snackbar.make(container, charSequence, Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFeedbackAdapter.restore(item, pos);
                shouldItemBeDeleted = false;
                EmptyData_listener.confirmDataEmpty(false);
                try {
                    FolderCountChangeListener.confirmFolderCountChangeListner(true);
                }catch (NullPointerException e){
                    //empty
                }
            }
        });
        snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if (shouldItemBeDeleted) {
                    int id = item.getId();
                    dataBaseManager.deleteEntry(id);
                    if (mFeedbackAdapter.getData().isEmpty() || mFeedbackAdapter.getData() == null)
                        EmptyData_listener.confirmDataEmpty(true);



                }
                shouldItemBeDeleted = true;
            }

            @Override
            public void onShown(Snackbar transientBottomBar) {
                if (mFeedbackAdapter.getData().isEmpty() || mFeedbackAdapter.getData() == null)
                    EmptyData_listener.confirmDataEmpty(true);
            }
        });

        deleteDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utilityClass.cancelDialog();
                snackbar.show();
                try {
                    FolderCountChangeListener.confirmFolderCountChangeListner(true);
                }catch (NullPointerException e){
                    //empty
                }
            }
        });
        deleteDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFeedbackAdapter.restore(item, pos);
                utilityClass.cancelDialog();
            }
        });
    }

    /**
     * Returns whether ItemTouchHelper should start a drag and drop operation if an item is
     * long pressed.
     * <p>
     * Default value returns true but you may want to disable this if you want to start
     * dragging on a custom view touch using {@link #startDrag(ViewHolder)}.
     *
     *
     *
     * @return True if ItemTouchHelper should start dragging an item when it is long pressed,
     * false otherwise. Default value is <code>true</code>.
     * @see #startDrag(ViewHolder)
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    /**
     * Returns whether ItemTouchHelper should start a swipe operation if a pointer is swiped
     * over the View.
     * <p>
     * Default value returns true but you may want to disable this if you want to start
     * swiping on a custom view touch using {@link #startSwipe(ViewHolder)}.
     *
     * @return True if ItemTouchHelper should start swiping an item when user swipes a pointer
     * over the View, false otherwise. Default value is <code>true</code>.
     * @see #startSwipe(ViewHolder)
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }
}
