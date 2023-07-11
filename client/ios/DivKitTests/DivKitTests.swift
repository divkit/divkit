import XCTest

import CommonCorePublic
import DivKit
import LayoutKit
import NetworkingPublic
import Serialization

final class DivKitTests: XCTestCase {
  static let cardId = DivCardID(rawValue: cardLogId)
  static let cardLogId = "test_card_id"
}

extension TemplateValue where ResolvedValue: DivBlockModeling {
  static func make(
    fromFile filename: String,
    subdirectory: String? = nil,
    context: DivBlockModelingContext = .default
  ) throws -> Block {
    let dict = try jsonDictFromFile(
      named: filename,
      subdirectory: "unit_test_data/" + (subdirectory ?? "")
    )
    let templatesDict = (dict["templates"] as? [String: Any]) ?? [:]
    let templates = DivTemplates(dictionary: templatesDict)
    let div = templates.parseValue(type: Self.self, from: dict)
    return try div.value!.makeBlock(context: context)
  }
}

private func jsonDictFromFile(named name: String, subdirectory: String?) throws -> [String: Any] {
  let bundle = Bundle(for: DivKitTests.self)
  let url = bundle.url(forResource: name, withExtension: "json", subdirectory: subdirectory)!
  let data = try Data(contentsOf: url)
  return try JSONSerialization.jsonObject(with: data, options: []) as! [String: Any]
}

extension Deserializable {
  init(file name: String) throws {
    let bundle = Bundle(for: DivKitTests.self)
    let url = bundle.url(forResource: name, withExtension: "json")!
    let data = try Data(contentsOf: url)
    try self.init(JSONData: data)
  }
}

extension DivBlockModelingContext {
  static let `default` = DivBlockModelingContext()

  init(
    blockStateStorage: DivBlockStateStorage = DivBlockStateStorage()
  ) {
    self.init(
      cardId: DivKitTests.cardId,
      cardLogId: DivKitTests.cardLogId,
      stateManager: DivStateManager(),
      blockStateStorage: blockStateStorage,
      imageHolderFactory: ImageHolderFactory(make: { _, _ in FakeImageHolder() })
    )
  }
}

final class FakeImageHolder: ImageHolder {
  var image: Image? {
    nil
  }

  var placeholder: ImagePlaceholder? {
    nil
  }

  func requestImageWithCompletion(_: @escaping ((Image?) -> Void)) -> Cancellable? {
    nil
  }

  func reused(with _: ImagePlaceholder?, remoteImageURL _: URL?) -> ImageHolder? {
    nil
  }

  func equals(_: ImageHolder) -> Bool {
    true
  }

  var debugDescription: String {
    "FakeImageHolder"
  }
}

public func XCTAssertThrowsError<T: Equatable & Error, U>(
  _ expression: @autoclosure () throws -> U,
  _ expectedError: T
) {
  XCTAssertThrowsError(try expression()) { error in
    XCTAssertEqual(error as? T, expectedError)
  }
}
