import UIKit

import BaseUIPublic
import CommonCorePublic
import LayoutKit
import Serialization

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

  private(set) var cardId: DivCardID!
  private(set) var cardSize: DivViewSize? {
    didSet {
      guard let cardSize else { return }
      onCardSizeChanged(cardId, cardSize)
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
  private(set) var block: Block = noDataBlock
  weak var parentScrollView: ScrollView? {
    didSet {
      guard oldValue !== parentScrollView else { return }
      update(reasons: [])
    }
  }

  var lastVisibleBounds: CGRect = .zero

  init(
    divKitComponents: DivKitComponents,
    onCardSizeChanged: @escaping (DivCardID, DivViewSize) -> Void
  ) {
    self.divKitComponents = divKitComponents
    self.onCardSizeChanged = onCardSizeChanged

    divKitComponents.updateCardSignal
      .addObserver { [weak self] in self?.update(reasons: $0) }
      .dispose(in: disposePool)
  }

  func setSource(
    _ source: DivViewSource,
    debugParams: DebugParams
  ) {
    block = noDataBlock

    cardId = source.cardId
    self.debugParams = debugParams
    switch source.kind {
    case let .data(data): update(data: data)
    case let .divData(divData): self.divData = divData
    case let .json(json): update(json: json)
    }
  }

  private func update(data: Data) {
    dataErrors = []
    do {
      let result = try divKitComponents.parseDivDataWithTemplates(data, cardId: cardId)
      dataErrors.append(contentsOf: result.errorsOrWarnings?.asArray() ?? [])
      divData = result.value
    } catch {
      block = handleError(error: error)
    }
  }

  private func update(json: [String: Any]) {
    dataErrors = []
    do {
      let result = try parseDivDataWithTemplates(json, cardId: cardId)
      dataErrors.append(contentsOf: result.errorsOrWarnings?.asArray() ?? [])
      divData = result.value
    } catch {
      block = handleError(error: error)
    }
  }

  private func update(reasons: [DivActionURLHandler.UpdateReason]) {
    guard var divData else {
      guard debugParams.isDebugInfoEnabled else { return }
      block = makeErrorsBlock(dataErrors.map(\.prettyMessage))
      return
    }

    guard needUpdateBlock(reasons: reasons) else { return }

    reasons.compactMap { $0.patch(for: self.cardId) }.forEach {
      divData = divData.applyPatch($0)
    }
    self.divData = divData

    let context = divKitComponents.makeContext(
      cardId: cardId,
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
      debugParams.processMeasurements((cardId: cardId, measurements: measurements))
      context.errorsStorage.errors.forEach {
        divKitComponents.reporter.reportError(cardId: cardId, error: $0)
      }
    } catch {
      divKitComponents.reporter.reportError(
        cardId: cardId,
        error: DivUnknownError(error, path: UIElementPath(cardId.rawValue))
      )
      block = handleError(error: error, context: context)
    }
    cardSize = DivViewSize(block: block)
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
      case let .variable(affectedCards):
        if case let .specific(cardIds) = affectedCards,
           cardIds.contains(self.cardId) || affectedCards == .all {
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

  private func parseDivDataWithTemplates(
    _ jsonDict: [String: Any],
    cardId: DivCardID
  ) throws -> DeserializationResult<DivData> {
    let rawDivData = try RawDivData(dictionary: jsonDict)
    let templates = try measurements.templateParsingTime.updateMeasure {
      DivTemplates(dictionary: rawDivData.templates)
    }
    return try measurements.divDataParsingTime.updateMeasure {
      let result = templates
        .parseValue(type: DivDataTemplate.self, from: rawDivData.card)
        .asCardResult(cardId: cardId)
      if let divData = result.value {
        divKitComponents.setVariablesAndTriggers(divData: divData, cardId: cardId)
        divKitComponents.setTimers(divData: divData, cardId: cardId)
      }
      return result
    }
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
    errors.forEach {
      divKitComponents.reporter.reportError(cardId: cardId, error: $0)
    }

    if debugParams.isDebugInfoEnabled {
      return makeErrorsBlock(errors.map(\.prettyMessage))
    } else {
      return noDataBlock
    }
  }

  private func makeErrorsBlock(_ errors: [String]) -> Block {
    guard !errors.isEmpty else {
      return EmptyBlock.zeroSized
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
        text: $0.with(typo: errorBlockTypo)
      )
    }
    return try! GalleryBlock(
      gaps: [0] + Array(repeating: 10, count: errorBlocks.count + 1),
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
      return cardId == divCardId ? patch : nil
    case .timer, .variable, .state:
      return nil
    }
  }
}
