package com.yandex.div.core.json

enum class JsonBackend {
    ORG_JSON,
    KTX_JSON
}

internal const val PRECISION_DELTA = 1e-7

internal const val JSON_OBJECT = """
{
  "short_integer_number": 65536,
  "long_integer_number": 1099511627776,
  "decimal_number": 3.141592653589793,
  "negative_decimal_number": -3.141592653589793,
  "boolean": true,
  "string": "Lorem ipsum",
  "short_integer_string": "65536",
  "long_integer_string": "1099511627776",
  "decimal_string": "3.141592653589793",
  "negative_decimal_string": "-3.141592653589793",
  "exponent_decimal_string": "6.5536e4",
  "true_string": "true",
  "false_string": "false",
  "uppercase_boolean_string": "TRUE",
  "mixed_case_boolean_string": "False",
  "null": null,
  "object": { "nested": "value" },
  "array": [ 65536 ]
}
"""

internal const val JSON_ARRAY = """
[
  65536,
  1099511627776,
  3.141592653589793,
  -3.141592653589793,
  true,
  "Lorem ipsum",
  "65536",
  "1099511627776",
  "3.141592653589793",
  "-3.141592653589793",
  "6.5536e4",
  "true",
  "false",
  "TRUE",
  "False",
  null,
  { "nested": "value" },
  [ 65536 ]
]
"""
