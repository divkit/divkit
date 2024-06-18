import BasePublic
import BaseUIPublic

struct FontParams: Hashable {
  let family: String
  let weight: Int
  let size: Int
  let unit: DivSizeUnit
  let featureSettings: String?
}

protocol FontParamsProvider {
  func resolveFontFamily(_ expressionResolver: ExpressionResolver) -> String?
  func resolveFontWeight(_ expressionResolver: ExpressionResolver) -> DivFontWeight
  func resolveFontWeightValue(_ expressionResolver: ExpressionResolver) -> Int?
  func resolveFontSize(_ resolver: ExpressionResolver) -> Int
  func resolveFontSizeUnit(_ resolver: ExpressionResolver) -> DivSizeUnit
  func resolveFontFeatureSettings(_ resolver: ExpressionResolver) -> String?
}

extension FontParamsProvider {
  func resolveFontParams(_ expressionResolver: ExpressionResolver) -> FontParams {
    FontParams(
      family: resolveFontFamily(expressionResolver) ?? "",
      weight: resolveFontWeightValue(expressionResolver)
        ?? resolveFontWeight(expressionResolver).toInt(),
      size: resolveFontSize(expressionResolver),
      unit: resolveFontSizeUnit(expressionResolver),
      featureSettings: resolveFontFeatureSettings(expressionResolver)
    )
  }

  func resolveFontFeatureSettings(_: ExpressionResolver) -> String? {
    nil
  }
}

extension DivFontProvider {
  func font(_ params: FontParams) -> Font {
    let font = font(
      family: params.family,
      weight: params.weight,
      size: params.unit.makeScaledValue(params.size)
    )
    if let featureSettings = params.featureSettings {
      return font.withFontFeatureSettings(featureSettings)
    }
    return font
  }
}
