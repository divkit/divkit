import BaseTinyPublic
import Foundation

/// A representation of the source from which a ``DivView`` can load content.
public struct DivViewSource {
  /// Specifies the type of the source for the `DivView`.
  public enum Kind {
    case json([String: Any])
    case data(Data)
    case divData(DivData)
  }

  let id: DivViewId
  let kind: Kind

  /// Initializes a new ``DivViewSource``.
  ///
  /// - Parameters:
  ///   - kind: The kind of data source, represented by the ``Kind`` enum.
  ///   - cardId: A unique identifier for the associated `DivKit` card.
  public init(
    kind: Kind,
    cardId: DivCardID
  ) {
    self.init(kind: kind, cardId: cardId, additionalId: nil)
  }

  init(
    kind: Kind,
    cardId: DivCardID,
    additionalId: String?
  ) {
    id = DivViewId(cardId: cardId, additionalId: additionalId)
    self.kind = kind
  }
}

struct DivViewId: Hashable {
  let cardId: DivCardID
  let additionalId: String?
}
