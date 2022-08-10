package com.yandex.div.json;

import com.yandex.div.json.schema.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void testIsMatched() {
        Assert.assertTrue(StringUtils.isMatched("13:21", "^\\d{1,2}\\:\\d{2}$"));
        Assert.assertFalse(StringUtils.isMatched(":21", "^\\d{1,2}\\:\\d{2}$"));
    }
}
