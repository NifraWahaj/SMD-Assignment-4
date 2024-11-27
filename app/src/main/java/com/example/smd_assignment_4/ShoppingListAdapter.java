package com.example.smd_assignment_4;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShoppingListAdapter {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textQuantity, textPrice;
        ImageButton buttonDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text_name);
            textQuantity = itemView.findViewById(R.id.text_quantity);
            textPrice = itemView.findViewById(R.id.text_price);
            buttonDelete = itemView.findViewById(R.id.button_delete);
        }
    }
}
