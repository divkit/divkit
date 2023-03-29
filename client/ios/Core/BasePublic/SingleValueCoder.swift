// Copyright 2021 Yandex LLC. All rights reserved.

import Foundation

struct SingleValueEncoder {
  func encode<T: Encodable>(_ value: T) throws -> Data {
    do {
      return try PropertyListEncoder().encode(value)
    } catch let EncodingError.invalidValue(v, context) {
      // failed due to scalar toplevel value
      if context.codingPath.isEmpty {
        let encoder = SingleValueEncoderImpl()
        try value.encode(to: encoder)
        guard let data = encoder.container.data.flatMap({ $0 }) else {
          throw EncodingError.invalidValue(v, context)
        }
        return data
      } else {
        throw EncodingError.invalidValue(v, context)
      }
    }
  }
}

struct SingleValueDecoder {
  func decode<T: Decodable>(_: T.Type, from data: Data) throws -> T {
    do {
      return try PropertyListDecoder().decode(T.self, from: data)
    } catch {
      let decoder = SingleValueDecoderImpl(data: data)
      return try T(from: decoder)
    }
  }
}

private final class SingleValueEncoderImpl: Encoder {
  let codingPath: [CodingKey] = []
  let container = SingleValueEncodingContainerImpl()
  var userInfo: [CodingUserInfoKey: Any] = [:]

  func container<Key>(
    keyedBy _: Key.Type
  ) -> KeyedEncodingContainer<Key> where Key: CodingKey {
    fatalError("only singleValueContainer() should be called")
  }

  func unkeyedContainer() -> UnkeyedEncodingContainer {
    fatalError("only singleValueContainer() should be called")
  }

  func singleValueContainer() -> SingleValueEncodingContainer { container }
}

private final class SingleValueEncodingContainerImpl: SingleValueEncodingContainer {
  var codingPath: [CodingKey] = []
  var data: Data??

  func encodeNil() throws {}

  func encode(_ value: Bool) throws {
    data = Data([value ? 1 : 0])
  }

  func encode(_ value: String) throws {
    data = value.data(using: .utf8)!
  }

  func encode(_ value: Double) throws {
    data = value.bitPattern.data
  }

  func encode(_ value: Float) throws {
    data = value.bitPattern.data
  }

  func encode<T: FixedWidthInteger & Encodable>(_ value: T) throws {
    data = value.data
  }

  func encode<T>(_ value: T) throws where T: Encodable {
    let impl = SingleValueEncoderImpl()
    try value.encode(to: impl)
    data = impl.container.data
  }
}

private struct SingleValueDecoderImpl: Decoder {
  let codingPath: [CodingKey] = []
  let userInfo: [CodingUserInfoKey: Any] = [:]
  let data: Data

  func container<Key>(keyedBy _: Key.Type) throws -> KeyedDecodingContainer<Key>
    where Key: CodingKey {
    throw DecodingError.dataCorrupted(DecodingError.Context(
      codingPath: [],
      debugDescription: "SingleValueDecoderImpl supports only singleValueContainer"
    ))
  }

  func unkeyedContainer() throws -> UnkeyedDecodingContainer {
    throw DecodingError.dataCorrupted(DecodingError.Context(
      codingPath: [],
      debugDescription: "SingleValueDecoderImpl supports only singleValueContainer"
    ))
  }

  func singleValueContainer() throws -> SingleValueDecodingContainer {
    SingleValueDecodingContainerImpl(data: data)
  }
}

private struct SingleValueDecodingContainerImpl: SingleValueDecodingContainer {
  let codingPath: [CodingKey] = []
  let data: Data

  func decodeNil() -> Bool { false }

  func decode(_: Bool.Type) throws -> Bool {
    switch data {
    case Data([0]): return false
    case Data([1]): return true
    default:
      throw DecodingError.dataCorrupted(DecodingError.Context(
        codingPath: [],
        debugDescription: "Expected 0 or 1, got \(data)"
      ))
    }
  }

  func decode(_: String.Type) throws -> String {
    guard let string = String(data: data, encoding: .utf8) else {
      throw DecodingError.dataCorrupted(DecodingError.Context(
        codingPath: [],
        debugDescription: "Failed to decode utf8 from \(data)"
      ))
    }
    return string
  }

  func decode(_: Double.Type) throws -> Double {
    Double(bitPattern: try UInt64(data: data))
  }

  func decode(_: Float.Type) throws -> Float {
    Float(bitPattern: try UInt32(data: data))
  }

  func decode<T: FixedWidthInteger & Decodable>(_: T.Type) throws -> T {
    try T(data: data)
  }

  func decode<T>(_: T.Type) throws -> T where T: Decodable {
    try T(from: SingleValueDecoderImpl(data: data))
  }
}

extension FixedWidthInteger {
  fileprivate typealias Base = Data.Element
  fileprivate typealias SignedBase = Int8

  fileprivate var data: Data {
    assert(Self.bitWidth % Base.bitWidth == 0)

    var bytes: [Base] = []
    for i in 0..<(Self.bitWidth / Base.bitWidth) {
      let shifted = self >> (i * SignedBase.bitWidth)
      let result: Base
      if Self.isSigned, i == (Self.bitWidth / Base.bitWidth) - 1 {
        result = Base(bitPattern: SignedBase(truncatingIfNeeded: shifted))
      } else {
        result = Base(truncatingIfNeeded: shifted)
      }
      bytes.append(result)
    }
    return Data(bytes)
  }

  fileprivate init(data: Data) throws {
    assert(Self.bitWidth % Base.bitWidth == 0)

    guard data.count == (Self.bitWidth / Base.bitWidth) else {
      throw DecodingError.dataCorrupted(DecodingError.Context(
        codingPath: [],
        debugDescription: "Failed to decode \(Self.self) from \(data)"
      ))
    }

    let reversedData = data.reversed()
    self = Self.zero
    for i in reversedData.indices {
      let value = reversedData[i]
      self <<= Base.bitWidth
      if Self.isSigned, i == reversedData.indices.first {
        self += Self(Int8(bitPattern: value))
      } else {
        self += Self(value)
      }
    }
  }
}
