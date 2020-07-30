package com.gaa.iap.sample.widget;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gaa.sdk.iap.ProductDetail;
import com.gaa.iap.sample.R;

import java.util.List;

public class BuyProductPopup extends AlertDialog {


    public interface OnItemClickListener {
        void onClick(ProductDetail item);
    }

    public BuyProductPopup(@NonNull Context context, @NonNull List<ProductDetail> items, @Nullable OnItemClickListener listener) {
        super(context);

        if (items.isEmpty()) {
            throw new IllegalArgumentException();
        }

        setTitle("Buy Product");

        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new PaddingItemDecoration(context, 10));
        recyclerView.setAdapter(new ProductAdapter(context, items, listener));
        setView(recyclerView);
    }

    private class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {
        private LayoutInflater inflater;
        private List<ProductDetail> items;
        private OnItemClickListener listener;

        ProductAdapter(Context context, List<ProductDetail> items, OnItemClickListener listener) {
            this.inflater = LayoutInflater.from(context);
            this.items = items;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ProductViewHolder(inflater.inflate(R.layout.dialog_buy_product_row, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
            holder.setData(items.get(position), listener);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }


    private class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text1);
        }

        void setData(final ProductDetail productDetail, final OnItemClickListener listener) {
            textView.setText(productDetail.getTitle());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(productDetail);
                    }
                    dismiss();
                }
            });
        }
    }

    private static class PaddingItemDecoration extends RecyclerView.ItemDecoration {
        private int PADDING;
        private int LEFT_RIGHT;
        private int space;

        PaddingItemDecoration(Context context, int space) {
            float density = context.getResources().getDisplayMetrics().density;
            PADDING = (int) (15 * density);
            LEFT_RIGHT = (int) (20 * density);
            this.space = (int) (space * density);
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state) {
            int adapterPosition = parent.getChildAdapterPosition(view);
            int itemCount = parent.getAdapter().getItemCount() - 1;
            if(adapterPosition != itemCount)
                outRect.bottom = space;

            if(adapterPosition == 0)
                outRect.top = space + PADDING;

            if(adapterPosition == itemCount)
                outRect.bottom = space + PADDING;

            outRect.left  = LEFT_RIGHT;
            outRect.right = LEFT_RIGHT;
        }
    }
}
