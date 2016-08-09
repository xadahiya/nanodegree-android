package com.typingeek.xadahiya.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by xadahiya on 8/9/16.
 */
public class CrimeListActivity extends SingleFragmentActivity{

    @Override
    protected Fragment createFragment(){
        return new CrimeListFragment();
    }
}
