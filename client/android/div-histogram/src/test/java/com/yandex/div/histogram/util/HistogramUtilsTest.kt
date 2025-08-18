package com.yandex.div.histogram.util

import com.yandex.div.histogram.util.HistogramUtils.calculateUtf8JsonByteSize
import com.yandex.div.histogram.util.HistogramUtils.calculateUtf8StringByteSize
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class HistogramUtilsTest {

    @Test
    fun testCalculateUtf8StringByteSize() {
        var str = ""
        Assert.assertEquals(str.utf8BytesSize(), calculateUtf8StringByteSize(str))

        str = '\u0000'.toString()
        Assert.assertEquals(str.utf8BytesSize(), calculateUtf8StringByteSize(str))

        str = '\u0080'.toString()
        Assert.assertEquals(str.utf8BytesSize(), calculateUtf8StringByteSize(str))

        str = '\u0800'.toString()
        Assert.assertEquals(str.utf8BytesSize(), calculateUtf8StringByteSize(str))

        str = '\uFFFF'.toString()
        Assert.assertEquals(str.utf8BytesSize(), calculateUtf8StringByteSize(str))

        str = "${'\uD800'}${'\uDC00'}"
        Assert.assertEquals(str.utf8BytesSize(), calculateUtf8StringByteSize(str))

        str = "मेरा नाम"
        Assert.assertEquals(str.utf8BytesSize(), calculateUtf8StringByteSize(str))
    }

    @Test
    fun testCalculateUtf8JsonByteSize() {
        var json = JSONObject()
        Assert.assertEquals(json.toString().utf8BytesSize(), calculateUtf8JsonByteSize(json))

        json = JSONObject(
            """
            {
                "key1":[true,false,true],
                "key2":null,
                "key3":"मेरा नाम ${'$'}dfg $\\",
                "key4":true,
                "key5":[1,2,3,4,5],
                "key6":{
                    "key1":[true,false,true],
                    "key2":null,
                    "key3":"मेरा नाम",
                    "key4":true,
                    "key5":[1,2,3,4,5]
                }
            }
        """
        )
        Assert.assertEquals(json.toString().utf8BytesSize(), calculateUtf8JsonByteSize(json))

        json = JSONObject(REAL_DIV_JSON)
        Assert.assertEquals(json.toString().utf8BytesSize(), calculateUtf8JsonByteSize(json))
    }

    private fun String.utf8BytesSize() = this.toByteArray(Charsets.UTF_8).size

    private companion object {
        private const val REAL_DIV_JSON = """
         {
            "log_id" : "header",
            "states" : [
               {
                  "div" : {
                     "background" : [
                        {
                           "color" : "#FFF",
                           "type" : "solid"
                        }
                     ],
                     "content_alignment_vertical" : "center",
                     "items" : [
                        {
                           "custom_type" : "bender_profile_button",
                           "height" : {
                              "type" : "fixed",
                              "value" : 50
                           },
                           "type" : "custom",
                           "width" : {
                              "type" : "fixed",
                              "value" : 64
                           }
                        },
                        {
                           "alignment_horizontal" : "center",
                           "height" : {
                              "type" : "fixed",
                              "value" : 52
                           },
                           "image_url" : "https://yastatic.net/s3/home/div/bottomsheet/bender/ya.2.png",
                           "preview" : "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",
                           "type" : "image",
                           "width" : {
                              "type" : "fixed",
                              "value" : 52
                           }
                        },
                        {
                           "action" : {
                              "log_id" : "open_alice",
                              "url" : "dialog://?type=alice"
                           },
                           "alignment_horizontal" : "right",
                           "height" : {
                              "type" : "fixed",
                              "value" : 32
                           },
                           "image_url" : "https://yastatic.net/s3/home/div/bottomsheet/bender/alice.2.png",
                           "preview" : "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",
                           "type" : "image",
                           "width" : {
                              "type" : "fixed",
                              "value" : 32
                           }
                        }
                     ],
                     "orientation" : "overlap",
                     "paddings" : {
                        "bottom" : 14,
                        "left" : 25,
                        "right" : 25,
                        "top" : 14
                     },
                     "type" : "container",
                     "width" : {
                        "type" : "match_parent"
                     }
                  },
                  "state_id" : 0
               }
            ],
            "type" : "div2"
         }
        """
    }
}
