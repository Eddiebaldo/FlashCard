package notecardproject.com.notecard;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListview extends ArrayAdapter<String>{
    private NoteCardCollection allCards = new NoteCardCollection();
    private Activity context;
    public CustomListview(Activity context, NoteCardCollection cards) {
        super(context, R.layout.activity_view_cards);
        this.context = context;
        this.allCards = allCards;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View r = convertView;
        ViewHolder viewHolder = null;
        if(r == null){
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r = layoutInflater.inflate(R.layout.activity_view_cards,null,true);
            viewHolder = new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) r.getTag();

        }
        String question = allCards.cards.get(position).question;
        String answer = allCards.cards.get(position).answer;
        viewHolder.tvw1.setText(question);
        viewHolder.tvw2.setText(answer);
        return super.getView(position, convertView, parent);
    }
    class ViewHolder{
        TextView tvw1;
        TextView tvw2;
        ViewHolder(View v){
            tvw1 = v.findViewById(R.id.Questionbox);
            tvw2 = v.findViewById(R.id.Answerbox);
        }

    }
}
