import XCTest

extension XCTestCase {
  static func makeSuite<TestData>(
    input: [(name: String, data: TestData)],
    test: @escaping (TestData) -> Void
  ) -> XCTestSuite {
    let suite = XCTestSuite(forTestCaseClass: self)
    input.forEach { name, data in
      let block: @convention(block) (XCTestCase) -> Void = { _ in test(data) }
      let implementation = imp_implementationWithBlock(block)
      let selector = NSSelectorFromString(name)
      class_addMethod(self, selector, implementation, "v@:")
      suite.addTest(self.init(selector: selector))
    }
    return suite
  }

  static func loadJsonFiles(
    _ directoryName: String,
    exclusions: [String]
  ) -> [JsonFile] {
    let testBundle = Bundle(for: DivKitSnapshotTestCase.self)
    let snapshotsPath = testBundle.bundleURL.appendingPathComponent(directoryName).path

    guard let paths = try? FileManager.default.subpathsOfDirectory(atPath: snapshotsPath) else {
      return []
    }

    return paths.compactMap { path -> JsonFile? in
      guard let index = path.lastIndex(of: "/") else {
        return nil
      }
      return JsonFile(
        path: path,
        name: String(path[path.index(after: index)...]),
        subdirectory: String(path[..<index])
      )
    }
    .filter { $0.name.contains(".json") && !exclusions.contains($0.path) }
  }
}

struct JsonFile {
  let path: String
  let name: String
  let subdirectory: String
}
