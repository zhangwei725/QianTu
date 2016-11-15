package com.zw.common.bottombar;

import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 *
 */
public class FragNavController {

    @IntDef({TAB1, TAB2, TAB3, TAB4, TAB5})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TabIndex {
    }

    public static final int TAB1 = 0;
    public static final int TAB2 = 1;
    public static final int TAB3 = 2;
    public static final int TAB4 = 3;
    public static final int TAB5 = 4;

    private final List<Stack<Fragment>> mFragmentStacks;
    private final FragmentManager mFragmentManager;
    @TabIndex
    int mSelectedTabIndex = -1;
    private int mTagCount;
    private Fragment mCurrentFrag;
    private NavListener mNavListener;
    private
    @IdRes
    int mContainerId;

    public FragNavController(@NonNull FragmentManager fragmentManager, @IdRes int containerId, @NonNull List<Fragment> baseFragments) {
        mFragmentManager = fragmentManager;
        mContainerId = containerId;
        mFragmentStacks = new ArrayList<>(baseFragments.size());
        for (Fragment fragment : baseFragments) {
            Stack<Fragment> stack = new Stack<>();
            stack.add(fragment);
            mFragmentStacks.add(stack);
        }
    }


    public void setNavListener(NavListener navListener) {
        mNavListener = navListener;
    }

    public void switchTab(@TabIndex int index) {
        if (index >= mFragmentStacks.size()) {
            throw new IndexOutOfBoundsException("Can't switch to a tab that hasn't been hasn't been initialized. " +
                    "Make sure to create all of the tabs you need in the Constructor");
        }
        if (mSelectedTabIndex != index) {
            mSelectedTabIndex = index;
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            hideCurrentFragment(ft);
            Fragment fragment = showPreviousFragment(ft);
            if (fragment != null) {
                ft.commit();
            } else {
                fragment = mFragmentStacks.get(mSelectedTabIndex).peek();
                ft.add(mContainerId, fragment, generateTag(fragment));
                ft.commit();
            }

            mCurrentFrag = fragment;
            if (mNavListener != null) {
                mNavListener.onTabTransaction(mCurrentFrag, mSelectedTabIndex);
            }
        }
    }

    public void push(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            hideCurrentFragment(ft);
            ft.add(mContainerId, fragment, generateTag(fragment));
            ft.commit();
            mFragmentManager.executePendingTransactions();
            mFragmentStacks.get(mSelectedTabIndex).push(fragment);
            mCurrentFrag = fragment;
            if (mNavListener != null) {
                mNavListener.onFragmentTransaction(mCurrentFrag);
            }

        }
    }

    public void pop() {
        Fragment poppingFrag = getCurrentFrag();
        if (poppingFrag != null) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.remove(poppingFrag);
            Stack<Fragment> fragmentStack = mFragmentStacks.get(mSelectedTabIndex);
            if (!fragmentStack.isEmpty()) {
                fragmentStack.pop();
            }

            Fragment fragment = showPreviousFragment(ft);
            if (fragment == null && !fragmentStack.isEmpty()) {
                fragment = fragmentStack.peek();
                ft.add(mContainerId, fragment, fragment.getTag());
            }

            //Commit our transactions
            ft.commit();
            mFragmentManager.executePendingTransactions();

            mCurrentFrag = fragment;
            if (mNavListener != null) {
                mNavListener.onFragmentTransaction(mCurrentFrag);
            }
        }
    }


    public void clearStack() {
        Stack<Fragment> fragmentStack = mFragmentStacks.get(mSelectedTabIndex);
        if (fragmentStack.size() > 1) {
            Fragment fragment;
            FragmentTransaction ft = mFragmentManager.beginTransaction();

            //Pop all of the fragments on the stack and remove them from the FragmentManager
            while (fragmentStack.size() > 1) {
                fragment = mFragmentManager.findFragmentByTag(fragmentStack.peek().getTag());
                if (fragment != null) {
                    fragmentStack.pop();
                    ft.remove(fragment);
                }
            }
            fragment = showPreviousFragment(ft);
            if (fragment != null) {
                ft.commit();
            } else {
                if (!fragmentStack.isEmpty()) {
                    fragment = fragmentStack.peek();
                    ft.add(mContainerId, fragment, fragment.getTag());
                    ft.commit();
                }
            }
            mFragmentStacks.set(mSelectedTabIndex, fragmentStack);
            mCurrentFrag = fragment;
            if (mNavListener != null) {
                mNavListener.onFragmentTransaction(mCurrentFrag);
            }
        }
    }

    @Nullable
    private Fragment showPreviousFragment(FragmentTransaction ft) {
        Stack<Fragment> fragmentStack = mFragmentStacks.get(mSelectedTabIndex);
        Fragment fragment = null;
        if (!fragmentStack.isEmpty()) {
            fragment = mFragmentManager.findFragmentByTag(fragmentStack.peek().getTag());
            if (fragment != null) {
                ft.show(fragment);
            }
        }
        return fragment;
    }

    private void hideCurrentFragment(FragmentTransaction ft) {
        Fragment oldFrag = getCurrentFrag();
        if (oldFrag != null) {
            ft.hide(oldFrag);
        }
    }


    @Nullable
    private Fragment getCurrentFrag() {
        if (mCurrentFrag != null) {
            return mCurrentFrag;
        } else {
            Stack<Fragment> fragmentStack = mFragmentStacks.get(mSelectedTabIndex);
            if (!fragmentStack.isEmpty()) {
                return mFragmentManager.findFragmentByTag(mFragmentStacks.get(mSelectedTabIndex).peek().getTag());
            } else {
                return null;
            }
        }
    }

    private String generateTag(Fragment fragment) {
        return fragment.getClass().getName() + ++mTagCount;
    }

    public Stack<Fragment> getCurrentStack() {
        return mFragmentStacks.get(mSelectedTabIndex);
    }

    public interface NavListener {
        void onTabTransaction(Fragment fragment, int index);

        void onFragmentTransaction(Fragment fragment);
    }
}