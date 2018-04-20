package notecardproject.com.notecard;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.gson.*;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;


public class AddCardActivity extends AppCompatActivity {

    // This is the sharedpreferences file that is used to save Card list.
    private final static String SHARED_PREFERENCES_FILE_USER_INFO_LIST = "cardInfoList";

    // This is the saved Card list jason string key in sharedpreferences file..
    private final static String SHARED_PREFERENCES_KEY_USER_INFO_LIST = "Card_Info_List";

    // This is the debug log info tag which will be printed in the android monitor console.
    private final static String USER_INFO_LIST_TAG = " CARD_INFO_LIST_TAG";
    NoteCardCollection allCards = new NoteCardCollection();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        ImageButton addBtn = (ImageButton) findViewById(R.id.addbutton);
        ImageButton homeBtn = (ImageButton) findViewById(R.id.homebutton);

        addBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Log.d("App", "I am here");

                TextView questionView = (TextView) findViewById(R.id.inputquestion);
                TextView answerView = (TextView) findViewById(R.id.inputanswer);

                NoteCardClass newCard = new NoteCardClass(questionView.getText().toString(), answerView.getText().toString());
                System.out.println(questionView.getText().toString() + "\n" + answerView.getText().toString());

                Gson gson = new Gson();
                //NoteCardCollection allCards = gson.fromJson(userInfoListJsonString, NoteCardCollection[].class);

                allCards.cards.add(newCard);
                Context ctx = getApplicationContext();
                SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREFERENCES_FILE_USER_INFO_LIST, MODE_PRIVATE);

                // Get saved string data in it.
                // String userInfoListJsonString = sharedPreferences.getString(SHARED_PREFERENCES_KEY_USER_INFO_LIST, "");

                // Log.d("App", userInfoListJsonString);

                // Create Gson object and translate the json string to related java object array.
                //Gson gson = new Gson();
                //allCards = gson.fromJson(userInfoListJsonString, NoteCardCollection.class);

                allCards.cards.add(newCard);

                // Get java object list json format string.
                String cardString = gson.toJson(allCards);

                // Put the json format string to SharedPreferences object.
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(SHARED_PREFERENCES_KEY_USER_INFO_LIST, cardString);
                editor.commit();

                String finalString = sharedPreferences.getString(SHARED_PREFERENCES_KEY_USER_INFO_LIST, "");

                Intent intent = new Intent(AddCardActivity.this, AddCardActivity.class);
                startActivity(intent);

                Log.d("App", finalString);
            }
        });
        homeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddCardActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
