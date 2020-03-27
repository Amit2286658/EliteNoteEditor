/*
package library.texteditor.amitkumar.searchview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import static library.texteditor.amitkumar.searchview.utilityClass.fixDimension;

*/
/**
 * Created by Amit on 20/11/2019.
 *//*


public class searchViewUtility {

    private ViewGroup SearchView_viewGroup;
    private LinearLayout view;
    private Context context;
    private static boolean isSearchViewAdded = false;
    private static float x, y;
    private static final int REVEAL_IN = 1, REVEAL_OUT = 2, SLIDE_IN = 3, SLIDE_OUT = 4, SLIDE_UP = 5, SLIDE_DOWN = 6;
    private static int durationCount = 0;
    private static boolean iSafeToCall = false;
    private static boolean isItemAnOverFlowMenuItem = false;
    private static int default_material_color;
    private static int default_status_bar_color;

    */
/*
    * keeping a reminder :)
    * <b>warning</b> : use it only if you are planning to use the circular reveal animation,
    * which for some reason does not appear to work, at least for now,
    * but further attempts will be made regardless,
    * this comment should be updated, when the animation begins to work as expected
    *
    * EDIT : the animation is now working as expected, the only issue is the color issue now,
    * and that should not be a big problem to tackle.
    *//*

    @SuppressLint("ClickableViewAccessibility")
    public void InitializeTouchListener(final Context context){
        ViewGroup decorView = (ViewGroup)((Activity)context).getWindow().getDecorView();
        ImageView touchListener = new ImageView(context){
            @Override
            public boolean performClick() {
                return super.performClick();
            }
        };
        fixDimension(context, touchListener, 0);
        decorView.addView(touchListener);
        touchListener.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                x = event.getX();
                y = event.getY();
                return false;
            }
        });

    }

    public void init(Context context, boolean isItOverFlow){
        //set the listener first,
        this.context = context;

        isItemAnOverFlowMenuItem = isItOverFlow;

        if (!isItemAnOverFlowMenuItem) {
            durationCount = 300;
            InitializeTouchListener(context);
        }
    }

    public void show(int color){
        default_material_color = color;
        setCustomSearchViewOnActivity(context, isItemAnOverFlowMenuItem);
        registerCancelListener(context, isItemAnOverFlowMenuItem);
        setEditorKeyListener(getEdittext());
        setOnSearchIconClicked(getEdittext());
        setEditTextClearButtonListener();
    }

    public void show(int color, boolean isItOverFlow){
        default_material_color = color;
        isItemAnOverFlowMenuItem = isItOverFlow;
        setCustomSearchViewOnActivity(context, isItemAnOverFlowMenuItem);
        registerCancelListener(context, isItemAnOverFlowMenuItem);
        setEditorKeyListener(getEdittext());
        setOnSearchIconClicked(getEdittext());
        setEditTextClearButtonListener();
    }

    public void setCustomSearchViewOnActivity(final Context context, boolean isItOverFlow){

        SearchView_viewGroup = (ViewGroup) ((Activity) context).getWindow().getDecorView();
        view = (LinearLayout) ((Activity)context).getLayoutInflater()
                .inflate(R.layout.custom_search_view_container, null);
        //make it safe to call methods here
        iSafeToCall = true;

        view.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        */
/*default_material_color = getTopMostMaterialCard().getCardBackgroundColor().getDefaultColor();*//*


        fixDimension(context, view, 0);

        SearchView_viewGroup.addView(view);
        isSearchViewAdded = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWholeSearchView().setElevation(getDipValue(context, 6));
        }

        if (!isItOverFlow){
            createCircularRevealAnimation(context, REVEAL_IN);
        }else {
            createSlidingAnimation(context, SLIDE_IN);
        }

        getEdittext().requestFocus();
    }

    public void cancelSearchView(final Context context, boolean isItOverFlow){
        Util.hideKeyboard(view, context);
        if (isSearchViewAdded) {
            if (!isItOverFlow) {
                createCircularRevealAnimation(context, REVEAL_OUT);
            }
            else {
                createSlidingAnimation(context, SLIDE_OUT);
            }
        }
    }

    @SuppressLint("newApi")
    private void createCircularRevealAnimation(final Context context, final int argument) throws IllegalStateException
            , NullPointerException{
        if (iSafeToCall) {
            if (argument == REVEAL_IN) {
                getWholeSearchView().setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
                getSuggestionLayoutContainer().setVisibility(View.INVISIBLE);

                */
/*default_status_bar_color = ((Activity)context).getWindow().getStatusBarColor();
                getTopMostStatusBar().setBackgroundColor(Cyanea.getInstance().getBackgroundColor());
                getTopMostStatusBar().setVisibility(View.VISIBLE);
                getTopMostStatusBar().setMinimumHeight(getStatusBarHeight(context));
                getTopMostStatusBar().setH
                getTopMostStatusBar().setAlpha(1.0f);*//*


                float r = (float) Math.hypot(x, y);
                Animator slideAndFade_in = ViewAnimationUtils
                        .createCircularReveal(getSearchViewWindow(), (int) x, (int) y, 0f, r);
                slideAndFade_in.setDuration(durationCount);
                slideAndFade_in.setInterpolator(new AccelerateDecelerateInterpolator());
                slideAndFade_in.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        getSuggestionLayoutContainer().setBackgroundColor(default_material_color);
                        */
/*getWholeSearchView().setBackgroundColor(default_material_color);*//*

                        Animation slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down_bounce);
                        getSuggestionLayoutContainer().startAnimation(slideDown);
                        slideDown.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
//                                ((Activity)context).getWindow().setStatusBarColor(default_material_color);
                                getSuggestionLayoutContainer().setVisibility(View.VISIBLE);
                                getWholeSearchView().setBackgroundColor(default_material_color);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        getSearchViewWindow().setBackgroundColor(default_material_color);
                    }
                });
                slideAndFade_in.start();
            }
            if (argument == REVEAL_OUT){

                getWholeSearchView().setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));

                Animation slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up);

                getSuggestionLayoutContainer().startAnimation(slideUp);

                slideUp.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        getSuggestionLayoutContainer().setVisibility(View.GONE);
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        float r = (float) Math.hypot(x, y);
                        Animator slideAndFade_out = ViewAnimationUtils
                                .createCircularReveal(getSearchViewWindow(), (int) x, (int) y, r, 0f);
                        slideAndFade_out.setDuration(durationCount);
                        slideAndFade_out.setInterpolator(new AccelerateDecelerateInterpolator());
                        slideAndFade_out.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                getSearchViewWindow().setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
                                getSearchViewWindow().removeAllViews();
                                SearchView_viewGroup.removeView(view);
                            }

                            @Override
                            public void onAnimationStart(Animator animation) {
                                //             ((Activity)context).getWindow().setStatusBarColor(default_status_bar_color);
                            }
                        });
                        slideAndFade_out.start();

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        }else throw new  IllegalStateException("Please inflate and add the view to the decor view first");
    }

    private void createSlidingAnimation(final Context context, int argument) throws IllegalStateException, NullPointerException{
        if (iSafeToCall){
            if (argument == SLIDE_IN){
                getWholeSearchView().setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
                getSuggestionLayoutContainer().setVisibility(View.INVISIBLE);

                Animation slideAndFade_in = AnimationUtils.loadAnimation(context, R.anim.slide_and_alpha_in_set);

                getSearchViewWindow().startAnimation(slideAndFade_in);

                slideAndFade_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        getSearchViewWindow().setBackgroundColor(default_material_color);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        getSuggestionLayoutContainer().setBackgroundColor(default_material_color);
                        */
/*getWholeSearchView().setBackgroundColor(default_material_color);*//*

                        Animation slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down_bounce);
                        getSuggestionLayoutContainer().startAnimation(slideDown);
                        slideDown.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                getSuggestionLayoutContainer().setVisibility(View.VISIBLE);
                                getWholeSearchView().setBackgroundColor(default_material_color);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
            if (argument == SLIDE_OUT){

                getWholeSearchView().setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));

                Animation slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up);

                getSuggestionLayoutContainer().startAnimation(slideUp);

                slideUp.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        getSuggestionLayoutContainer().setVisibility(View.GONE);

                        Animation slideAndFade_out = AnimationUtils.loadAnimation(context, R.anim.slide_and_alpha_out_set);
                        getSearchViewWindow().startAnimation(slideAndFade_out);
                        slideAndFade_out.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                getSearchViewWindow().setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
                                getSearchViewWindow().removeAllViews();
                                SearchView_viewGroup.removeView(view);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });



                */
/*getWholeSearchView().setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));*//*



            }
        }else throw new IllegalStateException("Please inflate and add the view to the decor view first");
    }

    */
/*private void creatingSlideUpAndDownAnimation(Context context, int argument){

        if (argument == SLIDE_DOWN){

            Animation slideBounceDown_animaion = AnimationUtils.loadAnimation(context, R.anim.slide_down_bounce);

            getSuggestionLayoutContainer().startAnimation(slideBounceDown_animaion);

            slideBounceDown_animaion.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    getSuggestionLayoutContainer().setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        }
        if (argument == SLIDE_UP){
            getWholeSearchView().setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));

            Animation slideUp_animation = AnimationUtils.loadAnimation(context, R.anim.slide_up);

            getSuggestionLayoutContainer().startAnimation(slideUp_animation);

            slideUp_animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    getSuggestionLayoutContainer().setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });


        }

    }*//*


    */
/*private suggestion_adapter setSuggestionAdapter(Context context){
        searchQueryDatabaseManager sqdbManager = new searchQueryDatabaseManager(context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);

        RecyclerView recyclerView = getRecyclerView();
        recyclerView.setLayoutManager(linearLayoutManager);
        suggestion_adapter suggestion_adapter = new suggestion_adapter(context, sqdbManager.getAll(), getEdittext());
        recyclerView.setAdapter(suggestion_adapter);
        return suggestion_adapter;
    }*//*


    public void registerCancelListener(final Context context, final boolean isItOverFlow){
        getWholeSearchViewAlongWithContainer().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelSearchView(context, isItOverFlow);
            }
        });

        getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelSearchView(context, isItOverFlow);
            }
        });
        getSearchViewWindow().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing
            }
        });
        getWholeSearchView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing
            }
        });
    }

    private void setEditorKeyListener(final EditText editText){
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    suggestionUtility.setSuggestionHitoryData(context, editText.getText().toString());
                    cancelSearchView(context, false);
                    launchFragmentContainer(editText.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    private void launchFragmentContainer(String title){
        Intent intent = new Intent();
        intent.putExtra(context.getString(R.string.from_folder_to_fragment_container_intent_key), title);
        intent.putExtra(context.getString(R.string.from_folder_to_fragment_container_boolean_argument), true);
        intent.setClass(context, fragment_container.class);
        context.startActivity(intent);
    }

    */
/*interface related methods here *//*


    private SearchViewTextChangeListener mSearchViewTextChangeListener;

    public void setSearchViewTextChangeListener(SearchViewTextChangeListener searchViewTextChangeListener){
        mSearchViewTextChangeListener = searchViewTextChangeListener;
    }

    private void confirmTextIsChanged(Editable editable){
        mSearchViewTextChangeListener.textChangeListener(editable);
    }

    public interface SearchViewTextChangeListener{
        void textChangeListener(Editable editable);
    }

    */
/*End of interface related methods here*//*


    private void setEditTextClearButtonListener(){

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                confirmTextIsChanged(s);
                if (TextUtils.isEmpty(s)){
                    getEdittextClearButton().setVisibility(View.GONE);
                }else getEdittextClearButton().setVisibility(View.VISIBLE);
            }
        };
        getEdittext().addTextChangedListener(textWatcher);
        getEdittextClearButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEdittext().setText("");
            }
        });
    }

    private void setOnSearchIconClicked(final EditText editText){
        final String str = editText.getText().toString();
        ImageView searchButton = getSearchButton();
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suggestionUtility.setSuggestionHitoryData(context, str);
                cancelSearchView(context, false);
                launchFragmentContainer(editText.getText().toString());
            }
        });
    }

    public boolean getIsSearchViewAdded(){
        return isSearchViewAdded;
    }

    public LinearLayout getWholeSearchViewAlongWithContainer(){
        if (!iSafeToCall) throw new IllegalStateException("please call the init function first");
        return view;
    }

    @Deprecated
    public LinearLayout getWholeSearchView(){
        if (!iSafeToCall) throw new IllegalStateException("please call the init function first");
        return view.findViewById(R.id.main_search_view);
    }

    public LinearLayout getSearchViewWindow(){
        if (!iSafeToCall) throw new IllegalStateException("please call the show function first");
        return view.findViewById(R.id.custom_search_view_window);
    }

    private View getTopMostStatusBar(){
        if (!iSafeToCall) throw new IllegalStateException("please call the show function first");
        return view.findViewById(R.id.top_most_status_bar);
    }

    public EditText getEdittext(){
        if (!iSafeToCall) throw new IllegalStateException("please call the show function first");
        return view.findViewById(R.id.custom_search_view_edittext);
    }

    public ImageView getAppIcon(){
        if (!iSafeToCall) throw new IllegalStateException("please call the show function first");
        return view.findViewById(R.id.custom_search_view_app_icon);
    }

    public ImageView getMicButton(){
        if (!iSafeToCall) throw new IllegalStateException("please call the show function first");
        return view.findViewById(R.id.custom_search_view_mic_button);
    }

    public ImageView getBackButton(){
        if (!iSafeToCall) throw new IllegalStateException("please call the show function first");
        return view.findViewById(R.id.custom_search_view_back_button);
    }

    public ImageView getHintSearchIcon(){
        if (!iSafeToCall) throw new IllegalStateException("please call the show function first");
        return view.findViewById(R.id.custom_search_view_search_icon_hint);
    }

    public RecyclerView getRecyclerView(){
        if (!iSafeToCall) throw new IllegalStateException("please call the show function first");
        return view.findViewById(R.id.custom_search_view_recycler_view);
    }

    public ImageView getSearchButton(){
        if (!iSafeToCall) throw new IllegalStateException("please call the show function first");
        return view.findViewById(R.id.custom_search_view_search_button);
    }

    public TextView getEmptySearchResultText(){
        if (!iSafeToCall) throw new IllegalStateException("please call the show function first");
        return view.findViewById(R.id.custom_search_view_empty_search_result);
    }

    public FrameLayout getSuggestionLayoutContainer(){
        if (!iSafeToCall) throw new IllegalStateException("please call the show function first");
        return view.findViewById(R.id.suggestion_container);
    }

    public ImageView getEdittextClearButton(){
        if (!iSafeToCall) throw new IllegalStateException("please call the show function first");
        return view.findViewById(R.id.custom_search_edittext_clear_button);
    }

    */
/*public MaterialCardView getTopMostMaterialCard(){
        if (!iSafeToCall) throw new Illega
        lStateException("please call the show function first");
        return view.findViewById(R.id.main_search_view_material_card);
    }*//*


}
*/
