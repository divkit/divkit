import XCTest

extension XCTestCase {
  static func makeSuite<TestData>(
    input: [(name: String, data: TestData)],
    test: @escaping (TestData) -> Void
  ) -> XCTestSuite {
    let suite = XCTestSuite(forTestCaseClass: self)
    for (name, data) in input {
      let block: @convention(block) (XCTestCase) -> Void = { _ in test(data) }
      let implementation = imp_implementationWithBlock(block)
      let selector = NSSelectorFromString(name)
      class_addMethod(self, selector, implementation, "v@:")
      suite.addTest(self.init(selector: selector))
    }
    return suite
  }
}
