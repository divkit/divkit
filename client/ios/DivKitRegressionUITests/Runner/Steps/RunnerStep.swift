enum RunnerStep: Decodable {
  case tap(Tap)
  case longTap(LongTap)
  case doubleTap(DoubleTap)
  case verifyText(VerifyText)
  case divAction(DivAction)

  enum StepType: String, Decodable {
    case tap
    case longTap = "long_tap"
    case doubleTap = "double_tap"
    case verifyText = "verify_text"
    case divAction = "div_action"
  }

  private enum CodingKeys: String, CodingKey {
    case type
  }

  var type: StepType {
    switch self {
    case .tap: .tap
    case .longTap: .longTap
    case .doubleTap: .doubleTap
    case .verifyText: .verifyText
    case .divAction: .divAction
    }
  }

  init(from decoder: Decoder) throws {
    let container = try decoder.container(keyedBy: CodingKeys.self)
    let type = try container.decode(StepType.self, forKey: .type)

    switch type {
    case .tap:
      self = try .tap(Tap(from: decoder))
    case .longTap:
      self = try .longTap(LongTap(from: decoder))
    case .doubleTap:
      self = try .doubleTap(DoubleTap(from: decoder))
    case .verifyText:
      self = try .verifyText(VerifyText(from: decoder))
    case .divAction:
      self = try .divAction(DivAction(from: decoder))
    }
  }
}
