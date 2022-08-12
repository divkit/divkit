// Copyright 2018 Yandex LLC. All rights reserved.

import CoreGraphics

import BaseUI
import LayoutKit
import Networking

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
  public let divCustomBlockFactory: DivCustomBlockFactory
  public let fontSpecifiers: FontSpecifiers
  public let flagsInfo: DivFlagsInfo
  public let extensionHandlers: [String: DivExtensionHandler]
  public let stateInterceptors: [String: DivStateInterceptor]
  public let expressionResolver: ExpressionResolver
  public let showDebugInfo: ((ViewType) -> Void)?
  public var childrenA11yDescription: String?

  #if INTERNAL_BUILD
  let blockModelingErrorsStorage: ExpressionErrorsStorage
  #endif

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
    divCustomBlockFactory: DivCustomBlockFactory = EmptyDivCustomBlockFactory(),
    fontSpecifiers: FontSpecifiers = BaseUI.fontSpecifiers,
    flagsInfo: DivFlagsInfo = .default,
    extensionHandlers: [DivExtensionHandler] = [],
    stateInterceptors: [DivStateInterceptor] = [],
    variables: DivVariables = [:],
    showDebugInfo: ((ViewType) -> Void)? = nil,
    childrenA11yDescription: String? = nil
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
    self.divCustomBlockFactory = divCustomBlockFactory
    self.flagsInfo = flagsInfo
    self.fontSpecifiers = fontSpecifiers
    self.showDebugInfo = showDebugInfo
    self.childrenA11yDescription = childrenA11yDescription

    #if INTERNAL_BUILD
    let blockModelingErrorsStorage = ExpressionErrorsStorage()
    self.expressionResolver = ExpressionResolver(
      variables: variables,
      errorTracker: { [weak blockModelingErrorsStorage] error in
        blockModelingErrorsStorage?.add(error: error)
      }
    )
    self.blockModelingErrorsStorage = blockModelingErrorsStorage
    #else
    self.expressionResolver = ExpressionResolver(variables: variables)
    #endif

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
      let handler = extensionHandlers[id]
      if handler == nil {
        DivKitLogger.error("No DivExtensionHandler for: \(id)")
      }
      return handler
    }
  }

  public func getStateInterceptor(for divState: DivState) -> DivStateInterceptor? {
    divState.extensions?.compactMap { stateInterceptors[$0.id] }.first
  }
}
