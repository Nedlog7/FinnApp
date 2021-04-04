package ru.finnapp.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ru.finnapp.R;
import ru.finnapp.drawable.CircleTransform;
import ru.finnapp.models.CurrentPrice;
import ru.finnapp.models.Data;
import ru.finnapp.models.StockInfo;
import ru.finnapp.ui.database.DatabaseHelper;
import ru.finnapp.utils.Constants;

public class StockRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Constants {

    private List<StockInfo> stockInfoList;
    private List<String> symbolList;
    private final DatabaseHelper dbHelper;

    public StockRecyclerViewAdapter(List<StockInfo> stockInfoList, List<String> symbolList, Context context) {
        this.stockInfoList = stockInfoList;
        this.symbolList = symbolList;
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public void setStockInfoList(List<StockInfo> stockInfoList) {
        this.stockInfoList = stockInfoList;
        notifyDataSetChanged();
    }

    public void setSymbolList(List<String> symbolList) {
        this.symbolList = symbolList;
    }

    public List<StockInfo> getStockInfoList() {
        return stockInfoList;
    }

    public void setStock(StockInfo stock) {
        StockInfo stockInfo = dbHelper.getStockInfo(stock.getSymbol());
        if (stockInfo == null)
            dbHelper.putStock(stock);
        else {
            stock.setFavorite(stockInfo.isFavorite());
        }

        symbolList.add(stock.getSymbol());
        stockInfoList.add(stock);

        notifyItemInserted(stockInfoList.size() - 1);
    }

    public void updateStock(String symbol, float closePrice, float currentPrice) {

        if (symbolList.contains(symbol)) {
            int index = symbolList.indexOf(symbol);
            StockInfo stockInfo = stockInfoList.get(index);
            stockInfo.setClosePrice(closePrice);
            stockInfo.setCurrentPrice(currentPrice);

            dbHelper.setClosePrice(symbol, closePrice);

            notifyItemChanged(index, stockInfo);
        }

    }

    public void updateCurrentPrice(CurrentPrice price) {

        if (price.data != null && price.data.size() > 0) {

            Data data = price.data.get(price.data.size() - 1);
            if (symbolList.contains(data.s)) {

                int index = symbolList.indexOf(data.s);
                StockInfo stockInfo = stockInfoList.get(index);
                stockInfo.setCurrentPrice(data.p);
                notifyItemChanged(index, stockInfo);

            }

        }

    }

    public void updateFavoriteStock(StockInfo stock) {

        if (symbolList.contains(stock.getSymbol())) {
            int index = symbolList.indexOf(stock.getSymbol());
            StockInfo stockInfo = stockInfoList.get(index);
            stockInfo.setFavorite(stock.isFavorite());

            notifyItemChanged(index, stockInfo);
        }

    }

    public synchronized void updateFavoriteList(StockInfo stock) {

        if (stock.isFavorite()) {
            symbolList.add(stock.getSymbol());
            stockInfoList.add(stock);

            notifyItemInserted(stockInfoList.size() - 1);
        }
        else if (!symbolList.isEmpty()) {

            int index = symbolList.indexOf(stock.getSymbol());
            symbolList.remove(index);
            stockInfoList.remove(index);
            notifyItemRemoved(index);
            notifyItemRangeChanged(index, getItemCount());

        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_stock, parent, false);
        return new StockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((StockViewHolder) holder).bind(stockInfoList.get(position));
    }

    @Override
    public int getItemCount() {
        return stockInfoList.size();
    }

    private class StockViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivLogo;
        private final ImageView ivFavorite;
        private final TextView tvTicker;
        private final TextView tvName;
        private final TextView tvPrice;
        private final TextView tvVolatility;

        public StockViewHolder(@NonNull View view) {
            super(view);

            ivLogo = view.findViewById(R.id.ivLogo);
            ivFavorite = view.findViewById(R.id.ivFavorite);
            tvTicker = view.findViewById(R.id.tvTicker);
            tvName = view.findViewById(R.id.tvName);
            tvPrice = view.findViewById(R.id.tvPrice);
            tvVolatility = view.findViewById(R.id.tvVolatility);

            ivFavorite.setOnClickListener(v -> {

                int adapterPosition = getAdapterPosition();
                if (adapterPosition >= 0) {
                    StockInfo stockInfo = stockInfoList.get(adapterPosition);
                    stockInfo.setFavorite(!stockInfo.isFavorite());
                    dbHelper.setFavorite(stockInfo);
                    notifyItemChanged(adapterPosition, stockInfo);
                }

            });

        }

        @SuppressLint("DefaultLocale")
        private void bind(final StockInfo stockInfo) {

            String logoUrl = stockInfo.getLogoUrl();
            if (logoUrl != null && !logoUrl.isEmpty()) {
                Picasso.get()
                        .load(logoUrl)
                        .transform(new CircleTransform(50,0))
                        .into(ivLogo);
            }
            else {
                ivLogo.setImageResource(R.drawable.ic_pending);
            }

            ivFavorite.setImageResource(stockInfo.isFavorite() ? R.drawable.ic_favorite
                    : R.drawable.ic_favorite_border);

            tvTicker.setText(stockInfo.getSymbol());
            tvName.setText(stockInfo.getName());
            tvPrice.setText(String.format("%s%.2f", "$", stockInfo.getCurrentPrice()));

            float volatilityPercent = 0;
            float volatility = 0;
            String sign = "";

            if (stockInfo.getCurrentPrice() != 0) {
                volatility = stockInfo.getCurrentPrice() - stockInfo.getClosePrice();
                if (volatility < 0) {
                    sign = "-";
                    tvVolatility.setTextColor(Color.RED);
                }
                else if (volatility > 0) {
                    sign = "+";
                    tvVolatility.setTextColor(Color.GREEN);
                }

                volatility =  Math.abs(volatility);
                if (volatility != 0 && stockInfo.getClosePrice() != 0)
                    volatilityPercent = volatility * 100 / stockInfo.getClosePrice();
            }

            tvVolatility.setText(String.format("%s%.2f (%.2f%%)", sign + "$", volatility,
                    volatilityPercent));

        }

    }

}
