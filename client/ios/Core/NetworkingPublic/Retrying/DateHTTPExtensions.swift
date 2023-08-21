// Copyright 2017 Yandex LLC. All rights reserved.

import Foundation

extension Date {
  public static func fromHTTPDate(_ dateString: String) -> Date? {
    for formatter in webDateFormatters {
      if let date = formatter.date(from: dateString) {
        return date
      }
    }
    return nil
  }
}

private let webDateFormatters = [
  makeWebDateFormatter(format: "EEE, dd MMM yyyy HH:mm:ss z"), // RFC 822, updated by RFC 1123
  makeWebDateFormatter(format: "EEEE, dd-MMM-yy HH:mm:ss z"), // RFC 850, obsoleted by RFC 1036
  makeWebDateFormatter(format: "EEE MMM d HH:mm:ss yyyy"), // ANSI C's asctime() format
]

private func makeWebDateFormatter(format: String) -> DateFormatter {
  let dateFormatter = DateFormatter()
  dateFormatter.locale = Locale(identifier: "en_US_POSIX")
  dateFormatter.timeZone = TimeZone(secondsFromGMT: 0)
  dateFormatter.dateFormat = format
  return dateFormatter
}
