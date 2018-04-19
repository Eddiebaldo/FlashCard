package org.liberty.android.fantastischmemo.modules;

import org.liberty.android.fantastischmemo.common.BaseActivity;
import org.liberty.android.fantastischmemo.ui.AnyMemo;
import org.liberty.android.fantastischmemo.ui.CardEditor;
import org.liberty.android.fantastischmemo.ui.CardListActivity;
import org.liberty.android.fantastischmemo.ui.DatabaseMerger;
import org.liberty.android.fantastischmemo.ui.PreviewEditActivity;
import org.liberty.android.fantastischmemo.ui.QACardActivity;
import org.liberty.android.fantastischmemo.ui.ShareScreen;
import org.liberty.android.fantastischmemo.ui.StudyActivity;

import javax.inject.Provider;

import dagger.BindsInstance;
import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = {ActivityModules.class})
public interface ActivityComponents {

    void inject(AnyMemo activity);

    void inject(QACardActivity activity);

    void inject(StudyActivity activity);

    void inject(PreviewEditActivity activity);

    void inject(CardEditor activity);

    void inject(CardListActivity activity);

    void inject(DatabaseMerger activity);

    void inject(ShareScreen activity);


    Provider<FragmentComponents.Builder> fragmentsComponentsBuilder();

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        ActivityComponents.Builder activity(BaseActivity activity);

        ActivityComponents build();
    }
}
