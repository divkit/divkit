#if os(iOS)
import UIKit

internal import VGSLUI
internal import DivKit
internal import DivKitExtensions

@objc public final class DivKitKMPFacade: NSObject {
  private let globalVariablesStorage = DivVariableStorage()
  private let divkitComponents: DivKitComponents

  @objc public init(
    actionHandler: DivKitKMPActionHandler?,
    errorReporter: DivKitKMPErrorReporter?
  ) {
    self.divkitComponents = DivKitComponents(
      extensionHandlers: [
        ShimmerImagePreviewExtension()
      ],
      reporter: errorReporter.flatMap { DivKitReporter(errorReporter: $0) },
      urlHandler: DivUrlHandlerDelegate { actionHandler?.handleAction(url: $0.absoluteString) },
      variablesStorage: DivVariablesStorage(outerStorage: globalVariablesStorage)
    )
  }

  @objc public func makeDivKitView(
    _ jsonString: String,
    cardId: String
  ) -> UIView {
    let view = DivView(divKitComponents: divkitComponents)
    Task {
      await view.setSource(
        DivViewSource(
          kind: .data(Data(jsonString.utf8)),
          cardId: DivCardID(rawValue: cardId)
        )
      )
    }
    return view
  }

  @objc public func setGlobalVariables(_ variables: Dictionary<String, Any>) {
    globalVariablesStorage.put(variables.divVariables)
  }
}

private final class DivKitReporter: DivReporter {
  private let errorReporter: DivKitKMPErrorReporter

  init(errorReporter: DivKitKMPErrorReporter) {
    self.errorReporter = errorReporter
  }

  func reportError(cardId: DivCardID, error: DivError) {
    errorReporter.report(cardId: cardId.rawValue, message: error.message)
  }
}

extension [String: Any] {
  fileprivate var divVariables: [DivVariableName: DivVariableValue] {
    compactMapValues {
      switch $0 {
      case let value as String:
        DivVariableValue.string(value)
      case let value as Double:
        DivVariableValue.number(value)
      case let value as Int:
        DivVariableValue.integer(value)
      case let value as Bool:
        DivVariableValue.bool(value)
      case let value as Color:
        DivVariableValue.color(value)
      case let value as URL:
        DivVariableValue.url(value)
      case let value as DivDictionary:
        DivVariableValue.dict(value)
      case let value as DivArray:
        DivVariableValue.array(value)
      default:
        nil
      }
    }.map(key: { DivVariableName(rawValue: $0) }, value: { $0 })
  }
}
#endif
