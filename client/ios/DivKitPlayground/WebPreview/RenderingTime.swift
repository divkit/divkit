import DivKit
import LayoutKit

extension DebugParams.Measurements {
  var renderingTime: UIStatePayload.RenderingTime {
    UIStatePayload.RenderingTime(
      div_render_total: renderTime.webTime,
      div_parsing_data: divDataParsingTime.webTime,
      div_parsing_templates: templateParsingTime.webTime
    )
  }
}

extension TimeMeasure {
  fileprivate var webTime: UIStatePayload.RenderingTime.Time {
    .init(value: time?.value ?? 0, histogram_type: time?.status.histogramType ?? .cold)
  }
}

extension TimeMeasure.Status {
  fileprivate var histogramType: UIStatePayload.RenderingTime.HistogramType {
    switch self {
    case .cold:
      return .cold
    case .warm:
      return .warm
    }
  }
}
