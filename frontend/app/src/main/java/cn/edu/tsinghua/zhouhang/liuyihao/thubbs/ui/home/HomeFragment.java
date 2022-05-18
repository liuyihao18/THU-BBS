package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.Constant;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.FragmentHomeBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.ui.tweets.TweetsFragment;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        initializeTayLayoutAndViewPager2();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initializeTayLayoutAndViewPager2() {
        final TabLayout tabLayout = binding.tabLayout;
        final ViewPager2 viewPager2 = binding.pager;

        FragmentStateAdapter adapter = new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                TweetsFragment tweetsFragment = new TweetsFragment();
                Bundle bundle = new Bundle();
                switch (position) {
                    case 0:
                        bundle.putInt(Constant.TWEETS_TYPE, Constant.TWEETS_ALL);
                        break;
                    case 1:
                        bundle.putInt(Constant.TWEETS_TYPE, Constant.TWEETS_FOLLOW);
                        break;
                    default:
                        bundle.putInt(Constant.TWEETS_TYPE, Constant.TWEETS_EMPTY);
                        break;
                }
                tweetsFragment.setArguments(bundle);
                return tweetsFragment;
            }

            @Override
            public int getItemCount() {
                return tabLayout.getTabCount();
            }
        };
        viewPager2.setAdapter(adapter);

        TabLayout.TabLayoutOnPageChangeListener pageChangeListener = new TabLayout.TabLayoutOnPageChangeListener(tabLayout);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                pageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                pageChangeListener.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                pageChangeListener.onPageScrollStateChanged(state);
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}