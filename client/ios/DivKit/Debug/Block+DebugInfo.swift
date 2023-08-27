import CoreGraphics
import Foundation

import BaseUIPublic
import CommonCorePublic
import LayoutKit

extension Block {
  func addingDebugInfo(context: DivBlockModelingContext) -> Block {
    let debugParams = context.debugParams
    guard debugParams.isDebugInfoEnabled else {
      return self
    }

    let errors = context.errorsStorage.errors
    let errorsCount = errors.count
    guard errorsCount > 0 else {
      return self
    }

    let counterText = errorsCount > maxCount
      ? "\(maxCount)+"
      : "\(errorsCount)"

    let typo = Typo(font: context.fontProvider.font(size: 14))
      .with(color: .white)

    let action = UserInterfaceAction(
      payload: .url(DebugInfoBlock.showOverlayURL),
      path: context.parentPath + "div_errors_indicator",
      accessibilityElement: nil
    )

    let counter = TextBlock(
      widthTrait: .intrinsic,
      text: counterText.with(typo: typo)
    )

    let indicator = counter
      .addingVerticalGaps(errorsButtonCounterGaps)
      .addingHorizontalGaps(calculateCounterHorizontalGaps(counter: counter))
      .addingDecorations(
        boundary: .clipCorner(radius: 10),
        backgroundColor: .red
      )
      .addingEdgeInsets(debugParams.errorCounterInsets)
      .addingEdgeGaps(2)
      .addingAccessibilityID(withTraits: (
        "divLayoutErrorCounter",
        .button
      ))
      .addingDecorations(action: action)

    let debugInfoBlock = DebugInfoBlock(
      child: indicator,
      showDebugInfo: {
        #if os(iOS)
        debugParams.showDebugInfo(ErrorListView(errors: errors.map { $0.prettyMessage }))
        #else
        return
        #endif
      }
    )

    let block = LayeredBlock(
      widthTrait: calculatedWidthTrait,
      heightTrait: calculatedHeightTrait,
      children: [self, debugInfoBlock]
    )

    return block
  }
}

private func calculateCounterHorizontalGaps(counter: Block) -> CGFloat {
  let additionalGap = (counter.intrinsicSize.height - counter.intrinsicSize.width) / 2
  return max(
    errorsButtonCounterGaps,
    errorsButtonCounterGaps + additionalGap
  )
}

private let errorsButtonCounterGaps: CGFloat = 4
private let maxCount = 9999
