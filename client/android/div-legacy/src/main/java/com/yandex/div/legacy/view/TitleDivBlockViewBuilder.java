package com.yandex.div.legacy.view;

import android.content.Context;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import com.yandex.div.DivTitleBlock;
import com.yandex.div.legacy.DivAutoLogger;
import com.yandex.div.legacy.DivTextStyleProvider;
import com.yandex.div.legacy.R;
import com.yandex.div.legacy.dagger.DivLegacyScope;
import com.yandex.div.legacy.dagger.LegacyNames;
import com.yandex.div.view.menu.OverflowMenuWrapper;
import com.yandex.div.view.pooling.ViewPool;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

@DivLegacyScope
public class TitleDivBlockViewBuilder extends DivElementDataViewBuilder<DivTitleBlock> {

    private static final String FACTORY_TAG_TITLE = "TitleDivBlockViewBuilder.TITLE";

    @NonNull
    private final Context mThemedContext;
    @NonNull
    private final ViewPool mViewPool;
    @NonNull
    private final DivTextStyleProvider mTextStyleProvider;
    @NonNull
    private final DivAutoLogger mAutoLogger;

    @Inject
    TitleDivBlockViewBuilder(@NonNull @Named(LegacyNames.THEMED_CONTEXT) Context themedContext,
                             @NonNull ViewPool viewPool,
                             @NonNull DivTextStyleProvider textStyleProvider,
                             @NonNull DivAutoLogger autoLogger,
                             @NonNull TextViewFactory textViewFactory) {
        super();
        mThemedContext = themedContext;
        mViewPool = viewPool;
        mTextStyleProvider = textStyleProvider;
        mAutoLogger = autoLogger;

        mViewPool.register(FACTORY_TAG_TITLE, () -> createTextView(textViewFactory,
                                                                   mThemedContext,
                                                                   R.attr.legacyTitleStyle,
                                                                   R.id.div_title_text), 8);
    }

    @Override
    @NonNull
    protected View build(@NonNull DivView divView, @NonNull DivTitleBlock divData) {
        AppCompatTextView titleView = mViewPool.obtain(FACTORY_TAG_TITLE);

        CharSequence text = divData.text;
        boolean hasText = text != null;
        if (hasText) {
            setTextAndStyle(titleView, text, mTextStyleProvider.getTextStyle(divData.textStyle));
        }

        List<DivTitleBlock.MenuItem> menuItems = divData.menuItems;
        if (menuItems == null || menuItems.isEmpty() || !divView.getConfig().isContextMenuEnabled()) {
            return titleView;
        }

        @ColorInt Integer menuColor = divData.menuColor;
        final OverflowMenuWrapper overflowMenuWrapper = new OverflowMenuWrapper(mThemedContext, titleView, divView,
                                                                                R.dimen.div_title_menu_padding,
                                                                                R.dimen.div_title_menu_padding)
                .buttonResourceId(R.drawable.overflow_menu_button)
                .color(menuColor)
                .listener(new MenuWrapperListener(divView, menuItems))
                .overflowGravity(Gravity.RIGHT | Gravity.TOP);
        if (hasText) {
            overflowMenuWrapper.horizontallyCompetingViews(titleView);
        }
        divView.subscribe(overflowMenuWrapper::dismiss);
        return overflowMenuWrapper.getView();
    }

    private class MenuWrapperListener extends OverflowMenuWrapper.Listener.Simple {

        @NonNull
        private final DivView mDivView;
        @NonNull
        private final List<DivTitleBlock.MenuItem> mItems;

        MenuWrapperListener(@NonNull DivView divView, @NonNull List<DivTitleBlock.MenuItem> items) {
            mDivView = divView;
            mItems = items;
        }

        @Override
        public void onMenuCreated(@NonNull PopupMenu popupMenu) {
            Menu menu = popupMenu.getMenu();
            for (DivTitleBlock.MenuItem itemData : mItems) {
                int itemPosition = menu.size();

                MenuItem menuItem = menu.add(itemData.text);
                menuItem.setOnMenuItemClickListener(item -> {
                    mDivView.handleUri(itemData.url);
                    mAutoLogger.logPopupMenuItemClick(mDivView, itemPosition, itemData.text, itemData.url);
                    return true;
                });
            }
        }
    }
}
