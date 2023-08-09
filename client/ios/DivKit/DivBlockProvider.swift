import UIKit

import BaseUIPublic
import CommonCorePublic
import LayoutKit
import Serialization

public final class DivBlockProvider {
  public struct Source {
    public enum Kind {
      case json([String: Any])
      case data(Data)
      case divData(DivData)
    }

    let kind: Kind
    let cardId: DivCardID

    public init(
      kind: Kind,
      cardId: DivCardID
    ) {
      self.kind = kind
      self.cardId = cardId
    }
  }

  private let divKitComponents: DivKitComponents
  private let disposePool = AutodisposePool()

  private var divData: DivData? {
    didSet {
      guard oldValue !== divData else { return }
      update(reasons: [])
    }
  }

  private var cardId: DivCardID!
  private var debugParams: DebugParams = .init()
  private var dataErrors: [DeserializationError] = []

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

  init(divKitComponents: DivKitComponents) {
    self.divKitComponents = divKitComponents

    divKitComponents.updateCardSignal
      .addObserver { [weak self] in self?.update(reasons: $0) }.dispose(in: disposePool)
  }

  func setSource(
    _ source: Source,
    debugParams: DebugParams,
    shouldResetPreviousCardData: Bool
  ) {
    if shouldResetPreviousCardData {
      cardId.flatMap(divKitComponents.reset(cardId:))
    }

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
      block = handleError(error: error, message: "Failed to parse DivData")
    }
  }

  private func update(json: [String: Any]) {
    dataErrors = []
    do {
      let result = try parseDivDataWithTemplates(json, cardId: cardId)
      dataErrors.append(contentsOf: result.errorsOrWarnings?.asArray() ?? [])
      divData = result.value
    } catch {
      block = handleError(error: error, message: "Failed to parse DivData")
    }
  }

  private func update(reasons: [DivActionURLHandler.UpdateReason]) {
    guard var divData = divData else {
      guard debugParams.isDebugInfoEnabled else { return }
      block = makeErrorsBlock(dataErrors.map(errorMessage(_:)))
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
    do {
      block = try measurements.renderTime.updateMeasure {
        try divData.makeBlock(
          context: context
        )
      }
      let errors: [DivError] = dataErrors + context.errorsStorage.errors
      debugParams.processMeasurements((cardId: cardId, measurements: measurements))
      debugParams.processErrors((cardId: cardId, errors: errors))
    } catch {
      block = handleError(error: error, message: "Failed to build block", context: context)
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
      block = handleError(error: error, message: "Failed to update block")
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
      let result = templates.parseValue(type: DivDataTemplate.self, from: rawDivData.card)
      if let divData = result.value {
        divKitComponents.setVariablesAndTriggers(divData: divData, cardId: cardId)
        divKitComponents.setTimers(divData: divData, cardId: cardId)
      }
      return result
    }
  }

  private func handleError(
    error: Error,
    message: String,
    context: DivBlockModelingContext? = nil
  ) -> Block {
    guard debugParams.isDebugInfoEnabled else { return noDataBlock }

    var errors: [DivError] = dataErrors
    errors.append(error as DivError)
    errors.append(contentsOf: context?.errorsStorage.errors ?? [])
    DivKitLogger.error("\(message). Error: \(error).")
    debugParams.processErrors((cardId: cardId, errors: errors))
    return makeErrorsBlock(errors.map(errorMessage(_:)))
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

private func makeErrorsBlock(_ errors: [String]) -> Block {
  guard !errors.isEmpty else {
    return EmptyBlock.zeroSized
  }

  let separator = SeparatorBlock(color: .gray, direction: .horizontal)
  let headerTypo = Typo(size: 18, weight: .bold)
  let errorsHeader = TextBlock(
    widthTrait: .resizable,
    text: "Errors: \(errors.count)".with(typo: headerTypo)
  ).addingEdgeGaps(10)

  let errorBlockTypo = Typo(size: 14, weight: .regular)
  let errorBlocks = errors.map {
    TextBlock(
      widthTrait: .resizable,
      text: $0.with(typo: errorBlockTypo)
    ).addingEdgeGaps(10)
  }
  return try! ContainerBlock(
    layoutDirection: .vertical,
    children: [separator, errorsHeader] + errorBlocks
  )
}

private func errorMessage(_ error: DivError) -> String {
  let message: String
  let stack: [String]
  let additional: [String: String]

  switch error {
  case let deserializationError as DeserializationError:
    message = deserializationError.errorMessage
    stack = deserializationError.stack
    additional = deserializationError.userInfo
  default:
    message = (error as DivError).description
    stack = []
    additional = [:]
  }

  return "\(message)\nPath: \(stack.isEmpty ? "nil" : stack.joined(separator: "/"))"
    + (additional.isEmpty ? "" : "\nAdditional: \(additional)")
}

extension DeserializationError {
  fileprivate var stack: [String] {
    switch self {
    case let .nestedObjectError(field, error):
      return [field] + error.stack
    default:
      return []
    }
  }
}

extension Block {
  fileprivate func addingTime(
    dataParsing: TimeMeasure.Time?,
    templateParsing: TimeMeasure.Time?,
    render: TimeMeasure.Time?
  ) -> Block {
    guard let render = render,
          let dataParsing = dataParsing,
          let templateParsing = templateParsing else {
      return self
    }

    let text =
      """
      Rendering time:

      - Div.Render.Total.\(render.description) ms
      - Div.Parsing.Data.\(dataParsing.description) ms
      - Div.Parsing.Templates.\(templateParsing.description) ms
      """

    let perfTypo = Typo(size: 18, weight: .regular)
    let textBlock = TextBlock(
      widthTrait: .resizable,
      text: text.with(typo: perfTypo)
    ).addingEdgeGaps(20)

    let block = try? ContainerBlock(
      layoutDirection: .vertical,
      children: [self, textBlock]
    )

    return block ?? self
  }
}
