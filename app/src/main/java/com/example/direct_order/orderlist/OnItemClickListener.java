package com.example.direct_order.orderlist;

import com.google.firebase.firestore.DocumentSnapshot;

public interface OnItemClickListener {
    void onItemClick(DocumentSnapshot documentSnapshot, int position);
}
