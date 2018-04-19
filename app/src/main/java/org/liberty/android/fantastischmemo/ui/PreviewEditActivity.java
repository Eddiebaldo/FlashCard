/*
Copyright (C) 2012 Haowen Ning

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

*/
package org.liberty.android.fantastischmemo.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.common.base.Preconditions;

import org.liberty.android.fantastischmemo.R;
import org.liberty.android.fantastischmemo.common.AMPrefKeys;
import org.liberty.android.fantastischmemo.entity.Card;
import org.liberty.android.fantastischmemo.entity.Category;
import org.liberty.android.fantastischmemo.entity.LearningData;
import org.liberty.android.fantastischmemo.entity.Setting;
import org.liberty.android.fantastischmemo.utils.AMGUIUtility;
import org.liberty.android.fantastischmemo.utils.AMPrefUtil;
import org.liberty.android.fantastischmemo.utils.ShareUtil;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.inject.Inject;

public class PreviewEditActivity extends QACardActivity {
    private final int ACTIVITY_EDIT = 11;
    private final int ACTIVITY_SETTINGS = 15;
    private final int ACTIVITY_LIST = 16;
    private final int ACTIVITY_MERGE = 17;
    private final int ACTIVITY_DETAIL = 18;
    private final int ACTIVITY_CARD_PLAYER = 19;
    private final static String WEBSITE_HELP_EDIT = "https://anymemo.org/";
    private long totalCardCount = 0;

    public static String EXTRA_DBPATH = "dbpath";
    public static String EXTRA_CARD_ID = "id";
    public static String EXTRA_CATEGORY = "category";

    private static final String SEARCH_BY_ID_PATTERN = "#\\d+";

    private Category currentCategory = null;
    private Integer savedCardId = null;
    private String dbPath = "";
    private int activeCategoryId = -1;

    private Button newButton;
    private Button editButton;
    private Button prevButton;
    private Button nextButton;

    // Injected objects
    @Inject ShareUtil shareUtil;

    @Inject AMPrefUtil amPrefUtil;

    // The first card to read and display.
    private int startCardId = 1;

    @Override
    public void onCreate(Bundle savedInstanceState){
        activityComponents().inject(this);
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();

        assert extras != null : "Extras for PreviewEditActivity should have at least dbPath!";

        dbPath = extras.getString(EXTRA_DBPATH);
        activeCategoryId = extras.getInt(EXTRA_CATEGORY, -1);
        startCardId = extras.getInt(EXTRA_CARD_ID, -1);

        if (savedInstanceState != null) {
            startCardId = savedInstanceState.getInt(EXTRA_CARD_ID, -1);
        }

        /*
         * Currently always set the result to OK
         * to assume there are always some changes.
         * This may be changed in the future to reflect the
         * real changes
         */
        setResult(Activity.RESULT_OK);

        startInit();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Card currentCard = getCurrentCard();
        if (currentCard != null) {
            outState.putInt(EXTRA_CARD_ID, currentCard.getId());
        }
    }

    @Override
    public int getContentView() {
        return R.layout.qa_card_layout_preview_edit;
    }

    @Override
    public void onPostInit() {
        super.onPostInit();
        Card currentCard = null;

        // If category is set, it will override the card id.
        if (activeCategoryId != -1) {
            currentCategory = getDbOpenHelper().getCategoryDao().queryForId(activeCategoryId);
            currentCard = getDbOpenHelper().getCardDao().queryFirstOrdinal(currentCategory);
        } else if (startCardId != -1) {
            currentCard = getDbOpenHelper().getCardDao().queryForId(startCardId);
        }

        // If None of category and card is is set, first ordinal is queried
        // Note curretnCategory should be null.
        if (currentCard == null) {
            currentCard = getDbOpenHelper().getCardDao().queryFirstOrdinal(currentCategory);
        }

        totalCardCount = getDbOpenHelper().getCardDao().countOf();
        setCurrentCard(currentCard);

        composeViews();
        //currentCard = .getItem(currentId);
        setViewListeners();
        if(getCurrentCard() != null){
            updateCardFrontSide();
            updateTitle();
        }

    }

    // Save the card id in onPause
    @Override
    public void onPause() {
        super.onPause();
        if (getCurrentCard() != null) {
            amPrefUtil.putSavedInt(AMPrefKeys.PREVIEW_EDIT_START_ID_PREFIX, dbPath, getCurrentCard().getId());
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_CANCELED){
            return;
        }

        /* Refresh the activity according to activities */
        switch(requestCode){
            case ACTIVITY_EDIT:
            {
                Bundle extras = data.getExtras();
                int cardId = extras.getInt(CardEditor.EXTRA_RESULT_CARD_ID, 1);
                Card card = getDbOpenHelper().getCardDao().queryForId(cardId);
                if (card != null) {
                    setCurrentCard(card);
                }
                restartActivity();
                break;
            }

            case ACTIVITY_CARD_PLAYER:
            {
                Bundle extras = data.getExtras();
                int cardId = extras.getInt(CardPlayerActivity.EXTRA_RESULT_CARD_ID, 1);
                Card card = getDbOpenHelper().getCardDao().queryForId(cardId);
                if (card != null) {
                    setCurrentCard(card);
                }
                restartActivity();
                break;
            }


            case ACTIVITY_SETTINGS:
            {
                restartActivity();
                break;
            }
            case ACTIVITY_LIST:
            {
                restartActivity();
                break;
            }
            case ACTIVITY_MERGE:
            {
                restartActivity();
                break;
            }
            case ACTIVITY_DETAIL:
            {
                restartActivity();
            }

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuspeakquestion:
            {
                if (getCurrentCard() != null) {
                    return speakQuestion();
                }
                return true;
            }

            case R.id.menuspeakanswer:
            {
                if (getCurrentCard() != null) {
                    return speakAnswer();
                }
                return true;
            }


            case R.id.editmenu_delete_id:
            {
                deleteCard(getCurrentCard());
                return true;
            }


        }

        return false;
    }

    @Override
    // Disable the copy to clipboard for Preview/Edit activity.
    protected void copyToClipboard() {
        Log.v(TAG, "Copy to clipboard is disabled for PreviewEditActivity");
    }



    @Override
    public void restartActivity(){
        Intent myIntent = new Intent(this, PreviewEditActivity.class);
        assert dbPath != null : "Use null dbPath to restartAcitivity";
        myIntent.putExtra(EXTRA_DBPATH, dbPath);
        if (getCurrentCard() != null) {
            myIntent.putExtra(EXTRA_CARD_ID, getCurrentCard().getId());
        }
        myIntent.putExtra(EXTRA_CATEGORY, activeCategoryId);
        finish();
        startActivity(myIntent);
    }

    private void composeViews(){
        newButton = (Button) findViewById(R.id.new_button);
        editButton = (Button) findViewById(R.id.edit_button);
        prevButton = (Button) findViewById(R.id.prev_button);
        nextButton = (Button) findViewById(R.id.next_button);
    }

    private void setViewListeners(){
        ///* Set button listeners */
        newButton.setOnClickListener(newButtonListener);
        editButton.setOnClickListener(editButtonListener);
        prevButton.setOnClickListener(prevButtonListener);
        nextButton.setOnClickListener(nextButtonListener);
    }

    private void updateTitle(){
        if (getCurrentCard() != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(getString(R.string.total_text) + ": " + totalCardCount + " ");
            sb.append(getString(R.string.id_text) + ": " + getCurrentCard().getId() + " ");
            sb.append(getString(R.string.ordinal_text_short) + ": " + getCurrentCard().getOrdinal() + " ");
            sb.append(getCurrentCard().getCategory().getName());
            setSmallTitle(sb.toString());
        }
    }

    private void gotoNext(){
        if (getCurrentCard() != null) {
            Card nextCard = getDbOpenHelper().getCardDao().queryNextCard(getCurrentCard(), currentCategory);

            assert nextCard != null : "Next card is null";
            gotoCard(nextCard);
        }
    }

    protected void gotoCard(Card card) {
        Preconditions.checkNotNull(card);

        setCurrentCard(card);

        updateCardFrontSide();
        updateTitle();
    }

    private void deleteCard(final Card cardToDelete){
        Preconditions.checkNotNull(cardToDelete);
        new AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_text))
            .setMessage(getString(R.string.delete_warning))
            .setPositiveButton(getString(R.string.yes_text),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            DeleteCardTask task = new DeleteCardTask(new WeakReference<>(PreviewEditActivity.this));
                            task.execute((Void)null);
                        }
                    })
        .setNegativeButton(getString(R.string.no_text), null)
            .create()
            .show();
    }

    protected void gotoPrev(){
        if (getCurrentCard() != null) {
            Card prevCard = getDbOpenHelper().getCardDao().queryPrevCard(getCurrentCard(), currentCategory);

            assert prevCard != null : "Prev card is null";
            gotoCard(prevCard);
            updateTitle();
        }
    }

    /*
     * Show the front side of the current card
     * This method is called instead directly update the flashcard
     * so both single and double sided card will work.
     */
    private void updateCardFrontSide(){
        if(getCurrentCard() != null){
            if(getSetting().getCardStyle() == Setting.CardStyle.DOUBLE_SIDED){
                /* Double sided card, show front */
                displayCard(false);
            } else {
                /* Single sided, show both answer and questjion. */
                displayCard(true);
            }
        }
    }


    private void searchCard(String text) {
        Preconditions.checkNotNull(text);
        SearchCardTask task = new SearchCardTask();
        task.execute(text);
    }

    private View.OnClickListener prevButtonListener = new View.OnClickListener(){
        public void onClick(View v){
            gotoPrev();
            getCardTTSUtil().stopSpeak();
        }
    };

    private View.OnClickListener nextButtonListener = new View.OnClickListener(){
        public void onClick(View v){
            gotoNext();
            getCardTTSUtil().stopSpeak();
        }
    };

    private View.OnClickListener newButtonListener = new View.OnClickListener(){
        public void onClick(View v) {
            Intent myIntent = new Intent(PreviewEditActivity.this, CardEditor.class);
            myIntent.putExtra(CardEditor.EXTRA_DBPATH, dbPath);
            if (getCurrentCard() != null) {
                myIntent.putExtra(CardEditor.EXTRA_CARD_ID, getCurrentCard().getId());
            }
            myIntent.putExtra(CardEditor.EXTRA_IS_EDIT_NEW, true);
            startActivityForResult(myIntent, ACTIVITY_EDIT);
        }
    };

    private View.OnClickListener editButtonListener = new View.OnClickListener(){
        public void onClick(View v){
            if (getCurrentCard() != null) {
                Intent myIntent = new Intent(PreviewEditActivity.this, CardEditor.class);
                myIntent.putExtra(CardEditor.EXTRA_DBPATH, dbPath);
                myIntent.putExtra(CardEditor.EXTRA_CARD_ID, getCurrentCard().getId());
                myIntent.putExtra(CardEditor.EXTRA_IS_EDIT_NEW, false);
                startActivityForResult(myIntent, ACTIVITY_EDIT);
            }
        }
    };



    private static class DeleteCardTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;

        private WeakReference<PreviewEditActivity> activityRef;

        public DeleteCardTask(@NonNull WeakReference<PreviewEditActivity> activityRef) {
            this.activityRef = activityRef;
        }


        @Override
        public void onPreExecute() {
            PreviewEditActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            progressDialog = new ProgressDialog(activity);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle(activity.getString(R.string.loading_please_wait));
            progressDialog.setMessage(activity.getString(R.string.loading_database));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        @Override
        public Void doInBackground(Void... params) {
            PreviewEditActivity activity = activityRef.get();
            if (activity == null) {
                return null;
            }

            Card delCard = activity.getCurrentCard();
            activity.setCurrentCard(activity.getDbOpenHelper().getCardDao().queryNextCard(activity.getCurrentCard(), activity.currentCategory));
            activity.getDbOpenHelper().getCardDao().delete(delCard);
            return null;
        }
        @Override
        public void onPostExecute(Void result){
            PreviewEditActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            activity.restartActivity();
        }
    }

    /*
     * params is the queyr text
     * Search Method should be in SearchMethod enum.
     */
    private class SearchCardTask extends AsyncTask<String, Void, Card> {

        private ProgressDialog progressDialog;

        @Override
        public void onPreExecute() {
            progressDialog = new ProgressDialog(PreviewEditActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle(getString(R.string.loading_please_wait));
            progressDialog.setMessage(getString(R.string.loading_database));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        public Card doInBackground(String... params) {
            String text = params[0];
            assert text != null : "Pass null criteria to SearchCardTask";

            Card foundCard = null;

            // Search #123 for id 123
            if (Pattern.matches(SEARCH_BY_ID_PATTERN, text)) {
                int id = Integer.valueOf(text.substring(1));
                foundCard = getDbOpenHelper().getCardDao().queryForId(id);
            }

            // Search normal cards
            if (foundCard == null) {
                // Search normal text
                if (!text.contains("*")) {
                    text = "*" + text + "*";
                }
                // Convert to SQL wildcard
                text = text.replace("*", "%");
                text = text.replace("?", "_");

                // First try to search from the current place
                foundCard = getDbOpenHelper().getCardDao().searchNextCard(text, getCurrentCard().getOrdinal());
            }
            // If not found, search from the first card
            if (foundCard == null) {
                foundCard = getDbOpenHelper().getCardDao().searchNextCard(text, 0);
            }

            return foundCard;
        }

        @Override
        public void onPostExecute(Card result){
            progressDialog.dismiss();
            if (result == null) {
                return;
            }
            setCurrentCard(result);
            updateCardFrontSide();
            updateTitle();
        }
    }
    // Invoked when the search action bar is used to search cards
    SearchView.OnQueryTextListener onQueryTextChangedListener = new SearchView.OnQueryTextListener() {

        @Override
        public boolean onQueryTextChange(String text) {
            return true;
        }

        @Override
        public boolean onQueryTextSubmit(String text) {
            searchCard(text);
            return true;
        }

    };
}
