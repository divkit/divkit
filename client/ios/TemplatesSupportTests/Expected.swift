import Foundation

import CommonCorePublic

enum Expected {
  static let withRequiredProperty = EntityWithRequiredProperty(
    property: .value("Some text")
  )

  static let withOptionalProperty = EntityWithOptionalProperty(
    property: .value("Some text")
  )

  static let withMissingOptionalProperty = EntityWithOptionalProperty(
    property: nil
  )

  static let withDefaultValue = EntityWithPropertyWithDefaultValue(
    nested: EntityWithPropertyWithDefaultValue.Nested(
      nonOptional: .value("Some text")
    )
  )

  static let withRequiredComplexProperty = EntityWithComplexProperty(
    property: EntityWithComplexProperty.Property(
      value: .value(URL(string: "https://ya.ru")!)
    )
  )

  static let withOptionalComplexProperty = EntityWithOptionalComplexProperty(
    property: EntityWithOptionalComplexProperty.Property(
      value: .value(URL(string: "https://ya.ru")!)
    )
  )

  static let withMissingOptionalComplexProperty = EntityWithOptionalComplexProperty(
    property: nil
  )

  static let withArray = EntityWithArray(
    array: [
      .entityWithComplexProperty(withRequiredComplexProperty),
      .entityWithComplexProperty(withRequiredComplexProperty),
    ]
  )

  static let withStrictArray = EntityWithStrictArray(
    array: [
      .entityWithComplexProperty(withRequiredComplexProperty),
      .entityWithComplexProperty(withRequiredComplexProperty),
    ]
  )

  static let withHeterogeneousArray = EntityWithArray(
    array: [
      .entityWithComplexProperty(withRequiredComplexProperty),
      .entityWithRequiredProperty(withRequiredProperty),
      .entityWithStringEnumProperty(withStringEnumProperty),
    ]
  )

  static let withNestedArray = EntityWithArray(
    array: [
      .entityWithArray(withArray),
    ]
  )

  static let withArrayWithTransform = EntityWithArrayWithTransform(
    array: [
      .value(.colorWithHexCode(0x00_FF_00_FF)),
      .value(.colorWithHexCode(0xFF_00_00_AA)),
    ]
  )

  static let withStringEnumProperty = EntityWithStringEnumProperty(
    property: .value(.second)
  )

  static let withOptionalStringEnumProperty = EntityWithOptionalStringEnumProperty(
    property: .value(.second)
  )

  static let withMissingStringEnumProperty = EntityWithOptionalStringEnumProperty(
    property: nil
  )

  static let withArrayOfNestedItems = EntityWithArrayOfNestedItems(
    items: [
      EntityWithArrayOfNestedItems.Item(
        entity: .entityWithComplexProperty(withRequiredComplexProperty),
        property: .value("Some text")
      ),
      EntityWithArrayOfNestedItems.Item(
        entity: .entityWithComplexProperty(withRequiredComplexProperty),
        property: .value("Some text")
      ),
    ]
  )
}
