package com.agrosmart;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.agrosmart.ItemFragment.OnListFragmentInteractionListener;
import com.agrosmart.dummy.DummyContent.DummyItem;
import com.jjoe64.graphview.GraphView;

import java.lang.reflect.Parameter;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<DataGraph> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final Context mContext;

    public MyItemRecyclerViewAdapter(List<DataGraph> items, OnListFragmentInteractionListener listener, Context context) {
        mValues = items;
        mListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.nameEstufa.setText(mValues.get(position).getTitulo());
        holder.graphView.addSeries(mValues.get(position).getSeries());

        holder.imageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onEditInteraction(mValues.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView nameEstufa;
        GraphView graphView;
        ImageView imageEdit;


        public ViewHolder(View view) {
            super(view);

            nameEstufa = (TextView) view.findViewById(R.id.estufaName);
            graphView = (GraphView) view.findViewById(R.id.graph);
            imageEdit = (ImageView) view.findViewById(R.id.imageEdit);
        }


    }
}
