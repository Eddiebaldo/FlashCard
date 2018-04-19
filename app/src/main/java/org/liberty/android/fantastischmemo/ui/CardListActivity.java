/*
Copyright (C) 2012 Haowen Ning, Xinxin Wang

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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.google.common.base.Strings;

import org.apache.commons.io.FilenameUtils;
import org.liberty.android.fantastischmemo.R;
import org.liberty.android.fantastischmemo.common.AMEnv;
import org.liberty.android.fantastischmemo.common.AMPrefKeys;
import org.liberty.android.fantastischmemo.common.AnyMemoDBOpenHelper;
import org.liberty.android.fantastischmemo.common.AnyMemoDBOpenHelperManager;
import org.liberty.android.fantastischmemo.common.BaseActivity;
import org.liberty.android.fantastischmemo.entity.Card;
import org.liberty.android.fantastischmemo.entity.LearningData;
import org.liberty.android.fantastischmemo.entity.SchedulingAlgorithmParameters;
import org.liberty.android.fantastischmemo.scheduler.Scheduler;
import org.liberty.android.fantastischmemo.ui.loader.CardWrapperListLoader;
import org.liberty.android.fantastischmemo.ui.loader.MultipleLoaderManager;
import org.liberty.android.fantastischmemo.utils.AMFileUtil;
import org.liberty.android.fantastischmemo.utils.AMPrefUtil;
import org.liberty.android.fantastischmemo.utils.CardTextUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

/**
 * The Card List activity is used for listing all cards in a db or learning in a list mode.
 */
public class CardListActivity extends BaseActivity {

    public static String EXTRA_DBPATH = "dbpath";

    private static final int CARD_WRAPPER_LOADER_ID = 0;

    private static final int LEARNED_CARD_ITEM_COLOR = 0x4F00FF00;
    private static final int REVIEW_CARD_ITEM_COLOR = 0x4FFFFF00;

    private String dbPath;

    private CardListAdapter cardListAdapter;

    private AnyMemoDBOpenHelper dbOpenHelper;

    private ListView listView;

    private CardTextUtil cardTextUtil;

    private boolean initialAnswerVisible = true;

    /**
     * This needs to be defined before onCreate so in onCreate, all loaders will
     * be registered with the right manager.
     */
    @Inject MultipleLoaderManager multipleLoaderManager;

    @Inject Scheduler scheduler;

    @Inject AMPrefUtil amPrefUtil;

    @Inject SchedulingAlgorithmParameters schedulingAlgorithmParameters;

    @Inject AMFileUtil amFileUtil;

    public void onCreate(Bundle savedInstanceState) {
        activityComponents().inject(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.card_list);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dbPath = extras.getString(CardListActivity.EXTRA_DBPATH);
        }
        dbOpenHelper = AnyMemoDBOpenHelperManager.getHelper(
                CardListActivity.this, dbPath);

        initialAnswerVisible = amPrefUtil.getSavedBoolean(
                AMPrefKeys.LIST_ANSWER_VISIBLE_PREFIX, dbPath, true);

        listView = (ListView) findViewById(R.id.item_list);

        String[] imageSearchPaths = {
            /* Relative path */
            "",
            /* Relative path with db name */
            "" + FilenameUtils.getName(dbPath),
            /* Try the image in /sdcard/anymemo/images/dbname/ */
            AMEnv.DEFAULT_IMAGE_PATH + FilenameUtils.getName(dbPath),
            /* Try the image in /sdcard/anymemo/images/ */
            AMEnv.DEFAULT_IMAGE_PATH,
        };

        cardTextUtil = new CardTextUtil(getApplicationContext(), amFileUtil, imageSearchPaths);

        // Use loader to load the cards.
        multipleLoaderManager.registerLoaderCallbacks(CARD_WRAPPER_LOADER_ID, new CardWrapperLoaderCallbacks(), false);
        multipleLoaderManager.setOnAllLoaderCompletedRunnable(onPostInitRunnable);
        multipleLoaderManager.startLoading();
    }

    @Override
    public void onDestroy() {
        amPrefUtil.putSavedInt(AMPrefKeys.LIST_EDIT_SCREEN_PREFIX, dbPath,
                listView.getFirstVisiblePosition());
        AnyMemoDBOpenHelperManager.releaseHelper(dbOpenHelper);
        super.onDestroy();
    }

    @Override
    public void restartActivity() {
        Intent myIntent = new Intent(this, CardListActivity.class);
        assert dbPath != null : "Use null dbPath to restartAcitivity";
        myIntent.putExtra(EXTRA_DBPATH, dbPath);
        finish();
        startActivity(myIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Refresh activity when the data has been changed.
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        } else {
            restartActivity();
        }
    }


    private void gotoCardEditorActivity(Card card) {
        Intent intent = new Intent(this, CardEditor.class);
        intent.putExtra(CardEditor.EXTRA_DBPATH, dbPath);
        intent.putExtra(CardEditor.EXTRA_CARD_ID, card.getId());
        startActivity(intent);
    }


    private void gotoPreviewEditActivity(Card card) {
        Intent intent = new Intent(this, PreviewEditActivity.class);
        intent.putExtra(PreviewEditActivity.EXTRA_DBPATH, dbPath);
        intent.putExtra(PreviewEditActivity.EXTRA_CARD_ID, card.getId());
        startActivity(intent);
    }

    private void markAsLearned(Card card) {
        LearningData newLd = scheduler.schedule(card.getLearningData(), 5,
                schedulingAlgorithmParameters.getEnableNoise());
        dbOpenHelper.getLearningDataDao().updateLearningData(newLd);
        card.setLearningData(newLd);
        cardListAdapter.notifyDataSetChanged();
    }

    private void markAsForgotten(Card card) {
        LearningData newLd = scheduler.schedule(card.getLearningData(), 1,
                schedulingAlgorithmParameters.getEnableNoise());
        dbOpenHelper.getLearningDataDao().updateLearningData(newLd);
        card.setLearningData(newLd);
        cardListAdapter.notifyDataSetChanged();
    }

    private void markAsNew(Card card) {
        dbOpenHelper.getLearningDataDao().resetLearningData(
                card.getLearningData());
        cardListAdapter.notifyDataSetChanged();
    }

    private void markAsLearnedForever(Card card) {
        dbOpenHelper.getLearningDataDao().markAsLearnedForever(
                card.getLearningData());
        cardListAdapter.notifyDataSetChanged();
    }

    private void highlightCardViewAsLearned(View view) {
        // Light green color
        view.setBackgroundColor(LEARNED_CARD_ITEM_COLOR);
    }

    private void highlightCardViewAsForgotten(View view) {
        // Light yellow color
        view.setBackgroundColor(REVIEW_CARD_ITEM_COLOR);
    }

    // Need to maintain compatibility with Android 2.3
    @SuppressWarnings("deprecation")
    private void highlightCardViewAsNew(View view) {
        // The default background color for individual views is the same as the list view's
        view.setBackgroundDrawable(listView.getBackground());
    }

    // Toggle the visibility of all the answers
    private void showHideAnswers() {
        if (initialAnswerVisible) {
            for (CardWrapper wrapper : cardListAdapter) {
                wrapper.setAnswerVisible(false);
            }
            initialAnswerVisible = false;
        } else {
            for (CardWrapper wrapper : cardListAdapter) {
                wrapper.setAnswerVisible(true);
            }
            initialAnswerVisible = true;
        }
        amPrefUtil.putSavedBoolean(AMPrefKeys.LIST_ANSWER_VISIBLE_PREFIX, dbPath, initialAnswerVisible);
        cardListAdapter.notifyDataSetChanged();
    }

    private void showSortListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //items in enum SortMethod and array sort_by_options_values should have the same order
        String defaultItem = getResources().getStringArray(
                R.array.sort_by_options_values)[0];
        String savedMethod = amPrefUtil.getSavedString(
                AMPrefKeys.LIST_SORT_BY_METHOD_PREFIX, dbPath, defaultItem);
        builder.setSingleChoiceItems(R.array.sort_by_options,
                SortMethod.valueOf(savedMethod).ordinal(),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] items = getResources().getStringArray(
                                R.array.sort_by_options_values);
                        sortList(SortMethod.valueOf(items[which]));
                        amPrefUtil.putSavedString(
                                AMPrefKeys.LIST_SORT_BY_METHOD_PREFIX, dbPath,
                                items[which]);
                        dialog.dismiss();
                    }
                }).show();
    }

    private void sortList(SortMethod sort) {
        //Handle sort method
        switch (sort) {
        case ORDINAL:
            cardListAdapter.sort(new Comparator<CardWrapper>() {
                @Override
                public int compare(CardWrapper c1, CardWrapper c2) {
                    return c1.getCard().getOrdinal()
                            - c2.getCard().getOrdinal();
                };
            });
            break;
        case QUESTION:
            cardListAdapter.sort(new Comparator<CardWrapper>() {
                @Override
                public int compare(CardWrapper c1, CardWrapper c2) {
                    return c1.getCard().getQuestion()
                            .compareTo(c2.getCard().getQuestion());
                };
            });
            break;
        case ANSWER:
            cardListAdapter.sort(new Comparator<CardWrapper>() {
                @Override
                public int compare(CardWrapper c1, CardWrapper c2) {
                    return c1.getCard().getAnswer()
                            .compareTo(c2.getCard().getAnswer());
                };
            });
            break;
        default:
            throw new AssertionError(
                    "This case will not happen! Or the system has carshed.");
        }
    }

    SearchView.OnQueryTextListener onQueryTextChangedListener = new SearchView.OnQueryTextListener() {

        @Override
        public boolean onQueryTextChange(String text) {
            // This is used to make sure user can clear the search result
            // and show all cards easily.
            if (Strings.isNullOrEmpty(text)) {
                cardListAdapter.getFilter().filter(text);
            }
            return true;
        }

        @Override
        public boolean onQueryTextSubmit(String text) {
            cardListAdapter.getFilter().filter(text);
            return true;
        }

    };


    private class CardListAdapter extends ArrayAdapter<CardWrapper> implements
            SectionIndexer, Iterable<CardWrapper> {
        /* quick index sections */
        private String[] sections;

        private List<CardWrapper> cardList = null;
        // As soon as filter is used card list this keeps all the origian cards
        private List<CardWrapper> originalCardList = null;

        public CardListAdapter(Context context, List<CardWrapper> cards) {
            super(context, 0, cards);
            cardList = cards;

            int sectionSize = getCount() / 100;
            sections = new String[sectionSize];
            for (int i = 0; i < sectionSize; i++) {
                sections[i] = String.valueOf(i * 100);
            }
        }

        public Card getCardItem(int position) {
            return getItem(position).getCard();
        }

        /* Display the quick index when the user is scrolling */
        @Override
        public int getPositionForSection(int section) {
            return section * 100;
        }

        @Override
        public int getSectionForPosition(int position) {
            return 1;
        }

        @Override
        public Object[] getSections() {
            return sections;
        }

        // Used to implement the search function for the card list
        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence searchTerm) {
                    FilterResults results = new FilterResults();

                    // Back up the original card list when called for the first time
                    if (originalCardList == null) {
                        originalCardList = new ArrayList<CardWrapper>(cardList);
                    }

                    // If empty term is gived, restore to the original list
                    if (searchTerm == null || Strings.isNullOrEmpty(searchTerm.toString())) {
                        List<CardWrapper> list = new ArrayList<CardWrapper>(
                                originalCardList);
                        results.values = list;
                        results.count = list.size();
                    } else {
                        List<CardWrapper> resultList = new ArrayList<CardWrapper>();

                        for (CardWrapper cardWrapper : cardList) {
                            Card card = cardWrapper.getCard();
                            if (card.getQuestion().toLowerCase().contains(searchTerm.toString().toLowerCase()) || 
                                    card.getAnswer() .toLowerCase().contains(searchTerm.toString().toLowerCase())) {
                                resultList.add(cardWrapper);
                            }
                        }

                        results.values = resultList;
                        results.count = resultList.size();
                    }

                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint,
                        FilterResults results) {
                    //noinspection unchecked
                    cardList.clear();

                    @SuppressWarnings("unchecked")
                    List<CardWrapper> values = (List<CardWrapper>) results.values;

                    cardList.addAll(values);
                    if (results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
        }

        @Override
        public Iterator<CardWrapper> iterator() {
            return cardList.iterator();
        }

    }

    /**
     * Wrap up the card with the answer visibility information.
     */
    public static class CardWrapper {

        private Card card;

        private boolean answerVisible;

        public CardWrapper(Card card, boolean visible) {
            this.card = card;
            this.answerVisible = visible;
        }

        public Card getCard() {
            return card;
        }

        public boolean isAnswerVisible() {
            return answerVisible;
        }

        public void setAnswerVisible(boolean visible) {
            this.answerVisible = visible;
        }

    }

    private class CardWrapperLoaderCallbacks implements
            LoaderManager.LoaderCallbacks<List<CardWrapper>> {
        @Override
        public Loader<List<CardWrapper>> onCreateLoader(int arg0, Bundle arg1) {
             CardWrapperListLoader loader = new CardWrapperListLoader(CardListActivity.this, dbPath, initialAnswerVisible);
             loader.forceLoad();
             return loader;
        }

        @Override
        public void onLoadFinished(Loader<List<CardWrapper>> loader , List<CardWrapper> result) {

            cardListAdapter = new CardListAdapter(CardListActivity.this, result);
            cardListAdapter.setNotifyOnChange(true);

            multipleLoaderManager.checkAllLoadersCompleted();
        }
        @Override
        public void onLoaderReset(Loader<List<CardWrapper>> arg0) {
            // Do nothing now
        }
    }

    private Runnable onPostInitRunnable = new Runnable() {

        @Override
        public void run() {
            int initPosition = amPrefUtil.getSavedInt(AMPrefKeys.LIST_EDIT_SCREEN_PREFIX, dbPath, 0);
            listView.setAdapter(cardListAdapter);
            listView.setSelection(initPosition);
            listView.setFastScrollEnabled(true);
            listView.setTextFilterEnabled(true);

            String savedMethod = amPrefUtil.getSavedString(AMPrefKeys.LIST_SORT_BY_METHOD_PREFIX, dbPath, getResources().getStringArray(R.array.sort_by_options_values)[0]);

            sortList(SortMethod.valueOf(savedMethod));
        }
    };

    private enum SortMethod {
        ORDINAL,
        QUESTION,
        ANSWER};
}
