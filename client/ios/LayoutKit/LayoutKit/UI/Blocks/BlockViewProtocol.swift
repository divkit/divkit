#if os(iOS)
import Foundation
import VGSL

public protocol BlockViewProtocol: AnyObject, VisibleBoundsTracking,
  EffectiveBackgroundColorProviding, LayoutReporterProvider {}
#endif
