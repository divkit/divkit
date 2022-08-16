import Foundation

import Base
import BaseUI
import LayoutKit
import Networking
import Serialization

public final class DivKitComponents {
  public let actionHandler: DivActionHandler
  public let blockStateStorage = DivBlockStateStorage()
  public let divCustomBlockFactory: DivCustomBlockFactory
  public var extensionHandlers: [DivExtensionHandler]
  public let flagsInfo: DivFlagsInfo
  public let fontSpecifiers: FontSpecifiers
  public let imageHolderFactory: ImageHolderFactory
  public let patchProvider: DivPatchProvider
  public let stateManager = DivStateManager()
  public let triggersStorage: DivTriggersStorage
  public let urlOpener: UrlOpener
  public let variablesStorage: DivVariablesStorage
  public let visibilityCounter = DivVisibilityCounter()
  public let showDebugInfo: ((ViewType) -> Void)?

  public init(
    divCustomBlockFactory: DivCustomBlockFactory = EmptyDivCustomBlockFactory(),
    extensionHandlers: [DivExtensionHandler] = [],
    flagsInfo: DivFlagsInfo = .default,
    fontSpecifiers: FontSpecifiers = BaseUI.fontSpecifiers,
    imageHolderFactory: ImageHolderFactory? = nil,
    patchProvider: DivPatchProvider? = nil,
    updateCardAction: DivActionURLHandler.UpdateCardAction?,
    urlOpener: @escaping UrlOpener,
    variablesStorage: DivVariablesStorage = DivVariablesStorage(),
    showDebugInfo: ((ViewType) -> Void)? = nil
  ) {
    self.divCustomBlockFactory = divCustomBlockFactory
    self.extensionHandlers = extensionHandlers
    self.flagsInfo = flagsInfo
    self.fontSpecifiers = fontSpecifiers
    self.urlOpener = urlOpener
    self.variablesStorage = variablesStorage
    self.showDebugInfo = showDebugInfo

    let requestPerformer = URLRequestPerformer(urlTransform: nil)

    self.imageHolderFactory = imageHolderFactory
      ?? makeImageHolderFactory(requestPerformer: requestPerformer)

    self.patchProvider = patchProvider
      ?? DivPatchDownloader(requestPerformer: requestPerformer)

    actionHandler = DivActionHandler(
      stateUpdater: DivStateUpdaterImpl(
        stateManager: stateManager
      ),
      blockStateStorage: blockStateStorage,
      patchProvider: self.patchProvider,
      variablesStorage: variablesStorage,
      updateCard: { updateCardAction?($0, $1) },
      showTooltip: { _ in },
      logger: DefaultDivActionLogger(
        requestPerformer: requestPerformer
      )
    )

    triggersStorage = DivTriggersStorage(
      variablesStorage: variablesStorage,
      actionHandler: actionHandler,
      urlOpener: urlOpener
    )
  }

  public func reset() {
    patchProvider.cancelRequests()

    blockStateStorage.reset()
    stateManager.reset()
    visibilityCounter.reset()
  }

  public func parseDivData(_ jsonData: Data, cardId: DivCardID) throws -> DivData {
    let divData = try jsonData.parseDivData()
    setVariablesAndTriggers(divData: divData, cardId: cardId)
    return divData
  }

  public func parseDivData(
    _ dataDict: [String: Any],
    cardId: DivCardID
  ) -> DeserializationResult<DivData> {
    let divData = DivData.resolve(card: dataDict, templates: nil)
    setVariablesAndTriggers(divData: divData.value, cardId: cardId)
    return divData
  }

  public func makeContext(
    cardId: DivCardID,
    cachedImageHolders: [ImageHolder]
  ) -> DivBlockModelingContext {
    DivBlockModelingContext(
      cardId: cardId,
      stateManager: stateManager,
      blockStateStorage: blockStateStorage,
      visibilityCounter: visibilityCounter,
      imageHolderFactory: imageHolderFactory
        .withInMemoryCache(cachedImageHolders: cachedImageHolders),
      divCustomBlockFactory: divCustomBlockFactory,
      fontSpecifiers: fontSpecifiers,
      flagsInfo: flagsInfo,
      extensionHandlers: extensionHandlers,
      variables: variablesStorage.makeVariables(for: cardId),
      showDebugInfo: showDebugInfo
    )
  }

  public func handleActions(params: UserInterfaceAction.DivActionParams) {
    actionHandler.handle(params: params, urlOpener: urlOpener)
  }

  private func setVariablesAndTriggers(divData: DivData?, cardId: DivCardID) {
    let divDataVariables = divData?.variables?.extractDivVariableValues() ?? [:]
    variablesStorage.append(
      variables: divDataVariables,
      for: cardId,
      replaceExisting: false
    )

    triggersStorage.set(
      cardId: cardId,
      triggers: divData?.variableTriggers ?? []
    )
  }
}

extension Data {
  fileprivate func parseDivData() throws -> DivData {
    guard let jsonObj = try? JSONSerialization.jsonObject(with: self),
          let jsonDict = jsonObj as? [String: Any] else {
      throw DeserializationError.invalidJSONData(data: self)
    }

    let rawDivData = try RawDivData(dictionary: jsonDict)
    return try DivData.resolve(
      card: rawDivData.card,
      templates: rawDivData.templates
    ).unwrap()
  }
}

private class DivStateUpdaterImpl: DivStateUpdater {
  private let stateManager: DivStateManager

  init(stateManager: DivStateManager) {
    self.stateManager = stateManager
  }

  func set(
    path: DivStatePath,
    cardId _: DivCardID,
    lifetime _: DivStateLifetime
  ) {
    if let (parentPath, stateId) = path.split() {
      stateManager.setStateWithHistory(path: parentPath, stateID: stateId)
    }
  }
}

func makeImageHolderFactory(requestPerformer: URLRequestPerformer) -> ImageHolderFactory {
  ImageHolderFactory(
    requester: NetworkURLResourceRequester(
      performer: requestPerformer
    ),
    imageProcessingQueue: OperationQueue(
      name: "tech.divkit.image-processing",
      qos: .userInitiated
    )
  )
}
