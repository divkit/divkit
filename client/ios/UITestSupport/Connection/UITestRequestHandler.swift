@_spi(Internal) import DivKit
import Foundation
import LayoutKit
import UIKit
import VGSL

@MainActor
struct UITestRequestHandler {
  let components: DivKitComponents
  let cardId: DivCardID
  let rootView: UIView
  let logReporter: UITestLogReporter

  func handle(_ request: UITestRequest) throws -> UITestResponse {
    switch request {
    case let .divAction(action):
      try performDivAction(action)
      return .success(.divAction)
    case .logs:
      return .success(.logs(logReporter.logs()))
    case .snapshotRegion:
      return .success(.snapshotRegion(try getSnapshotCropRect()))
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
      rootView.setNeedsLayout()
      rootView.layoutIfNeeded()
    } else {
      throw RequestHandlingError.invalidDivAction(
        parsed.errorsOrWarnings?.map(\.description).joined(separator: "\n") ?? ""
      )
    }
  }

  private func getSnapshotCropRect() throws -> CGRect {
    guard let window = rootView.window else {
      throw RequestHandlingError.snapshotWindowMissing
    }
    rootView.layoutIfNeeded()
    let cropRectInWindow = rootView.convert(rootView.safeAreaLayoutGuide.layoutFrame, to: window)
    let cropRect = window.convert(cropRectInWindow, to: window.screen.coordinateSpace)
    guard cropRect.isValidAndFinite,
          !cropRect.isEmpty,
          window.screen.coordinateSpace.bounds.contains(cropRect) else {
      throw RequestHandlingError.invalidSnapshotRegion
    }
    return cropRect
  }
}

private enum RequestHandlingError: LocalizedError {
  case invalidDivAction(String)
  case snapshotWindowMissing
  case invalidSnapshotRegion

  var errorDescription: String? {
    switch self {
    case let .invalidDivAction(details):
      "Failed to parse DivAction: \(details)"
    case .snapshotWindowMissing:
      "The snapshot root view is not attached to a window"
    case .invalidSnapshotRegion:
      "The snapshot content region is empty, invalid or outside the screen"
    }
  }
}
