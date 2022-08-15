import Foundation

import CommonCore

enum Expected {
  static let withRequiredProperty = EntityWithRequiredProperty(property: "Some text")
  static let withOptionalProperty = EntityWithOptionalProperty(property: "Some text")
  static let withMissingOptionalProperty = EntityWithOptionalProperty(property: nil)
  static let withDefaultValue =
    EntityWithPropertyWithDefaultValue(nested: .init(nonOptional: "Some text"))

  static let withRequiredComplexProperty = EntityWithComplexProperty(
    property: .init(value: URL(string: "https://ya.ru")!)
  )
  static let withOptionalComplexProperty = EntityWithOptionalComplexProperty(
    property: .init(value: URL(string: "https://ya.ru")!)
  )
  static let withMissingOptionalComplexProperty = EntityWithOptionalComplexProperty(
    property: nil
  )

  static let withArray = EntityWithArray(array: [
    .entityWithComplexProperty(withRequiredComplexProperty),
    .entityWithComplexProperty(withRequiredComplexProperty),
  ])

  static let withStrictArray = EntityWithStrictArray(array: [
    .entityWithComplexProperty(withRequiredComplexProperty),
    .entityWithComplexProperty(withRequiredComplexProperty),
  ])

  static let withHeterogeneousArray = EntityWithArray(array: [
    .entityWithComplexProperty(withRequiredComplexProperty),
    .entityWithRequiredProperty(withRequiredProperty),
    .entityWithStringEnumProperty(withStringEnumProperty),
  ])

  static let withNestedArray = EntityWithArray(array: [
    .entityWithArray(withArray),
  ])

  static let withArrayWithTransform = EntityWithArrayWithTransform(array: [
    .colorWithHexCode(0x00_FF_00_FF),
    .colorWithHexCode(0xFF_00_00_AA),
  ])

  static let withStringEnumProperty = EntityWithStringEnumProperty(
    property: .second
  )

  static let withOptionalStringEnumProperty = EntityWithOptionalStringEnumProperty(
    property: .second
  )

  static let withMissingStringEnumProperty = EntityWithOptionalStringEnumProperty(
    property: nil
  )

  static let withArrayOfNestedItems = EntityWithArrayOfNestedItems(items: [
    .init(
      entity: .entityWithComplexProperty(withRequiredComplexProperty),
      property: "Some text"
    ),
    .init(
      entity: .entityWithComplexProperty(withRequiredComplexProperty),
      property: "Some text"
    ),
  ])
}
