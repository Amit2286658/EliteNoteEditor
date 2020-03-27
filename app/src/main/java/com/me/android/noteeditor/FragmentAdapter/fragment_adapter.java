package com.me.android.noteeditor.FragmentAdapter;

import android.view.View;
import android.view.ViewGroup;

import com.me.android.noteeditor.FolderClass;
import com.me.android.noteeditor.NotesClass;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class fragment_adapter extends FragmentPagerAdapter {

    private String tab_names[] = new String[]{"Notes", "Folders"};

    public fragment_adapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0 :
                return new NotesClass();
            case 1 :
                return new FolderClass();
        }
        return null;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        container.removeView((View) object);
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return 2;
    }

    /**
     * This method may be called by the ViewPager to obtain a title string
     * to describe the specified page. This method may return null
     * indicating no title for this page. The default implementation returns
     * null.
     *
     * @param position The position of the title requested
     * @return A title for the requested page
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return tab_names[position];
    }
}
