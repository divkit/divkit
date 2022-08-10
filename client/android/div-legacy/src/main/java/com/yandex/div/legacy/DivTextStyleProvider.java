package com.yandex.div.legacy;

import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.alicekit.core.utils.Assert;
import com.yandex.alicekit.core.widget.TypefaceProvider;
import com.yandex.div.DivCustomTextStyle;
import com.yandex.div.DivTextStyle;
import com.yandex.div.legacy.view.TextStyle;
import javax.inject.Inject;
import javax.inject.Provider;

public class DivTextStyleProvider {

    @NonNull
    private final TextStyle mNumbersS;
    @NonNull
    private final TextStyle mNumbersM;
    @NonNull
    private final TextStyle mNumbersL;
    @NonNull
    private final TextStyle mTitleS;
    @NonNull
    private final TextStyle mTitleM;
    @NonNull
    private final TextStyle mTitleL;
    @NonNull
    private final TextStyle mTextS;
    @NonNull
    private final TextStyle mTextM;
    @NonNull
    private final TextStyle mTextL;
    @NonNull
    private final TextStyle mButton;
    @NonNull
    private final TextStyle mCardHeader;
    @NonNull
    private final TextStyle mTextMMedium;
    @NonNull
    private final TypefaceProvider mTypefaceProvider;
    @NonNull
    private final Provider<Typeface> mLight;
    @NonNull
    private final Provider<Typeface> mRegular;
    @NonNull
    private final Provider<Typeface> mMedium;

    @Inject
    public DivTextStyleProvider(@NonNull TypefaceProvider typefaceProvider) {
        mTypefaceProvider = typefaceProvider;

        mLight = mTypefaceProvider::getLight;
        mRegular = mTypefaceProvider::getRegularLegacy;
        mMedium = mTypefaceProvider::getMedium;

        mNumbersS = new TextStyle.Builder().setTypefaceProvider(mRegular)
                .setColor(R.color.div_text_dark_disabled_80)
                .setTextSizeDimenRes(R.dimen.div_style_text_size_numbers_s)
                .setLetterSpacingDimenRes(R.dimen.div_style_text_letter_spacing_numbers_s)
                .setLineSpaceExtra(R.dimen.div_style_text_line_space_extra_numbers_s)
                .createTextStyle();

        mNumbersM = new TextStyle.Builder().setTypefaceProvider(mRegular)
                .setColor(R.color.div_text_dark_disabled_80)
                .setTextSizeDimenRes(R.dimen.div_style_text_size_numbers_m)
                .setLetterSpacingDimenRes(R.dimen.div_style_text_letter_spacing_numbers_m)
                .setLineSpaceExtra(R.dimen.div_style_text_line_space_extra_numbers_m)
                .createTextStyle();

        mNumbersL = new TextStyle.Builder().setTypefaceProvider(mLight)
                .setColor(R.color.div_text_dark_disabled_80)
                .setTextSizeDimenRes(R.dimen.div_style_text_size_numbers_l)
                .setLetterSpacingDimenRes(R.dimen.div_style_text_letter_spacing_numbers_l)
                .setLineSpaceExtra(R.dimen.div_style_text_line_space_extra_numbers_l)
                .createTextStyle();

        mTitleS = new TextStyle.Builder().setTypefaceProvider(mMedium)
                .setColor(R.color.div_text_dark_disabled_80)
                .setTextSizeDimenRes(R.dimen.div_style_text_size_title_s)
                .setLetterSpacingDimenRes(R.dimen.div_style_text_letter_spacing_no)
                .setLineSpaceExtra(R.dimen.div_style_text_line_space_extra_title_s)
                .createTextStyle();

        mTitleM = new TextStyle.Builder().setTypefaceProvider(mMedium)
                .setColor(R.color.div_text_dark_disabled_80)
                .setTextSizeDimenRes(R.dimen.div_style_text_size_title_m)
                .setLetterSpacingDimenRes(R.dimen.div_style_text_letter_spacing_no)
                .setLineSpaceExtra(R.dimen.div_style_text_line_space_extra_title_m)
                .createTextStyle();

        mTitleL = new TextStyle.Builder().setTypefaceProvider(mMedium)
                .setColor(R.color.div_text_dark_disabled_80)
                .setTextSizeDimenRes(R.dimen.div_style_text_size_title_l)
                .setLetterSpacingDimenRes(R.dimen.div_style_text_letter_spacing_no)
                .setLineSpaceExtra(R.dimen.div_style_text_line_space_extra_title_l)
                .createTextStyle();

        mTextS = new TextStyle.Builder().setTypefaceProvider(mRegular)
                .setColor(R.color.div_text_dark_disabled_80)
                .setTextSizeDimenRes(R.dimen.div_style_text_size_text_s)
                .setLetterSpacingDimenRes(R.dimen.div_style_text_letter_spacing_no)
                .setLineSpaceExtra(R.dimen.div_style_text_line_space_extra_text_s)
                .createTextStyle();

        mTextM = new TextStyle.Builder().setTypefaceProvider(mRegular)
                .setColor(R.color.div_text_dark_disabled_80)
                .setTextSizeDimenRes(R.dimen.div_style_text_size_text_m)
                .setLetterSpacingDimenRes(R.dimen.div_style_text_letter_spacing_no)
                .setLineSpaceExtra(R.dimen.div_style_text_line_space_extra_text_m)
                .createTextStyle();

        mTextL = new TextStyle.Builder().setTypefaceProvider(mRegular)
                .setColor(R.color.div_text_dark_disabled_80)
                .setTextSizeDimenRes(R.dimen.div_style_text_size_text_l)
                .setLetterSpacingDimenRes(R.dimen.div_style_text_letter_spacing_no)
                .setLineSpaceExtra(R.dimen.div_style_text_line_space_extra_text_l)
                .createTextStyle();

        mButton = new TextStyle.Builder().setTypefaceProvider(mMedium)
                .setColor(R.color.div_text_dark_disabled_50)
                .setTextSizeDimenRes(R.dimen.div_style_text_size_button)
                .setLetterSpacingDimenRes(R.dimen.div_style_text_letter_spacing_button)
                .setLineSpaceExtra(R.dimen.div_style_text_line_space_extra_no)
                .createTextStyle();

        mCardHeader = new TextStyle.Builder().setTypefaceProvider(mMedium)
                .setColor(R.color.div_text_dark_disabled_40)
                .setTextSizeDimenRes(R.dimen.div_style_text_size_card_header)
                .setLetterSpacingDimenRes(R.dimen.div_style_text_letter_spacing_card_header)
                .setLineSpaceExtra(R.dimen.div_style_text_line_space_extra_card_header)
                .createTextStyle();

        mTextMMedium = new TextStyle.Builder().setTypefaceProvider(mMedium)
                .setColor(R.color.div_text_dark_disabled_80)
                .setTextSizeDimenRes(R.dimen.div_style_text_size_text_m)
                .setLetterSpacingDimenRes(R.dimen.div_style_text_letter_spacing_no)
                .setLineSpaceExtra(R.dimen.div_style_text_line_space_extra_text_m)
                .createTextStyle();
    }

    @NonNull
    private static Typeface selectWithDefault(Typeface first, @NonNull Typeface second) {
        Assert.assertNotNull(first);
        return first == null ? second : first;
    }

    @NonNull
    public TypefaceProvider getTypefaceProvider() {
        return mTypefaceProvider;
    }

    @NonNull
    public TextStyle getNumbersS() {
        return mNumbersS;
    }

    @NonNull
    public TextStyle getNumbersM() {
        return mNumbersM;
    }

    @NonNull
    public TextStyle getNumbersL() {
        return mNumbersL;
    }

    @NonNull
    public TextStyle getTitleS() {
        return mTitleS;
    }

    @NonNull
    public TextStyle getTitleM() {
        return mTitleM;
    }

    @NonNull
    public TextStyle getTitleL() {
        return mTitleL;
    }

    @NonNull
    public TextStyle getTextS() {
        return mTextS;
    }

    @NonNull
    public TextStyle getTextM() {
        return mTextM;
    }

    @NonNull
    public TextStyle getTextL() {
        return mTextL;
    }

    @NonNull
    public TextStyle getTextMMedium() {
        return mTextMMedium;
    }

    @NonNull
    public TextStyle getCardHeader() {
        return mCardHeader;
    }

    @NonNull
    public TextStyle getButton() {
        return mButton;
    }

    @NonNull
    public TextStyle getTextStyle(@NonNull @DivTextStyle String divTextStyle) {
        switch (divTextStyle) {
            case DivTextStyle.TEXT_S:
                return mTextS;
            case DivTextStyle.TEXT_M:
                return mTextM;
            case DivTextStyle.TEXT_L:
                return mTextL;
            case DivTextStyle.TEXT_M_MEDIUM:
                return mTextMMedium;
            case DivTextStyle.TITLE_S:
                return mTitleS;
            case DivTextStyle.TITLE_M:
                return mTitleM;
            case DivTextStyle.TITLE_L:
                return mTitleL;
            case DivTextStyle.NUMBERS_S:
                return mNumbersS;
            case DivTextStyle.NUMBERS_M:
                return mNumbersM;
            case DivTextStyle.NUMBERS_L:
                return mNumbersL;
            case DivTextStyle.CARD_HEADER:
                return mCardHeader;
            case DivTextStyle.BUTTON:
                return mButton;
            default:
                Assert.fail("Unknown text style: " + divTextStyle);
                return mTextM;
        }
    }

    @NonNull
    public TextStyle getCustomTextStyle(@NonNull DivCustomTextStyle customTextStyle) {
        TextStyle.Builder builder = new TextStyle.Builder();
        builder.setTypefaceProvider(getTypefaceProvider(customTextStyle.fontStyle))
            .setLetterSpacing(customTextStyle.letterSpacing);

        return builder.createTextStyle();
    }

    @NonNull
    private Provider<Typeface> getTypefaceProvider(@DivCustomTextStyle.FontStyle String fontStyle) {
        switch (fontStyle) {
            case DivCustomTextStyle.FontStyle.LIGHT:
                return mLight;
            case DivCustomTextStyle.FontStyle.MEDIUM:
                return mMedium;
            case DivCustomTextStyle.FontStyle.REGULAR:
                return mRegular;
            default:
                Assert.fail("Unsupported font style " + fontStyle);
                return mRegular;
        }
    }

    @NonNull
    public TextStyle getTextStyle(@NonNull @DivTextStyle String divTextStyle, @Nullable Integer maxLines) {
        TextStyle baseStyle = getTextStyle(divTextStyle);
        return maxLines == null ? baseStyle : new TextStyle(baseStyle, maxLines);
    }
}
