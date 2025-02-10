#if os(iOS)
import UIKit
#elseif os(macOS)
import AppKit
#endif

import VGSL

public struct DebugParams {
  public struct Measurements: Sendable {
    public let divDataParsingTime: TimeMeasure
    public let renderTime: TimeMeasure
    public let templateParsingTime: TimeMeasure

    public init(
      divDataParsingTime: TimeMeasure,
      renderTime: TimeMeasure,
      templateParsingTime: TimeMeasure
    ) {
      self.divDataParsingTime = divDataParsingTime
      self.renderTime = renderTime
      self.templateParsingTime = templateParsingTime
    }
  }

  public let isDebugInfoEnabled: Bool
  public let processMeasurements: ResultAction<(cardId: DivCardID, measurements: Measurements)>
  public let showDebugInfo: (ViewType) -> Void
  public let errorCounterInsets: EdgeInsets
  public let widcardExtensionHandlers: [DivExtensionHandler]

  public init(
    isDebugInfoEnabled: Bool = false,
    processMeasurements: @escaping ResultAction<(cardId: DivCardID, measurements: Measurements)> =
      { _ in },
    showDebugInfo: @escaping (ViewType) -> Void = DebugParams.showDebugInfo(_:),
    errorCounterInsets: EdgeInsets = .zero,
    widcardExtensionHandlers: [DivExtensionHandler] = []
  ) {
    self.isDebugInfoEnabled = isDebugInfoEnabled
    self.showDebugInfo = showDebugInfo
    self.errorCounterInsets = errorCounterInsets
    self.processMeasurements = processMeasurements
    self.widcardExtensionHandlers = widcardExtensionHandlers
  }
}

#if os(iOS)
extension DebugParams {
  public static func showDebugInfo(_ view: ViewType) {
    let window = (UIApplication.shared.connectedScenes.first as? UIWindowScene)?.windows.first
    view.frame.center = window?.center ?? .zero
    window?.addSubview(view)
  }
}

#elseif os(macOS)
extension DebugParams {
  public static func showDebugInfo(_: ViewType) {}
}
#endif
