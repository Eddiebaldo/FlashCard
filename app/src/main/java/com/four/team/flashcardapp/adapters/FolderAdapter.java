package com.four.team.flashcardapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.four.team.flashcardapp.R;
import com.four.team.flashcardapp.room.domain.Folder;

import java.util.ArrayList;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {
    private ArrayList<Folder> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public Folder folder;
        public ViewHolder(View v) {
            super(v);
            //mTextView = v.findViewById(R.id.folderName);
        }
        public void bind(Folder folder){
            this.folder = folder;
            TextView textView = itemView.findViewById(R.id.folderName);
            textView.setText(folder.getSubject());

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FolderAdapter(ArrayList<Folder> myDataset) {
        mDataset = myDataset;
    }

    public void setmDataset(ArrayList<Folder> mDataset){
        this.mDataset = mDataset;
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FolderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.folderitem, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.bind(mDataset.get(position));

    }

    public void addFolder(Folder newFolder){
        mDataset.add(newFolder);
        notifyItemInserted(mDataset.size()-1);
    }

    public void removeFolder(Folder name){
        int index = mDataset.indexOf(name);
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
