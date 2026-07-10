#if os(iOS)
import Foundation
@testable import LayoutKit
import Testing

@Suite
struct TooltipStatesStoreTests {
  @Test
  func onlyOneConcurrentReserveSucceeds_removeClearsReservation() {
    let storage = DefaultTooltipManager.TooltipStates()
    let identity = TooltipIdentity(id: "tooltip_id", scopePath: nil)

    var results = [Bool](repeating: false, count: 10)
    DispatchQueue.concurrentPerform(iterations: 10) { index in
      results[index] = storage.tryReserve(identity)
    }

    #expect(results.filter(\.self).count == 1)
    #expect(storage.isReserved(identity))

    storage.remove(identity)
    #expect(!storage.isReserved(identity))
  }
}
#endif
