package ru.finnapp.ui.main;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import ru.finnapp.R;
import ru.finnapp.ui.main.viewModels.SearchViewModel;
import ru.finnapp.utils.Constants;

public class MainActivity extends AppCompatActivity implements Constants {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this);

        ViewPager2 viewPager = findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setVerticalScrollBarEnabled(true);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        new TabLayoutMediator(tabs, viewPager, true,
            (tab, position) -> {
                switch (position) {
                    case 0:
                        tab.setText(R.string.StocksTab);
                        break;
                    case 1:
                        tab.setText(R.string.FavoriteTab);
                        break;
                }
            }).attach();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        final MenuItem searchMenuItem = menu.findItem(R.id.search_menu);
        if (searchMenuItem != null) {

            SearchViewModel searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);

            SearchView mSearchView = (SearchView) searchMenuItem.getActionView();
            mSearchView.setQueryHint(getString(R.string.SearchStock));
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    searchViewModel.setQuery(newText);
                    return false;
                }
            });
            searchMenuItem.expandActionView();

        }

        return true;

    }

}