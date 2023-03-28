import CoreGraphics

import BasePublic
import BaseUIPublic
import LayoutKit
import NetworkingPublic
import Serialization

public struct DivBlockModelingContext {
  public let cardId: DivCardID
  internal var cardLogId: String?
  public internal(set) var parentPath: UIElementPath
  internal var parentDivStatePath: DivStatePath?
  public var stateManager: DivStateManager
  public let blockStateStorage: DivBlockStateStorage
  public let visibilityCounter: DivVisibilityCounting
  public var galleryResizableInsets: InsetMode.Resizable?
  public let imageHolderFactory: ImageHolderFactory
  public let highPriorityImageHolderFactory: ImageHolderFactory?
  public let divCustomBlockFactory: DivCustomBlockFactory
  public let fontSpecifiers: FontSpecifiers
  public let flagsInfo: DivFlagsInfo
  public let extensionHandlers: [String: DivExtensionHandler]
  public let stateInterceptors: [String: DivStateInterceptor]
  public let expressionResolver: ExpressionResolver
  public let debugParams: DebugParams
  public var childrenA11yDescription: String?
  public weak var parentScrollView: ScrollView?
  public let errorsStorage: DivErrorsStorage
  var overridenWidth: DivOverridenSize?
  var overridenHeight: DivOverridenSize?

  public init(
    cardId: DivCardID,
    cardLogId: String? = nil,
    parentPath: UIElementPath? = nil,
    parentDivStatePath: DivStatePath? = nil,
    stateManager: DivStateManager,
    blockStateStorage: DivBlockStateStorage = DivBlockStateStorage(),
    visibilityCounter: DivVisibilityCounting = DivVisibilityCounter(),
    galleryResizableInsets: InsetMode.Resizable? = nil,
    imageHolderFactory: ImageHolderFactory,
    highPriorityImageHolderFactory: ImageHolderFactory? = nil,
    divCustomBlockFactory: DivCustomBlockFactory = EmptyDivCustomBlockFactory(),
    fontSpecifiers: FontSpecifiers = BaseUIPublic.fontSpecifiers,
    flagsInfo: DivFlagsInfo = .default,
    extensionHandlers: [DivExtensionHandler] = [],
    stateInterceptors: [DivStateInterceptor] = [],
    variables: DivVariables = [:],
    debugParams: DebugParams = DebugParams(),
    childrenA11yDescription: String? = nil,
    parentScrollView: ScrollView? = nil,
    errorsStorage: DivErrorsStorage = DivErrorsStorage(errors: [])
  ) {
    self.cardId = cardId
    self.cardLogId = cardLogId
    self.parentPath = parentPath ?? UIElementPath(cardId.rawValue)
    self.parentDivStatePath = parentDivStatePath
    self.stateManager = stateManager
    self.blockStateStorage = blockStateStorage
    self.visibilityCounter = visibilityCounter
    self.galleryResizableInsets = galleryResizableInsets
    self.imageHolderFactory = imageHolderFactory
    self.highPriorityImageHolderFactory = highPriorityImageHolderFactory
    self.divCustomBlockFactory = divCustomBlockFactory
    self.flagsInfo = flagsInfo
    self.fontSpecifiers = fontSpecifiers
    self.debugParams = debugParams
    self.childrenA11yDescription = childrenA11yDescription
    self.parentScrollView = parentScrollView
    self.errorsStorage = errorsStorage

    if debugParams.isDebugInfoEnabled {
      self.expressionResolver = ExpressionResolver(
        variables: variables,
        errorTracker: { [weak errorsStorage] error in
          errorsStorage?.add(error)
        }
      )
    } else {
      self.expressionResolver = ExpressionResolver(variables: variables)
    }

    var extensionsHandlersDictionary = [String: DivExtensionHandler]()
    extensionHandlers.forEach {
      let id = $0.id
      if extensionsHandlersDictionary[id] != nil {
        DivKitLogger.failure("Duplicate DivExtensionHandler for: \(id)")
        return
      }
      extensionsHandlersDictionary[id] = $0
    }
    self.extensionHandlers = extensionsHandlersDictionary

    var stateInterceptorsDictionary = [String: DivStateInterceptor]()
    stateInterceptors.forEach {
      let id = $0.id
      if stateInterceptorsDictionary[id] != nil {
        DivKitLogger.failure("Duplicate DivStateInterceptor for: \(id)")
        return
      }
      stateInterceptorsDictionary[id] = $0
    }
    self.stateInterceptors = stateInterceptorsDictionary
  }

  public func getExtensionHandlers(for div: DivBase) -> [DivExtensionHandler] {
    guard let extensions = div.extensions else {
      return []
    }
    return extensions.compactMap {
      let id = $0.id
      if !extensionHandlers.keys.contains(id) && !stateInterceptors.keys.contains(id) {
        addError(level: .warning, message: "No DivExtensionHandler/DivStateInterceptor for: \(id)")
      }
      return extensionHandlers[id]
    }
  }

  public func getStateInterceptor(for divState: DivState) -> DivStateInterceptor? {
    divState.extensions?.compactMap { stateInterceptors[$0.id] }.first
  }

  public func addError(level: DivErrorLevel, message: String) {
    let error: DivError
    switch level {
    case .warning:
      error = DivBlockModelingWarning(message, path: parentPath)
    case .error:
      error = DivBlockModelingError(message, path: parentPath)
    }
    errorsStorage.add(error)
  }

  func override(width: DivSize) -> DivSize {
    guard let overridenWidth = overridenWidth else {
      return width
    }
    if overridenWidth.original == width {
      return overridenWidth.overriden
    }
    return width
  }

  func override(height: DivSize) -> DivSize {
    guard let overridenHeight = overridenHeight else {
      return height
    }
    if overridenHeight.original == height {
      return overridenHeight.overriden
    }
    return height
  }
}
