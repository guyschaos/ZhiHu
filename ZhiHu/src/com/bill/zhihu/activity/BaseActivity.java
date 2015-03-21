package com.bill.zhihu.activity;

import com.bill.zhihu.R;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * custom which layout would be replace
     * 
     * @param id
     * @param fragment
     */
    public void toggleFragment(int id, Fragment fragment) {
        toggleFragment(id, fragment, FragmentTransaction.TRANSIT_ENTER_MASK,
                FragmentTransaction.TRANSIT_EXIT_MASK);
    }

    /**
     * use R.id.fragment_container for replace
     * 
     * @param fragment
     *            target fragment
     */
    public void toggleFragment(Fragment fragment) {
        toggleFragment(R.id.fragment_container, fragment);
    }

    /**
     * custom enter and exit animation
     * 
     * @param id
     * @param fragment
     * @param enter
     * @param exit
     */
    public void toggleFragment(int id, Fragment fragment, int enter, int exit) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(id, fragment);
        ft.setCustomAnimations(enter, exit);
        ft.commit();
    }

}
