import XCTest

import CommonCorePublic
@testable import DivKit
import LayoutKit
import NetworkingPublic
import Serialization
import VGSL_Fundamentals_Tiny

final class DivKitTests: XCTestCase {
  static let cardId = DivCardID(rawValue: "test_card_id")

  func test_multithreaded_blockCreation() {
    let expectation = XCTestExpectation()
    guard let data = try? dataFromFile(named: "heavy", subdirectory: nil) else {
      XCTFail()
      return
    }
    let components = DivKitComponents()
    let counter = Atomic(initialValue: 0)

    for i in 0...50 {
      let cardId = DivCardID(rawValue: "card_\(i)")
      let result = try! components.parseDivDataWithTemplates(data, cardId: cardId)
      onBackgroundThread {
        let context = components.makeContext(cardId: cardId, cachedImageHolders: [])
        let _ = try! result.value!.makeBlock(context: context)

        counter.accessWrite { counter in
          counter += 1
          if counter == 50 {
            expectation.fulfill()
          }
        }
      }
    }
    wait(for: [expectation])
  }
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

private func dataFromFile(named name: String, subdirectory: String?) throws -> Data {
  let bundle = Bundle(for: DivKitTests.self)
  let url = bundle.url(forResource: name, withExtension: "json", subdirectory: subdirectory)!
  return try Data(contentsOf: url)
}

private func jsonDictFromFile(named name: String, subdirectory: String?) throws -> [String: Any] {
  let data = try dataFromFile(named: name, subdirectory: subdirectory)
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
    blockStateStorage: DivBlockStateStorage = DivBlockStateStorage(),
    extensionHandlers: [DivExtensionHandler] = [],
    scheduler: Scheduling? = nil
  ) {
    self.init(
      cardId: DivKitTests.cardId,
      cardLogId: DivKitTests.cardId.rawValue,
      stateManager: DivStateManager(),
      blockStateStorage: blockStateStorage,
      imageHolderFactory: FakeImageHolderFactory(),
      extensionHandlers: extensionHandlers,
      scheduler: scheduler,
      persistentValuesStorage: DivPersistentValuesStorage()
    )
  }
}

final class FakeImageHolderFactory: DivImageHolderFactory {
  func make(_: URL?, _: ImagePlaceholder?) -> ImageHolder {
    FakeImageHolder()
  }
}

private final class FakeImageHolder: ImageHolder {
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

public func XCTAssertThrowsError<T: Equatable & Error>(
  _ expression: @autoclosure () throws -> some Any,
  _ expectedError: T
) {
  XCTAssertThrowsError(try expression()) { error in
    XCTAssertEqual(error as? T, expectedError)
  }
}
