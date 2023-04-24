import CoreMedia

extension CMTime {
  public init(value: Int) {
    self.init(value: CMTimeValue(value), timescale: 1)
  }
}
