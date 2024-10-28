import UIKit

import LayoutKit
import Serialization
import VGSL

@MainActor
final class DivBlockProvider {
  private let divKitComponents: DivKitComponents
  private let onCardSizeChanged: (DivCardID, DivViewSize) -> Void
  private let disposePool = AutodisposePool()

  private var divData: DivData? {
    didSet {
      guard oldValue !== divData else { return }
      update(reasons: [])
    }
  }

  private(set) var id: DivViewId!

  var cardId: DivCardID {
    id.cardId
  }

  private(set) var cardSize: DivViewSize? {
    didSet {
      if let cardSize, let id {
        onCardSizeChanged(id.cardId, cardSize)
      }
    }
  }

  private var debugParams = DebugParams()
  private var dataErrors = [DeserializationError]()

  private let measurements = DebugParams.Measurements(
    divDataParsingTime: TimeMeasure(),
    renderTime: TimeMeasure(),
    templateParsingTime: TimeMeasure()
  )

  @ObservableProperty
  private(set) var block: Block = noDataBlock {
    didSet {
      cardSize = DivViewSize(block: block)
    }
  }

  weak var parentScrollView: ScrollView? {
    didSet {
      guard oldValue !== parentScrollView else { return }
      update(reasons: [])
    }
  }

  var lastVisibleBounds: CGRect = .zero
  
  var accessibilityElementsStorage: DivAccessibilityElementsStorage?

  init(
    divKitComponents: DivKitComponents,
    onCardSizeChanged: @escaping (DivCardID, DivViewSize) -> Void
  ) {
    self.divKitComponents = divKitComponents
    self.onCardSizeChanged = onCardSizeChanged

    divKitComponents.updateCardSignal
      .addObserver { [weak self] reasons in
        self?.update(reasons: reasons)
      }
      .dispose(in: disposePool)
  }

  func setSource(
    _ source: DivViewSource,
    debugParams: DebugParams
  ) {
    id = source.id
    self.debugParams = debugParams

    switch source.kind {
    case let .data(data):
      update(data: data)
    case let .divData(divData):
      update(divData: divData)
    case let .json(json):
      update(json: json)
    }
  }

  func setSource(
    _ source: DivViewSource,
    debugParams: DebugParams
  ) async {
    id = source.id
    self.debugParams = debugParams

    switch source.kind {
    case let .data(data):
      await update(data: data)
    case let .divData(divData):
      update(divData: divData)
    case let .json(json):
      await update(json: json)
    }
  }

  private func update(data: Data) {
    dataErrors = []
    do {
      guard let jsonObj = try? JSONSerialization.jsonObject(with: data),
            let jsonDict = jsonObj as? [String: Any] else {
        throw DeserializationError.nestedObjectError(
          field: cardId.rawValue,
          error: .invalidJSONData(data: data)
        )
      }
      update(json: jsonDict)
    } catch {
      block = handleError(error: error)
    }
  }

  private func update(data: Data) async {
    dataErrors = []

    let result = await Task.detached {
      guard let jsonObj = try? JSONSerialization.jsonObject(with: data),
            let jsonDict = jsonObj as? [String: Any] else {
        throw await DeserializationError.nestedObjectError(
          field: self.cardId.rawValue,
          error: .invalidJSONData(data: data)
        )
      }
      return jsonDict
    }.result

    switch result {
    case let .success(jsonDict):
      await update(json: jsonDict)
    case let .failure(error):
      block = handleError(error: error)
    }
  }

  private func update(json: [String: Any]) {
    dataErrors = []
    do {
      let result = try parseDivDataWithTemplates(json, cardId: cardId)
      dataErrors.append(contentsOf: result.errorsOrWarnings?.asArray() ?? [])
      update(divData: result.value)
    } catch {
      block = handleError(error: error)
    }
  }

  private func update(json: [String: Any]) async {
    dataErrors = []
    do {
      let result = try await parseDivDataWithTemplates(json, cardId: cardId)
      dataErrors.append(contentsOf: result.errorsOrWarnings?.asArray() ?? [])
      update(divData: result.value)
    } catch {
      block = handleError(error: error)
    }
  }

  private func update(divData: DivData?) {
    guard divData !== self.divData else { return }
    block = noDataBlock
    guard let divData else {
      self.divData = nil
      return
    }
    if !id.isTooltip {
      divKitComponents.setVariablesAndTriggers(divData: divData, cardId: cardId)
      divKitComponents.setTimers(divData: divData, cardId: cardId)
    }
    self.divData = divData
  }

  private func update(reasons: [DivActionURLHandler.UpdateReason]) {
    guard var divData else {
      guard debugParams.isDebugInfoEnabled else { return }
      block = makeErrorsBlock(dataErrors)
      return
    }

    guard needUpdateBlock(reasons: reasons) else {
      return
    }

    reasons.compactMap { $0.patch(for: self.cardId) }.forEach {
      divData = divData.applyPatch(
        $0,
        callbacks: Callbacks(elementChanged: { [weak self] id in
          self?.divKitComponents.triggersStorage.reset(elementId: id)
        })
      )
      $0.onAppliedActions?.forEach { action in
        divKitComponents.actionHandler.handle(
          action,
          path: UIElementPath(cardId.rawValue),
          source: .callback,
          sender: nil
        )
      }
    }
    self.divData = divData
    let context = divKitComponents.makeContext(
      cardId: cardId,
      additionalId: id.additionalId,
      cachedImageHolders: block.getImageHolders(),
      debugParams: debugParams,
      parentScrollView: parentScrollView
    )
    dataErrors.forEach { context.errorsStorage.add($0) }
    do {
      block = try measurements.renderTime.updateMeasure {
        try divData.makeBlock(
          context: context
        )
      }
      accessibilityElementsStorage = context.accessibilityElementsStorage
      debugParams.processMeasurements((cardId: cardId, measurements: measurements))
      for error in context.errorsStorage.errors {
        divKitComponents.reporter.reportError(cardId: cardId, error: error)
      }
    } catch {
      divKitComponents.reporter.reportError(
        cardId: cardId,
        error: DivUnknownError(error, path: UIElementPath(cardId.rawValue))
      )
      block = handleError(error: error, context: context)
    }
  }

  private func needUpdateBlock(reasons: [DivActionURLHandler.UpdateReason]) -> Bool {
    guard !reasons.isEmpty else { return true }
    for reason in reasons {
      let cardId: DivCardID
      switch reason {
      case let .patch(divCardID, _):
        cardId = divCardID
      case let .timer(divCardID):
        cardId = divCardID
      case let .state(divCardID):
        cardId = divCardID
      case .external:
        return true
      case let .variable(cardIds):
        if cardIds.keys.contains(self.cardId) {
          return true
        }
        continue
      }
      if cardId == self.cardId {
        return true
      }
    }
    return false
  }

  func update(withStates blockStates: BlocksState) {
    do {
      block = try block.updated(withStates: blockStates)
    } catch {
      block = handleError(error: error)
    }
  }

  func update(path: UIElementPath, isFocused: Bool) {
    do {
      block = try block.updated(path: path, isFocused: isFocused)
    } catch {
      block = handleError(error: error)
    }
  }

  private func parseDivDataWithTemplates(
    _ jsonDict: [String: Any],
    cardId: DivCardID
  ) throws -> DeserializationResult<DivData> {
    let rawDivData = try RawDivData(dictionary: jsonDict)
    let templates = try measurements.templateParsingTime.updateMeasure {
      DivTemplates(dictionary: rawDivData.templates)
    }
    return try measurements.divDataParsingTime.updateMeasure {
      templates
        .parseValue(type: DivDataTemplate.self, from: rawDivData.card)
        .asCardResult(cardId: cardId)
    }
  }

  private func parseDivDataWithTemplates(
    _ jsonDict: [String: Any],
    cardId: DivCardID
  ) async throws -> DeserializationResult<DivData> {
    let rawDivData = try RawDivData(dictionary: jsonDict)
    let measurements = measurements
    let templates = try measurements.templateParsingTime.updateMeasure {
      DivTemplates(dictionary: rawDivData.templates)
    }
    let result = try await withCheckedThrowingContinuation { continuation in
      DispatchQueue.global().async { [measurements] in
        do {
          let result = try measurements.divDataParsingTime.updateMeasure {
            templates
              .parseValue(type: DivDataTemplate.self, from: rawDivData.card)
              .asCardResult(cardId: cardId)
          }
          continuation.resume(returning: result)
        } catch {
          continuation.resume(throwing: error)
        }
      }
    }
    return result
  }

  private func handleError(
    error: Error,
    context: DivBlockModelingContext? = nil
  ) -> Block {
    var errors: [DivError] = []
    if let divError = error as? DivError {
      errors.append(divError)
    } else {
      errors.append(DivUnknownError(error, path: UIElementPath(cardId.rawValue)))
    }
    errors.append(contentsOf: context?.errorsStorage.errors ?? dataErrors)
    for item in errors {
      divKitComponents.reporter.reportError(cardId: cardId, error: item)
    }

    if debugParams.isDebugInfoEnabled {
      return makeErrorsBlock(errors)
    } else {
      return noDataBlock
    }
  }

  private func makeErrorsBlock(_ errors: [DivError]) -> Block {
    guard !errors.isEmpty, !errors.isNoDataError else {
      return noDataBlock
    }
    let headerTypo = Typo(size: 18, weight: .bold)
    let errorsHeader = TextBlock(
      widthTrait: .resizable,
      text: "Errors: \(errors.count)".with(typo: headerTypo)
    )

    let errorBlockTypo = Typo(size: 14, weight: .regular)
    let errorBlocks = errors.map {
      TextBlock(
        widthTrait: .resizable,
        text: $0.prettyMessage.with(typo: errorBlockTypo)
      )
    }
    return try! GalleryBlock(
      gaps: [0] + Array(repeating: 10, times: UInt(errorBlocks.count + 1)),
      children: [errorsHeader] + errorBlocks,
      path: UIElementPath(cardId.rawValue),
      direction: .vertical,
      crossAlignment: .leading,
      widthTrait: .resizable,
      heightTrait: .resizable
    ).addingDecorations(forceWrapping: true).addingEdgeGaps(15)
  }
}

private let noDataBlock = EmptyBlock.zeroSized

extension DivActionURLHandler.UpdateReason {
  fileprivate func patch(for divCardId: DivCardID) -> DivPatch? {
    switch self {
    case let .patch(cardId, patch):
      cardId == divCardId ? patch : nil
    case .timer, .variable, .state, .external:
      nil
    }
  }
}

extension [DivError] {
  fileprivate var isNoDataError: Bool {
    if count == 1,
       let deserializationError = first as? DeserializationError,
       case .noData = deserializationError {
      return true
    }
    return false
  }
}
