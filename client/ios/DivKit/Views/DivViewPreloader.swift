import Foundation
import VGSL

/// Utility for preloading the contents of `DivView'.
///
/// ``DivViewPreloader`` can be useful when it is important to calculate layout before building
/// ``DivView``, for example in `UICollectionView`.
@MainActor
public final class DivViewPreloader {
  /// Represents a change in estimated size for a specific ``DivView``.
  public struct DivViewSizeChange {
    public let cardId: DivCardID
    public let estimatedSize: DivViewSize
  }

  private var blockProviders = [DivCardID: DivBlockProvider]()
  private let divKitComponents: DivKitComponents
  private let changeEventsPipe = SignalPipe<DivViewSizeChange>()
  var changeEvents: Signal<DivViewSizeChange> {
    changeEventsPipe.signal
  }

  /// Initializes a new ``DivViewPreloader`` instance.
  ///
  /// - Parameters:
  ///  - divKitComponents: The ``DivKitComponents`` instance used for creating `DivBlockProvider`.
  public init(divKitComponents: DivKitComponents) {
    self.divKitComponents = divKitComponents
  }

  func blockProvider(for cardId: DivCardID) -> DivBlockProvider {
    if let blockProvider = blockProviders[cardId] {
      return blockProvider
    } else {
      let blockProvider = DivBlockProvider(divKitComponents: divKitComponents) { [weak self] in
        self?.changeEventsPipe.send(DivViewSizeChange(cardId: $0, estimatedSize: $1))
      }
      blockProviders[cardId] = blockProvider
      return blockProvider
    }
  }

  /// Sets the source for  ``DivViewPreloader`` and updates the layout.
  /// - Parameters:
  /// - source: The source of the ``DivView``.
  /// - debugParams: Optional debug configurations for the ``DivView``.
  public func setSource(
    _ source: DivViewSource,
    debugParams: DebugParams = DebugParams()
  ) async {
    let blockProvider: DivBlockProvider = blockProvider(for: source.id.cardId)

    await blockProvider.setSource(
      source,
      debugParams: debugParams
    )

    blockProviders[source.id.cardId] = blockProvider
  }

  /// Sets the source for  ``DivViewPreloader`` and updates the layout.
  /// - Parameters:
  /// - source: The source of the ``DivView``.
  /// - debugParams: Optional debug configurations for the ``DivView``.
  @_spi(Legacy)
  public func setSource(
    _ source: DivViewSource,
    debugParams: DebugParams = DebugParams()
  ) {
    let blockProvider: DivBlockProvider = blockProvider(for: source.id.cardId)

    blockProvider.setSource(
      source,
      debugParams: debugParams
    )

    blockProviders[source.id.cardId] = blockProvider
  }

  /// Sets the sources for  ``DivViewPreloader`` and updates the layout.
  /// - Parameters:
  /// - sources: The sources of the ``DivView``.
  /// - debugParams: Optional debug configurations for the ``DivView``.
  public func setSources(
    _ sources: [DivViewSource],
    debugParams: DebugParams = DebugParams()
  ) async {
    let blockProviders = sources.map { blockProvider(for: $0.id.cardId) }
    await withTaskGroup(of: Void.self) { group in
      zip(blockProviders, sources).forEach { blockProvider, source in
        group.addTask {
          await blockProvider.setSource(source, debugParams: debugParams)
        }
      }
    }
  }

  /// Fetches the expected size for a ``DivView`` with a specific identifier.
  ///
  /// - Parameters:
  /// - cardId: The unique identifier of the desired ``DivView``.
  ///
  /// - Returns: An optional `DivViewSize` representing the expected size if available, otherwise
  /// nil.
  @MainActor
  public func expectedSize(for cardId: DivCardID) -> DivViewSize? {
    blockProvider(for: cardId).cardSize
  }

  /// Adds an observer to listen for any ``DivView`` estimated size changes.
  ///
  /// - Parameters:
  /// - onCardSizeChanged: A closure that gets invoked whenever a ``DivView`` estimated size
  /// changes.
  ///
  /// - Returns: A `Disposable` which can be used to unregister the observer when it's no longer
  /// needed.
  public func addObserver(_ onCardSizeChanged: @escaping (DivViewSizeChange) -> Void)
    -> Disposable {
    changeEvents.addObserver(onCardSizeChanged)
  }

  func reset(cardId: DivCardID) {
    blockProviders.removeValue(forKey: cardId)
  }
}
