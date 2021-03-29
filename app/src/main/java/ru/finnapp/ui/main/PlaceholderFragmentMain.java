package ru.finnapp.ui.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.finnapp.ui.main.viewModels.FavoriteViewModel;
import ru.finnapp.R;
import ru.finnapp.models.StockInfo;
import ru.finnapp.models.StockInfoFavorite;
import ru.finnapp.request.RequestApi;
import ru.finnapp.ui.database.DatabaseHelper;
import ru.finnapp.ui.main.viewModels.SearchViewModel;
import ru.finnapp.ui.main.viewModels.MainViewModel;
import ru.finnapp.utils.Constants;

public class PlaceholderFragmentMain extends Fragment implements Constants {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private SearchAdapter searchAdapter;
    private volatile Handler applicationHandler;
    private LinearLayout notFoundLayout;
    private StockRecyclerViewAdapter viewAdapter;
    private final List<StockInfo> stockInfoList = new ArrayList<>();
    private final List<String> symbolList = new ArrayList<>();

    public static PlaceholderFragmentMain newInstance(int index) {
        PlaceholderFragmentMain fragment = new PlaceholderFragmentMain();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        searchAdapter = new SearchAdapter();
        applicationHandler = new Handler(Looper.getMainLooper());

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_stocks, container, false);

        MainActivity activity = (MainActivity) getActivity();
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(activity);

        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        notFoundLayout = view.findViewById(R.id.notFoundLayout);

        boolean isStocksTab = getArguments().getInt(ARG_SECTION_NUMBER) == 0;
        if (isStocksTab) {

            viewAdapter = new StockRecyclerViewAdapter(stockInfoList, symbolList, activity);

            RecyclerView recyclerView = view.findViewById(R.id.list);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            recyclerView.setAdapter(viewAdapter);
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            RequestApi requestApi = new RequestApi(activity, viewAdapter);
            requestApi.stockProfileRequest();

        }
        else {

            StockInfoFavorite favorite = dbHelper.getStockFavoriteList();

            stockInfoList.addAll(favorite.getStockInfoList());
            symbolList.addAll(favorite.getSymbolList());
            viewAdapter = new StockRecyclerViewAdapter(stockInfoList, symbolList, activity);

            RecyclerView recyclerView = view.findViewById(R.id.list);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            recyclerView.setAdapter(viewAdapter);
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

        }

        progressBar.setVisibility(View.GONE);

        SearchViewModel searchViewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);
        searchViewModel.getQuery().observe(getViewLifecycleOwner(), s -> searchAdapter.filterSearch(s));

        FavoriteViewModel favoriteViewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);
        favoriteViewModel.getFavoriteLiveData().observe(getViewLifecycleOwner(), stockInfo -> {
            if (notFoundLayout.getVisibility() == View.VISIBLE)
                notFoundLayout.setVisibility(View.GONE);

            if (isStocksTab) {
                viewAdapter.updateFavoriteStock(stockInfo);

                if (stockInfoList != viewAdapter.getStockInfoList()) {
                    int index = symbolList.indexOf(stockInfo.getSymbol());
                    StockInfo stock = stockInfoList.get(index);
                    stock.setFavorite(stockInfo.isFavorite());
                }
            }
            else {
                viewAdapter.updateFavoriteList(stockInfo);

                if (stockInfoList != viewAdapter.getStockInfoList()) {
                    if (stockInfo.isFavorite()) {
                        symbolList.add(stockInfo.getSymbol());
                        stockInfoList.add(stockInfo);
                    }
                    else if (!symbolList.isEmpty()) {
                        int index = symbolList.indexOf(stockInfo.getSymbol());
                        symbolList.remove(index);
                        stockInfoList.remove(index);
                    }
                }
            }

        });

        MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getSocketLiveData().observe(getViewLifecycleOwner(), viewAdapter::updateCurrentPrice);

        return view;

    }

    public class SearchAdapter {
        private SearchFilter mSearchFilter;
        private Runnable searchRunnable;

        void filterSearch(String query) {
            if (mSearchFilter == null) {
                mSearchFilter = new SearchFilter();
            }
            mSearchFilter.search(query);
        }

        private class SearchFilter {

            protected void search(final String query) {

                notFoundLayout.setVisibility(View.GONE);

                if (searchRunnable != null) {
                    applicationHandler.removeCallbacks(searchRunnable);
                    searchRunnable = null;
                }

                if (TextUtils.isEmpty(query)) {

                    viewAdapter.setStockInfoList(stockInfoList);
                    viewAdapter.setSymbolList(symbolList);

                } else {

                    applicationHandler.postDelayed(searchRunnable = () -> {

                        final String search = query.trim().toLowerCase();
                        List<StockInfo> foundStockList = new ArrayList<>();
                        List<String> foundSymbolList = new ArrayList<>();

                        for (int i = 0; i < stockInfoList.size(); i++) {

                            StockInfo stockInfo = stockInfoList.get(i);

                            if (stockInfo.getSymbol().toLowerCase().contains(search)
                                    || stockInfo.getName().toLowerCase().contains(search)) {
                                foundStockList.add(stockInfo);
                                foundSymbolList.add(stockInfo.getSymbol());
                            }


                        }

                        updateSearchResults(foundStockList, foundSymbolList);

                    }, 300);
                }
            }

            private void updateSearchResults(final List<StockInfo> foundStockList,
                                             final List<String> foundSymbolList) {
                applicationHandler.post(() -> {
                    if (foundStockList.size() == 0) {
                        notFoundLayout.setVisibility(View.VISIBLE);
                    }
                    viewAdapter.setStockInfoList(foundStockList);
                    viewAdapter.setSymbolList(foundSymbolList);
                });
            }

        }

    }

}