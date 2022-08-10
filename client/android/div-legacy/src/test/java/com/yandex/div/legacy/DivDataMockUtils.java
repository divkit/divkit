package com.yandex.div.legacy;

import android.graphics.Color;
import android.net.Uri;
import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.div.DivAction;
import com.yandex.div.DivAlignment;
import com.yandex.div.DivBackground;
import com.yandex.div.DivBaseBlock;
import com.yandex.div.DivButtonsBlock;
import com.yandex.div.DivData;
import com.yandex.div.DivGradientBackground;
import com.yandex.div.DivImageBackground;
import com.yandex.div.DivImageElement;
import com.yandex.div.DivPaddingModifier;
import com.yandex.div.DivPosition;
import com.yandex.div.DivPredefinedSize;
import com.yandex.div.DivSeparatorBlock;
import com.yandex.div.DivSize;
import com.yandex.div.DivSizeTrait;
import com.yandex.div.DivSolidBackground;
import com.yandex.div.DivTextStyle;
import com.yandex.div.DivTitleBlock;
import com.yandex.div.DivUniversalBlock;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.robolectric.util.ReflectionHelpers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DivDataMockUtils {

    // Mocks for backgrounds

    public static DivBackground createSolidBackground(@ColorInt int color) {
        final DivBackground solidBackground = mock(DivBackground.class);
        DivSolidBackground background = mock(DivSolidBackground.class);
        when(solidBackground.asDivSolidBackground()).thenReturn(background);
        ReflectionHelpers.setField(background, "color", color);

        return solidBackground;
    }

    public static DivBackground createGradientBackground(@ColorInt int startColor, @ColorInt int endColor) {
        final DivBackground gradientBackground = mock(DivBackground.class);
        DivGradientBackground background = mock(DivGradientBackground.class);
        when(gradientBackground.asDivGradientBackground()).thenReturn(background);

        ReflectionHelpers.setField(background, "startColor", startColor);
        ReflectionHelpers.setField(background, "endColor", endColor);

        return gradientBackground;
    }

    public static DivBackground createImageBackground(String url) {
        final DivBackground imageBackground = mock(DivBackground.class);

        DivImageBackground background = mock(DivImageBackground.class);
        when(imageBackground.asDivImageBackground()).thenReturn(background);
        ReflectionHelpers.setField(background, "imageUrl", Uri.parse(url));

        return imageBackground;
    }

    // Mocks for DivData

    public static DivData createDivData(@Nullable List<DivBackground> backgroundList, @NonNull List<DivData.State> stateList,
                                        @NonNull DivSizeTrait predefinedSize) {
        final DivData divData = mock(DivData.class);

        ReflectionHelpers.setField(divData, "background", backgroundList);
        ReflectionHelpers.setField(divData, "states", stateList);
        ReflectionHelpers.setField(divData, "width", predefinedSize);

        return divData;
    }

    public static DivSizeTrait createDivPredefinedSize() {
        DivPredefinedSize predefinedSize;
        DivSizeTrait divSizeTrait = null;
        try {
            predefinedSize = new DivPredefinedSize(new JSONObject("{\n" +
                                                                  "\"value\": \"match_parent\",\n" +
                                                                  "\"type\": \"predefined\"\n" +
                                                                  "}"), null);
            divSizeTrait = new DivSizeTrait(predefinedSize.writeToJSON(), null);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return divSizeTrait;
    }

    public static DivData.State createDivState(int stateId, @Nullable DivAction action, @Nullable List<DivData.State.Block> blockList) {
        final DivData.State divState = mock(DivData.State.class);

        ReflectionHelpers.setField(divState, "stateId", stateId);
        ReflectionHelpers.setField(divState, "action", action);
        ReflectionHelpers.setField(divState, "blocks", blockList);

        return divState;
    }

    @NonNull
    public static DivPaddingModifier createPaddingModifier(@DivSize @Nullable final String size, @DivPosition String position) {
        final DivPaddingModifier paddingModifier = mock(DivPaddingModifier.class);
        ReflectionHelpers.setField(paddingModifier, "size", size);
        ReflectionHelpers.setField(paddingModifier, "position", position);
        return paddingModifier;
    }

    @NonNull
    public static DivAction createDivAction(@Nullable final String logId, @NonNull final String url) {
        final DivAction action = mock(DivAction.class);
        ReflectionHelpers.setField(action, "logId", logId);
        ReflectionHelpers.setField(action, "url", Uri.parse(url));
        return action;
    }

    public static DivData.State.Block createStateBlock(@NonNull @DivData.State.Block.Type String type,
                                                       @NonNull DivBaseBlock baseBlock) {
        DivData.State.Block block = mock(DivData.State.Block.class);

        when(block.asDivTitleBlock()).thenCallRealMethod();
        when(block.asDivButtonsBlock()).thenCallRealMethod();
        when(block.asDivFooterBlock()).thenCallRealMethod();
        when(block.asDivContainerBlock()).thenCallRealMethod();
        when(block.asDivTableBlock()).thenCallRealMethod();
        when(block.asDivGalleryBlock()).thenCallRealMethod();
        when(block.asDivImageBlock()).thenCallRealMethod();
        when(block.asDivSeparatorBlock()).thenCallRealMethod();
        when(block.asDivTrafficBlock()).thenCallRealMethod();
        when(block.asDivUniversalBlock()).thenCallRealMethod();

        ReflectionHelpers.setField(block, "value", baseBlock);
        ReflectionHelpers.setField(block, "type", type);

        return block;
    }

    // Mocks for separate types of divs

    public static DivTitleBlock createDivTitleBlock(@Nullable final String text, @NonNull @DivTextStyle final String textStyle,
                                                    @NonNull final String menuColor, @Nullable final DivPaddingModifier paddingModifier,
                                                    @Nullable DivAction action) {
        final DivTitleBlock block = mock(DivTitleBlock.class);

        ReflectionHelpers.setField(block, "text", text);
        ReflectionHelpers.setField(block, "textStyle", textStyle);
        ReflectionHelpers.setField(block, "menuColor", Color.parseColor(menuColor));
        ReflectionHelpers.setField(block, "paddingModifier", paddingModifier);
        ReflectionHelpers.setField(block, "action", action);

        return block;
    }

    public static DivSeparatorBlock createDivSeparatorBlock(@ColorInt int delimiterColor, boolean hasDelmiter, @DivSize String size) {
        final DivSeparatorBlock block = mock(DivSeparatorBlock.class);

        ReflectionHelpers.setField(block, "delimiterColor", delimiterColor);
        ReflectionHelpers.setField(block, "hasDelimiter", hasDelmiter);
        ReflectionHelpers.setField(block, "size", size);

        return block;
    }

    @NonNull
    public static DivButtonsBlock.Item createButtonItem(@NonNull final DivAction action,
                                                   @NonNull final String backgroundColor,
                                                   @Nullable final DivImageElement image,
                                                   @Nullable final String text) {
        DivButtonsBlock.Item result = mock(DivButtonsBlock.Item.class);
        ReflectionHelpers.setField(result, "action", action);
        ReflectionHelpers.setField(result, "backgroundColor", Color.parseColor(backgroundColor));
        ReflectionHelpers.setField(result, "image", image);
        ReflectionHelpers.setField(result, "text", text);
        return result;
    }

    public static void setButtonData(@NonNull final DivButtonsBlock buttonBlock, @Nullable final DivAction action,
                                     @NonNull @DivAlignment final String alignment, @NonNull final List<DivButtonsBlock.Item> items,
                                     final boolean isFullwidth) {
        ReflectionHelpers.setField(buttonBlock, "action", action);
        ReflectionHelpers.setField(buttonBlock, "alignment", alignment);
        ReflectionHelpers.setField(buttonBlock, "items", items);
        ReflectionHelpers.setField(buttonBlock, "isFullwidth", isFullwidth);
    }

    public static DivUniversalBlock createDivUniversalBlock(@Nullable String title, @DivTextStyle String titleStyle, @Nullable Integer titleMaxLines,
                                                            @Nullable String text, @DivTextStyle String textStyle, @Nullable Integer textMaxLines) {
        final DivUniversalBlock block = mock(DivUniversalBlock.class);

        ReflectionHelpers.setField(block, "title", title);
        ReflectionHelpers.setField(block, "titleStyle", titleStyle);
        ReflectionHelpers.setField(block, "titleMaxLines", titleMaxLines);

        ReflectionHelpers.setField(block, "text", text);
        ReflectionHelpers.setField(block, "textStyle", textStyle);
        ReflectionHelpers.setField(block, "textMaxLines", textMaxLines);
        return block;
    }

    public static DivImageElement createDivImageElement(@NonNull Uri imageUrl, @FloatRange(from=0.1, to=100) double ratio) {
        DivImageElement element = mock(DivImageElement.class);
        ReflectionHelpers.setField(element, "imageUrl", imageUrl);
        ReflectionHelpers.setField(element, "ratio", ratio);

        return element;
    }

    public static void addDateToDivUniversalBlock(DivUniversalBlock block, @DivPosition String position, @NonNull @DivSize String size,
                                                  @NonNull String dateDay, @Nullable String dateMonth) {
        final DivUniversalBlock.SideElement sideElement = mock(DivUniversalBlock.SideElement.class);
        final DivUniversalBlock.SideElement.Element element = mock(DivUniversalBlock.SideElement.Element.class);
        final DivUniversalBlock.SideElement.DateElement value = mock(DivUniversalBlock.SideElement.DateElement.class);

        ReflectionHelpers.setField(sideElement, "element", element);
        ReflectionHelpers.setField(sideElement, "position", position);
        ReflectionHelpers.setField(sideElement, "size", size);

        ReflectionHelpers.setField(element, "type", "date_element");
        ReflectionHelpers.setField(element, "value", value);

        when(sideElement.element.asDateElement()).thenCallRealMethod();
        when(sideElement.element.asDivImageElement()).thenCallRealMethod();

        ReflectionHelpers.setField(value, "dateDay", dateDay);
        ReflectionHelpers.setField(value, "dateMonth", dateMonth);

        ReflectionHelpers.setField(block, "sideElement", sideElement);
    }

    public static void addImageToDivUniversalBlock(DivUniversalBlock block, @DivPosition String position, @NonNull @DivSize String size,
                                                   @NonNull DivImageElement imageElement) {
        final DivUniversalBlock.SideElement sideElement = mock(DivUniversalBlock.SideElement.class);
        final DivUniversalBlock.SideElement.Element element = mock(DivUniversalBlock.SideElement.Element.class);

        ReflectionHelpers.setField(sideElement, "element", element);
        ReflectionHelpers.setField(sideElement, "position", position);
        ReflectionHelpers.setField(sideElement, "size", size);

        ReflectionHelpers.setField(element, "type", "div-image-element");
        ReflectionHelpers.setField(element, "value", imageElement);

        when(sideElement.element.asDateElement()).thenCallRealMethod();
        when(sideElement.element.asDivImageElement()).thenCallRealMethod();

        ReflectionHelpers.setField(block, "sideElement", sideElement);
    }
}
