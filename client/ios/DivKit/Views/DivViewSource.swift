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

  let kind: Kind
  let cardId: DivCardID

  /// Initializes a new ``DivViewSource``.
  ///
  /// - Parameters:
  ///   - kind: The kind of data source, represented by the ``Kind`` enum.
  ///   - cardId: A unique identifier for the associated `DivKit` card.
  public init(
    kind: Kind,
    cardId: DivCardID
  ) {
    self.kind = kind
    self.cardId = cardId
  }
}
