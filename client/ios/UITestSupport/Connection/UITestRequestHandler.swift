@_spi(Internal) import DivKit
import Foundation
import LayoutKit
import VGSL

@MainActor
struct UITestRequestHandler {
  let components: DivKitComponents
  let cardId: DivCardID

  func handle(_ request: UITestRequest) throws -> UITestResponse {
    switch request {
    case let .divAction(action):
      try performDivAction(action)
      return .success
    }
  }

  private func performDivAction(_ dictionary: JSONDictionary) throws {
    let parsed = DivTemplates.empty.parseValue(
      type: DivAction.self,
      from: dictionary.untypedJSON()
    )
    if let action = parsed.value {
      components.actionHandler.handle(
        action,
        path: UIElementPath(cardId.rawValue),
        source: .tap,
        sender: nil
      )
      components.flushUpdateActions()
    } else {
      throw RequestHandlingError.invalidDivAction(
        parsed.errorsOrWarnings?.map(\.description).joined(separator: "\n") ?? ""
      )
    }
  }
}

private enum RequestHandlingError: LocalizedError {
  case invalidDivAction(String)

  var errorDescription: String? {
    switch self {
    case let .invalidDivAction(details):
      "Failed to parse DivAction: \(details)"
    }
  }
}
