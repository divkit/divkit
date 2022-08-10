package com.yandex.div.legacy.view;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.yandex.div.DivAction;
import com.yandex.div.DivAlignment;
import com.yandex.div.DivTrafficBlock;
import com.yandex.div.legacy.DivDataMockUtils;
import com.yandex.div.legacy.R;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ReflectionHelpers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class TrafficDivViewBuilderTest {

    private static final String ACTION_URL = "ACTION URL";
    private static final String ACTION_LOGID = "ACTION LOGID";
    @DivAlignment
    private static final String ALIGNMENT = DivAlignment.RIGHT;

    @Mock
    private DivView mDivView;

    private TrafficDivViewBuilder mTrafficDivViewBuilder;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);

        Activity activity = Robolectric.setupActivity(Activity.class);
        when(mDivView.getContext()).thenReturn(activity);

        mTrafficDivViewBuilder = new TrafficDivViewBuilder(activity);
    }

    @Test
    public void testEmptyData() {
        final DivTrafficBlock trafficBlock = mock(DivTrafficBlock.class);
        final DivAction divAction = DivDataMockUtils.createDivAction(ACTION_LOGID, ACTION_URL);
        setData(trafficBlock, divAction, ALIGNMENT, Collections.emptyList());

        final View build = mTrafficDivViewBuilder.build(mDivView, trafficBlock);

        Assert.assertNull(build);
    }

    @Test
    public void testIncorrectBlock() {
        final DivTrafficBlock trafficBlock = mock(DivTrafficBlock.class);
        final DivAction divAction = DivDataMockUtils.createDivAction(ACTION_LOGID, ACTION_URL);

        final DivTrafficBlock.Item item1 = createItem("blue", "", "text1");
        setData(trafficBlock, divAction, ALIGNMENT, Collections.singletonList(item1));

        final View build = mTrafficDivViewBuilder.build(mDivView, trafficBlock);

        Assert.assertNull(build);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testBind() {
        final DivTrafficBlock trafficBlock = mock(DivTrafficBlock.class);
        final DivAction divAction = DivDataMockUtils.createDivAction(ACTION_LOGID, ACTION_URL);
        final DivTrafficBlock.Item item1 = createItem("blue", "12", "14");
        setData(trafficBlock, divAction, ALIGNMENT, Collections.singletonList(item1));

        final View build = mTrafficDivViewBuilder.build(mDivView, trafficBlock);

        Assert.assertNotNull(build);

        RecyclerView recyclerView = build.findViewById(R.id.div_traffic_list);
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        Assert.assertEquals(1, adapter.getItemCount());
        RecyclerView.ViewHolder viewHolder = adapter.onCreateViewHolder(recyclerView, 0);
        adapter.bindViewHolder(viewHolder, 0);

        TextView score = viewHolder.itemView.findViewById(R.id.div_traffic_score);
        Assert.assertEquals("12", score.getText().toString());
        TextView text = viewHolder.itemView.findViewById(R.id.div_traffic_text);
        Assert.assertEquals("14", text.getText().toString());
    }

    @Test
    public void testMultipleItems() {
        final DivTrafficBlock trafficBlock = mock(DivTrafficBlock.class);
        final DivAction divAction = DivDataMockUtils.createDivAction(ACTION_LOGID, ACTION_URL);
        final DivTrafficBlock.Item item1 = createItem("blue", "so cool", "text1");
        final DivTrafficBlock.Item item2 = createItem("red", "such impressive", "text2");
        final DivTrafficBlock.Item item3 = createItem("black", "wow", "text3");
        setData(trafficBlock, divAction, ALIGNMENT, Arrays.asList(item1, item2, item3));

        final View build = mTrafficDivViewBuilder.build(mDivView, trafficBlock);

        Assert.assertNotNull(build);

        RecyclerView recyclerView = build.findViewById(R.id.div_traffic_list);
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        Assert.assertEquals(3, adapter.getItemCount());

        // item 1
        RecyclerView.ViewHolder viewHolder = adapter.onCreateViewHolder(recyclerView, 0);
        adapter.bindViewHolder(viewHolder, 0);

        TextView score = viewHolder.itemView.findViewById(R.id.div_traffic_score);
        Assert.assertEquals("so cool", score.getText().toString());
        TextView text = viewHolder.itemView.findViewById(R.id.div_traffic_text);
        Assert.assertEquals("text1", text.getText().toString());

        // item 2
        viewHolder = adapter.onCreateViewHolder(recyclerView, 1);
        adapter.bindViewHolder(viewHolder, 1);

        score = viewHolder.itemView.findViewById(R.id.div_traffic_score);
        Assert.assertEquals("such impressive", score.getText().toString());
        text = viewHolder.itemView.findViewById(R.id.div_traffic_text);
        Assert.assertEquals("text2", text.getText().toString());

        // item 3
        viewHolder = adapter.onCreateViewHolder(recyclerView, 2);
        adapter.bindViewHolder(viewHolder, 2);

        score = viewHolder.itemView.findViewById(R.id.div_traffic_score);
        Assert.assertEquals("wow", score.getText().toString());
        text = viewHolder.itemView.findViewById(R.id.div_traffic_text);
        Assert.assertEquals("text3", text.getText().toString());
    }

    @NonNull
    private static DivTrafficBlock.Item createItem(@NonNull final String color, @NonNull final String score, @Nullable final String text) {
        final DivTrafficBlock.Item item = mock(DivTrafficBlock.Item.class);
        ReflectionHelpers.setField(item, "color", Color.parseColor(color));
        ReflectionHelpers.setField(item, "score", score);
        ReflectionHelpers.setField(item, "text", text);
        return item;
    }

    private static void setData(@NonNull final DivTrafficBlock trafficBlock, @Nullable final DivAction action,
                                @NonNull @DivAlignment final String alignment,
                                @NonNull final List<DivTrafficBlock.Item> items) {
        ReflectionHelpers.setField(trafficBlock, "action", action);
        ReflectionHelpers.setField(trafficBlock, "alignment", alignment);
        ReflectionHelpers.setField(trafficBlock, "items", items);
    }
}
