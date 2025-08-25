#if os(iOS)
import Foundation
import VGSLUI

struct MaskRun: Equatable {
  let mask: TextMask
  let rect: CGRect

  init?(run: StringLayout.Run) {
    guard let mask = run.action.mask else { return nil }
    self.mask = mask
    self.rect = run.rect
  }
}

extension StringLayout {
  var runsWithMasks: [MaskRun] {
    runsWithAction.compactMap { MaskRun(run: $0) }
  }
}
#endif
