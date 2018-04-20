package notecardproject.com.notecard;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.google.gson.*;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.*;
import android.widget.*;
import android.widget.LinearLayout.LayoutParams;
import android.app.Activity;

public class ViewCardsActivity extends AppCompatActivity {


    // This is the sharedpreferences file that is used to save Card list.
    private final static String SHARED_PREFERENCES_FILE_USER_INFO_LIST = "cardInfoList";

    // This is the saved Card list jason string key in sharedpreferences file..
    private final static String SHARED_PREFERENCES_KEY_USER_INFO_LIST = "Card_Info_List";

    // This is the debug log info tag which will be printed in the android monitor console.
    private final static String USER_INFO_LIST_TAG = " CARD_INFO_LIST_TAG";
    private EditText edtValor;
    private Button btnAdcionar;
    private Button btnRemover;
    private Spinner spnOpcoes;
    private ListView ScrollBox;

    private ArrayAdapter<String> adpOpcoes;
    private ArrayAdapter<String> adpdados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_cards);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */


        NoteCardCollection allCards = new NoteCardCollection();

        Context ctx = getApplicationContext();
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREFERENCES_FILE_USER_INFO_LIST, MODE_PRIVATE);

        // Get saved string data in it.
        String userInfoListJsonString = sharedPreferences.getString(SHARED_PREFERENCES_KEY_USER_INFO_LIST, "");

        Log.d("App", userInfoListJsonString);

        // Create Gson object and translate the json string to related java object array.
        Gson gson = new Gson();
        allCards = gson.fromJson(userInfoListJsonString, NoteCardCollection.class);


        //LinearLayout linearLayout = (LinearLayout) findViewById(R.id.cardListView);

        for (NoteCardClass cardObj : allCards.cards) {
            // do something with object

            Log.d("App", cardObj.question + " - " + cardObj.answer);

            EditText listView = (EditText) findViewById(R.id.cardListText);

            listView.setText(listView.getText().toString() + "\nQuestion: " + cardObj.question);
            listView.setText(listView.getText().toString() + "\nAnswer: " + cardObj.answer + " \n\n");
            //setContentView(linearLayout);
            //linearLayout.setOrientation(LinearLayout.VERTICAL);


            /*
            TextView textViewQ = new TextView(this);
            textViewQ.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            textViewQ.setText(cardObj.question);
            textViewQ.setBackgroundColor(0xff66ff66); // hex color 0xAARRGGBB
            textViewQ.setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)
            linearLayout.addView(textViewQ);

            TextView textViewA = new TextView(this);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            textViewQ.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            layoutParams.setMargins(10, 10, 10, 10); // (left, top, right, bottom)
            textViewA.setLayoutParams(layoutParams);
            textViewA.setText(cardObj.answer);
            linearLayout.addView(textViewA);
            */

        }

    }

}

