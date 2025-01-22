import CoreGraphics
import LayoutKit
import VGSL

#if os(iOS)
import UIKit
#else
import AppKit
#endif

public struct DivBlockModelingContext {
  private(set) var viewId: DivViewId
  private(set) var cardLogId: String?
  private(set) var parentDivStatePath: DivStatePath?
  let stateManager: DivStateManager
  public let actionHandler: DivActionHandler?
  public let blockStateStorage: DivBlockStateStorage
  let visibilityCounter: DivVisibilityCounter
  let lastVisibleBoundsCache: DivLastVisibleBoundsCache
  public let imageHolderFactory: DivImageHolderFactory
  let divCustomBlockFactory: DivCustomBlockFactory
  public let fontProvider: DivFontProvider
  let flagsInfo: DivFlagsInfo
  let extensionHandlers: [String: DivExtensionHandler]
  let layoutDirection: UserInterfaceLayoutDirection
  let debugParams: DebugParams
  let scheduler: Scheduling
  let playerFactory: PlayerFactory?
  private(set) weak var parentScrollView: ScrollView?
  public private(set) var errorsStorage: DivErrorsStorage
  let debugErrorCollector: DebugErrorCollector?
  private let persistentValuesStorage: DivPersistentValuesStorage
  let tooltipViewFactory: DivTooltipViewFactory?
  let functionsStorage: DivFunctionsStorage?
  public let variablesStorage: DivVariablesStorage
  let triggersStorage: DivTriggersStorage?
  public private(set) var expressionResolver: ExpressionResolver
  private let functionsProvider: FunctionsProvider
  public let variableTracker: DivVariableTracker?
  public private(set) var parentPath: UIElementPath
  private(set) var elementId: String?
  private(set) var sizeModifier: DivSizeModifier?
  private(set) var localValues = [String: AnyHashable]()
  let layoutProviderHandler: DivLayoutProviderHandler?
  let idToPath: IdToPath
  let animatorController: DivAnimatorController?
  private(set) var accessibilityElementsStorage = DivAccessibilityElementsStorage()

  @_spi(Internal)
  public init(
    cardId: DivCardID,
    additionalId: String? = nil,
    stateManager: DivStateManager = DivStateManager(),
    blockStateStorage: DivBlockStateStorage = DivBlockStateStorage(),
    imageHolderFactory: DivImageHolderFactory,
    extensionHandlers: [DivExtensionHandler] = [],
    variablesStorage: DivVariablesStorage = DivVariablesStorage(),
    scheduler: Scheduling? = nil
  ) {
    self.init(
      viewId: DivViewId(cardId: cardId, additionalId: additionalId),
      stateManager: stateManager,
      actionHandler: nil,
      blockStateStorage: blockStateStorage,
      visibilityCounter: nil,
      lastVisibleBoundsCache: nil,
      imageHolderFactory: imageHolderFactory,
      divCustomBlockFactory: nil,
      fontProvider: nil,
      flagsInfo: .default,
      extensionHandlers: extensionHandlers.dictionary,
      functionsStorage: nil,
      variablesStorage: variablesStorage,
      triggersStorage: nil,
      playerFactory: nil,
      debugParams: DebugParams(),
      scheduler: scheduler,
      parentScrollView: nil,
      errorsStorage: nil,
      debugErrorCollector: nil,
      layoutDirection: .leftToRight,
      variableTracker: nil,
      persistentValuesStorage: nil,
      tooltipViewFactory: nil,
      layoutProviderHandler: nil,
      idToPath: nil,
      animatorController: nil
    )
  }

  init(
    viewId: DivViewId,
    stateManager: DivStateManager,
    actionHandler: DivActionHandler?,
    blockStateStorage: DivBlockStateStorage,
    visibilityCounter: DivVisibilityCounter?,
    lastVisibleBoundsCache: DivLastVisibleBoundsCache?,
    imageHolderFactory: DivImageHolderFactory,
    divCustomBlockFactory: DivCustomBlockFactory?,
    fontProvider: DivFontProvider?,
    flagsInfo: DivFlagsInfo,
    extensionHandlers: [String: DivExtensionHandler],
    functionsStorage: DivFunctionsStorage?,
    variablesStorage: DivVariablesStorage,
    triggersStorage: DivTriggersStorage?,
    playerFactory: PlayerFactory?,
    debugParams: DebugParams,
    scheduler: Scheduling?,
    parentScrollView: ScrollView?,
    errorsStorage: DivErrorsStorage?,
    debugErrorCollector: DebugErrorCollector?,
    layoutDirection: UserInterfaceLayoutDirection,
    variableTracker: DivVariableTracker?,
    persistentValuesStorage: DivPersistentValuesStorage?,
    tooltipViewFactory: DivTooltipViewFactory?,
    layoutProviderHandler: DivLayoutProviderHandler?,
    idToPath: IdToPath?,
    animatorController: DivAnimatorController?
  ) {
    self.viewId = viewId
    let parentPath = makeParentPath(viewId: viewId)
    self.parentPath = parentPath
    self.stateManager = stateManager
    self.actionHandler = actionHandler
    self.blockStateStorage = blockStateStorage
    self.visibilityCounter = visibilityCounter ?? DivVisibilityCounter()
    self.lastVisibleBoundsCache = lastVisibleBoundsCache ?? DivLastVisibleBoundsCache()
    self.imageHolderFactory = imageHolderFactory
    self.divCustomBlockFactory = divCustomBlockFactory ?? EmptyDivCustomBlockFactory()
    self.flagsInfo = flagsInfo
    self.fontProvider = fontProvider ?? DefaultFontProvider()
    self.playerFactory = playerFactory
    self.debugParams = debugParams
    self.scheduler = scheduler ?? TimerScheduler()
    self.parentScrollView = parentScrollView
    let errorsStorage = errorsStorage ?? DivErrorsStorage(errors: [])
    self.debugErrorCollector = debugErrorCollector
    self.errorsStorage = errorsStorage
    self.layoutDirection = layoutDirection
    self.variableTracker = variableTracker
    let persistentValuesStorage = persistentValuesStorage ?? DivPersistentValuesStorage()
    self.persistentValuesStorage = persistentValuesStorage
    self.tooltipViewFactory = tooltipViewFactory
    self.functionsStorage = functionsStorage
    self.variablesStorage = variablesStorage
    self.triggersStorage = triggersStorage
    self.extensionHandlers = extensionHandlers
    self.layoutProviderHandler = layoutProviderHandler
    self.functionsProvider = FunctionsProvider(
      persistentValuesStorage: persistentValuesStorage
    )
    self.idToPath = idToPath ?? IdToPath()
    self.animatorController = animatorController
    expressionResolver = makeExpressionResolver(
      functionsProvider: functionsProvider,
      viewId: viewId,
      path: parentPath,
      variablesStorage: variablesStorage,
      functionsStorage: functionsStorage,
      localValues: nil,
      variableTracker: variableTracker,
      errorsStorage: errorsStorage
    )
  }

  public var cardId: DivCardID {
    viewId.cardId
  }

  public func getExtensionHandlers(for div: DivBase) -> [DivExtensionHandler] {
    guard let extensions = div.extensions else {
      return []
    }
    return extensions.compactMap {
      let id = $0.id
      if !extensionHandlers.keys.contains(id) {
        addError(message: "No DivExtensionHandler for: \(id)")
      }
      return extensionHandlers[id]
    }
  }

  public func addError(message: String) {
    errorsStorage.add(DivBlockModelingError(message, path: parentPath))
  }

  public func addWarning(message: String) {
    errorsStorage.add(DivBlockModelingWarning(message, path: parentPath))
  }

  func addError(error: Error) {
    if let divError = error as? DivError {
      errorsStorage.add(divError)
      return
    }
    errorsStorage.add(DivUnknownError(error, path: parentPath))
  }

  func makeBinding<T>(variableName: String?, defaultValue: T) -> Binding<T> {
    guard let variableName else {
      return Binding(name: "", value: Property(initialValue: defaultValue))
    }

    let divVariableName = DivVariableName(rawValue: variableName)
    variableTracker?.onVariableUsed(id: viewId, variable: divVariableName)
    let value: T = variablesStorage
      .getVariableValue(path: parentPath, name: divVariableName) ?? defaultValue
    let valueProp = Property<T>(
      getter: { value },
      setter: {
        guard let newValue = DivVariableValue($0) else { return }
        self.variablesStorage.update(
          path: parentPath,
          name: divVariableName,
          value: newValue
        )
      }
    )
    return Binding(name: variableName, value: valueProp)
  }

  func modifying(
    elementId: String? = nil,
    cardLogId: String? = nil,
    parentPath: UIElementPath? = nil,
    parentDivStatePath: DivStatePath? = nil,
    errorsStorage: DivErrorsStorage? = nil,
    sizeModifier: DivSizeModifier? = nil,
    prototypeParams: PrototypeParams? = nil
  ) -> Self {
    var context = self
    context.elementId = elementId
    if let cardLogId {
      context.cardLogId = cardLogId
    }
    if let parentDivStatePath {
      context.parentDivStatePath = parentDivStatePath
    }
    if let sizeModifier {
      context.sizeModifier = sizeModifier
    }

    if parentPath == nil, errorsStorage == nil, prototypeParams == nil {
      return context
    }

    if let prototypeParams {
      var localValues = self.localValues
      localValues[prototypeParams.variableName] = prototypeParams.value
      localValues["index"] = prototypeParams.index
      context.localValues = localValues
    }

    context.parentPath = parentPath ?? self.parentPath
    context.errorsStorage = errorsStorage ?? self.errorsStorage
    context.expressionResolver = makeExpressionResolver(
      functionsProvider: functionsProvider,
      viewId: viewId,
      path: context.parentPath,
      variablesStorage: variablesStorage,
      functionsStorage: functionsStorage,
      localValues: context.localValues,
      variableTracker: variableTracker,
      errorsStorage: context.errorsStorage
    )

    return context
  }

  func cloneForTooltip(tooltipId: String) -> Self {
    var context = self
    let viewId = DivViewId(cardId: cardId, additionalId: tooltipId)
    context.viewId = DivViewId(cardId: cardId, additionalId: tooltipId)
    context.parentPath = makeParentPath(viewId: viewId)
    return context
  }
}

struct PrototypeParams {
  let index: Int
  let variableName: String
  let value: DivDictionary
}

private func makeExpressionResolver(
  functionsProvider: FunctionsProvider,
  viewId: DivViewId,
  path: UIElementPath,
  variablesStorage: DivVariablesStorage,
  functionsStorage: DivFunctionsStorage?,
  localValues: [String: AnyHashable]?,
  variableTracker: DivVariableTracker?,
  errorsStorage: DivErrorsStorage
) -> ExpressionResolver {
  ExpressionResolver(
    functionsProvider: functionsProvider,
    customFunctionsStorageProvider: {
      functionsStorage?.getStorage(path: path, contains: $0)
    },
    variableValueProvider: {
      if let value = localValues?[$0] {
        return value
      }
      let variableName = DivVariableName(rawValue: $0)
      variableTracker?.onVariableUsed(id: viewId, variable: variableName)
      return variablesStorage.getVariableValue(path: path, name: variableName)
    },
    errorTracker: { error in
      errorsStorage.add(DivExpressionError(error, path: path))
    }
  )
}

private func makeParentPath(viewId: DivViewId) -> UIElementPath {
  let cardIdPath = UIElementPath(viewId.cardId.rawValue)
  return if let additionalId = viewId.additionalId {
    cardIdPath + additionalId
  } else {
    cardIdPath
  }
}

extension [DivExtensionHandler] {
  var dictionary: [String: DivExtensionHandler] {
    var extensionsHandlersDictionary = [String: DivExtensionHandler]()
    for extensionHandler in self {
      let id = extensionHandler.id
      if extensionsHandlersDictionary[id] != nil {
        DivKitLogger.failure("Duplicate DivExtensionHandler for: \(id)")
        continue
      }
      extensionsHandlersDictionary[id] = extensionHandler
    }
    return extensionsHandlersDictionary
  }
}
