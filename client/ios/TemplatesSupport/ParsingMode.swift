/// Supported modes for dictionary interpretation.
public enum ParsingMode {
  /// Do not treat object body as links container.
  /// Should be passed when parsing templates.
  case template

  /// Expect data for links resolution in object body.
  /// Should be passed when parsing main respose body.
  case data
}
