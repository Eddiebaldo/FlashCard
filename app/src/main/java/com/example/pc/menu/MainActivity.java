package com.example.pc.menu;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import java.util.*;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private android.support.v7.widget.Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.menu_action);
        setSupportActionBar(mToolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle =  new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return  super.onOptionsItemSelected(item);
    }
    public class Activity1 extends Activity {
        /** Called when the activity is first created. */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.add_card);
            Button next = (Button) findViewById(R.id.addbutton);
            next.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent myIntent = new Intent(view.getContext(), Activity1.class);
                    startActivityForResult(myIntent, 0);
                }

            });
        }
    }
    public class Activity2 extends Activity {

        /**
         * Called when the activity is first created.
         */
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            Button next = (Button) findViewById(R.id.mainmenu);
            next.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }

            });
        }
    }

    public class card{
        String question;
        String answer;
        public card(String question, String answer){
            this.question = question;
            this.answer = answer;
        }
        ArrayList<card> cards = new ArrayList<card>(5);
        EditText inputquestion;
        EditText inputanswer;
        ImageButton addbutton;

        protected void onCreate(Bundle savedCard){
            setContentView(R.layout.add_card);
            inputquestion = (EditText) findViewById(R.id.inputquestion);
            inputanswer = (EditText) findViewById(R.id.inputanswer);
            addbutton = (ImageButton) findViewById(R.id.addbutton);
            addbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    question = inputquestion.getText().toString();
                    answer = inputanswer.getText().toString();
                    card adding = new card(question,answer);
                    cards.add(adding);
                }
            });



        }
    }

}
