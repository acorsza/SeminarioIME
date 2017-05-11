package br.usp.ime.mac5743.ep1.seminarioime.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.usp.ime.mac5743.ep1.seminarioime.R;
import br.usp.ime.mac5743.ep1.seminarioime.activity.SeminarDetailsActivity;
import br.usp.ime.mac5743.ep1.seminarioime.pojo.Seminar;

/**
 * Created by aderleifilho on 06/05/17.
 */

public class SeminarCardListAdapter extends RecyclerView.Adapter<SeminarCardListAdapter.SeminarViewHolder> {

    public static class SeminarViewHolder extends RecyclerView.ViewHolder {

        CardView seminarCardView;
        TextView seminarName;
        TextView seminarId;
        Context context;
        List<Seminar> seminars;

        SeminarViewHolder(View itemView, Context pContext, final List<Seminar> pSeminars) {
            super(itemView);
            context = pContext;
            seminars = pSeminars;
            seminarCardView = (CardView) itemView.findViewById(R.id.seminar_cardview);
            seminarId = (TextView) itemView.findViewById(R.id.seminar_cardview_id);
            seminarName = (TextView) itemView.findViewById(R.id.seminar_cardview_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = Integer.valueOf(v.getTag().toString());

                    Intent intent = new Intent(context, SeminarDetailsActivity.class);
                    Bundle b = new Bundle();
                    b.putString("seminarId", seminars.get(i).getSeminarId());
                    b.putString("seminarName", seminars.get(i).getSeminarName());
                    intent.putExtras(b);
                    context.startActivity(intent);
                }
            });
        }
    }

    List<Seminar> listSeminars;
    Context context;

    public SeminarCardListAdapter(List<Seminar> pSeminars, Context pContext) {
        this.listSeminars = pSeminars;
        this.context = pContext;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public SeminarViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_seminar, viewGroup, false);
        SeminarViewHolder seminarViewHolder = new SeminarViewHolder(view, context, listSeminars);
        return seminarViewHolder;
    }

    @Override
    public void onBindViewHolder(SeminarViewHolder seminarViewHolder, int i) {
        seminarViewHolder.seminarId.setText(listSeminars.get(i).getSeminarId());
        seminarViewHolder.seminarName.setText(listSeminars.get(i).getSeminarName());
        seminarViewHolder.seminarCardView.setTag(String.valueOf(i));
    }

    @Override
    public int getItemCount() {
        return listSeminars.size();
    }
}
