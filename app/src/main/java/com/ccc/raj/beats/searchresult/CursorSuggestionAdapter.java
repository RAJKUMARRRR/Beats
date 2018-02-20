package com.ccc.raj.beats.searchresult;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.ccc.raj.beats.MainActivity;
import com.ccc.raj.beats.R;
import com.ccc.raj.beats.SearchActivity;

/**
 * Created by Raj on 1/25/2018.
 */

public class CursorSuggestionAdapter extends CursorAdapter {
    LayoutInflater layoutInflater;
    private OnSuggessionClickListener mOnSuggessionClickListener;
    public interface OnSuggessionClickListener{
        public void onSuggessionSelect(String suggession,String action);
    }

    public void setOnSuggessionClickListener(OnSuggessionClickListener onSuggessionClickListener){
        mOnSuggessionClickListener = onSuggessionClickListener;
    }

    public CursorSuggestionAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = layoutInflater.inflate(R.layout.search_suggestion,viewGroup,false);
        return view;
    }

    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
    }

    @Override
    public void bindView(View view, Context context,Cursor cursor) {
        int columnIndex = cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1);
        final String text = cursor.getString(columnIndex);
        TextView textView = view.findViewById(R.id.suggestion_text);
        textView.setText(text);
        int columnAction = cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_INTENT_ACTION);
        final String action = cursor.getString(columnAction);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*int columnAction = cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_INTENT_ACTION);
                String action = cursor.getString(columnAction);
                int columnIndexText = cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1);
                String textSuggestion = cursor.getString(columnIndexText);
                Intent intent = new Intent(context, SearchActivity.class);
                intent.setAction(action);
                intent.setData(Uri.parse(textSuggestion));
                context.startActivity(intent);*/
                if(mOnSuggessionClickListener != null){
                    mOnSuggessionClickListener.onSuggessionSelect(text,action);
                }
            }
        });
    }
}




