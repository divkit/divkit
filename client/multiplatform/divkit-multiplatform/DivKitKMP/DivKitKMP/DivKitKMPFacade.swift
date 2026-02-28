#if os(iOS)
import UIKit

@_spi(Legacy) import DivKit

internal import VGSLUI
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
        ShimmerImagePreviewExtension(),
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
    let divView = DivView(divKitComponents: divkitComponents)
    divView.setSource(
      DivViewSource(
        kind: .data(Data(jsonString.utf8)),
        cardId: DivCardID(rawValue: cardId)
      )
    )
    return DivKitKMPView(divView: divView)
  }

  @objc public func getVariableValue(_ name: String) -> Any? {
    globalVariablesStorage.getValue(DivVariableName(rawValue: name))
  }

  @objc public func setGlobalVariables(_ variables: [String: Any]) {
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
      case let number as NSNumber where number.isBool:
        DivVariableValue.bool(number.boolValue)
      case let value as Double:
        DivVariableValue.number(value)
      case let value as Int:
        DivVariableValue.integer(value)
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

extension NSNumber {
  fileprivate var isBool: Bool {
    let type = String(cString: objCType)
    return type == "B" || type == "c"
  }
}
#endif
