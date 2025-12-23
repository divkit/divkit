import DivKit
import DivKitTestsSupport
import Testing

struct ShimmerStyleTester<Style: Equatable> {
  private let styleFactory: ([String: Any], DivBlockModelingContext) throws -> Style

  init(styleFactory: @escaping ([String: Any], DivBlockModelingContext) throws -> Style) {
    self.styleFactory = styleFactory
  }

  func assertStyle(
    _ style: Style,
    _ dictionary: [String: Any]
  ) {
    #expect(style == (try! styleFactory(dictionary, .default)))
  }
}
