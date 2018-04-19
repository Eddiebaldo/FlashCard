package org.liberty.android.fantastischmemo.modules;


import android.support.v4.app.Fragment;


import org.liberty.android.fantastischmemo.ui.CardFragment;
import org.liberty.android.fantastischmemo.ui.CardPlayerFragment;
import org.liberty.android.fantastischmemo.ui.ConverterFragment;
import org.liberty.android.fantastischmemo.ui.FileBrowserFragment;
import org.liberty.android.fantastischmemo.ui.GradeButtonsFragment;
import org.liberty.android.fantastischmemo.ui.OpenActionsFragment;
import org.liberty.android.fantastischmemo.ui.RecentListFragment;

import dagger.BindsInstance;
import dagger.Subcomponent;

@PerFragment
@Subcomponent
public interface FragmentComponents {

    void inject(RecentListFragment fragment);

    void inject(FileBrowserFragment fragment);

    void inject(GradeButtonsFragment fragment);

    void inject(OpenActionsFragment fragment);

    void inject(CardPlayerFragment fragment);

    void inject(ConverterFragment fragment);

    void inject(CardFragment fragment);

    @Subcomponent.Builder
    interface Builder {

        @BindsInstance
        FragmentComponents.Builder fragment(Fragment fragment);

        FragmentComponents build();
    }
}
