/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package com.hg.mad.adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.hg.mad.R;
import com.hg.mad.model.CompetitiveEvent;


/**
 * RecyclerView adapter for a list of Restaurants.
 */
public class CompetitiveAdapter extends FirestoreAdapter<CompetitiveAdapter.ViewHolder> {

    public interface OnCompetitiveListener {

        void onCompetitiveSelected(DocumentSnapshot competitive);

    }

    private OnCompetitiveListener listener;

    public CompetitiveAdapter(Query query, OnCompetitiveListener listener) {
        super(query);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_competitive, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameView;
        TextView typeView;
        TextView categoryView;
        TextView introView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.competitiveTitle);
            typeView = itemView.findViewById(R.id.competitiveType);
            categoryView = itemView.findViewById(R.id.competitiveCategory);
            introView = itemView.findViewById(R.id.competitiveIntro);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnCompetitiveListener listener) {

            CompetitiveEvent competitiveEvent = snapshot.toObject(CompetitiveEvent.class);
            Resources resources = itemView.getResources();

            nameView.setText(competitiveEvent.getEventName());
            typeView.setText(competitiveEvent.getType());
            categoryView.setText(competitiveEvent.getCategory());
            introView.setText(competitiveEvent.getIntro());

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onCompetitiveSelected(snapshot);
                    }
                }
            });
        }

    }
}
