// Copyright 2018 Yandex LLC. All rights reserved.
extension Signal {
  @inlinable
  public static func combineLatest<U>(_ signals: [Signal<U>]) -> Signal where T == [U] {
    let count = signals.count
    return Signal(addObserver: { arrayObserver in
      var latest = [U?](repeating: nil, times: try! UInt(value: count))
      func sendAll() {
        let compacted = latest.compactMap { $0 }
        if compacted.count == count {
          arrayObserver.action(compacted)
        }
      }
      let disposables = signals.enumerated().map { idx, signal in
        signal.addObserver { u in
          latest[idx] = u
          sendAll()
        }
      }
      return Disposable(disposables)
    })
  }

  @inlinable
  public static func combineLatest<U0, U1>(
    _ signal0: Signal<U0>,
    _ signal1: Signal<U1>
  ) -> Signal where T == (U0, U1) {
    return Signal(addObserver: { observer in
      var latest: (U0?, U1?) = (nil, nil)
      func sendAll() {
        if let u0 = latest.0, let u1 = latest.1 {
          observer.action((u0, u1))
        }
      }
      return Disposable([
        signal0.addObserver { u0 in
          latest.0 = u0
          sendAll()
        },
        signal1.addObserver { u1 in
          latest.1 = u1
          sendAll()
        },
      ])
    })
  }

  @inlinable
  public static func combineLatest<U0, U1, U2>(
    _ signal0: Signal<U0>,
    _ signal1: Signal<U1>,
    _ signal2: Signal<U2>
  ) -> Signal where T == (U0, U1, U2) {
    return Signal(addObserver: { observer in
      var latest: (U0?, U1?, U2?) = (nil, nil, nil)
      func sendAll() {
        if let u0 = latest.0, let u1 = latest.1, let u2 = latest.2 {
          observer.action((u0, u1, u2))
        }
      }
      return Disposable([
        signal0.addObserver { u0 in
          latest.0 = u0
          sendAll()
        },
        signal1.addObserver { u1 in
          latest.1 = u1
          sendAll()
        },
        signal2.addObserver { u2 in
          latest.2 = u2
          sendAll()
        },
      ])
    })
  }

  @inlinable
  public static func combineLatest<U0, U1, U2, U3>(
    _ signal0: Signal<U0>,
    _ signal1: Signal<U1>,
    _ signal2: Signal<U2>,
    _ signal3: Signal<U3>
  ) -> Signal where T == (U0, U1, U2, U3) {
    return Signal(addObserver: { observer in
      var latest: (U0?, U1?, U2?, U3?) = (nil, nil, nil, nil)
      func sendAll() {
        if let u0 = latest.0, let u1 = latest.1, let u2 = latest.2, let u3 = latest.3 {
          observer.action((u0, u1, u2, u3))
        }
      }
      return Disposable([
        signal0.addObserver { u0 in
          latest.0 = u0
          sendAll()
        },
        signal1.addObserver { u1 in
          latest.1 = u1
          sendAll()
        },
        signal2.addObserver { u2 in
          latest.2 = u2
          sendAll()
        },
        signal3.addObserver { u3 in
          latest.3 = u3
          sendAll()
        },
      ])
    })
  }

  @inlinable
  public static func combineLatest<U0, U1, U2, U3, U4>(
    _ signal0: Signal<U0>,
    _ signal1: Signal<U1>,
    _ signal2: Signal<U2>,
    _ signal3: Signal<U3>,
    _ signal4: Signal<U4>
  ) -> Signal where T == (U0, U1, U2, U3, U4) {
    return Signal(addObserver: { observer in
      var latest: (U0?, U1?, U2?, U3?, U4?) = (nil, nil, nil, nil, nil)
      func sendAll() {
        if let u0 = latest.0, let u1 = latest.1, let u2 = latest.2, let u3 = latest.3,
           let u4 = latest.4 {
          observer.action((u0, u1, u2, u3, u4))
        }
      }
      return Disposable([
        signal0.addObserver { u0 in
          latest.0 = u0
          sendAll()
        },
        signal1.addObserver { u1 in
          latest.1 = u1
          sendAll()
        },
        signal2.addObserver { u2 in
          latest.2 = u2
          sendAll()
        },
        signal3.addObserver { u3 in
          latest.3 = u3
          sendAll()
        },
        signal4.addObserver { u4 in
          latest.4 = u4
          sendAll()
        },
      ])
    })
  }

  @inlinable
  public static func combineLatest<U0, U1, U2, U3, U4, U5>(
    _ signal0: Signal<U0>,
    _ signal1: Signal<U1>,
    _ signal2: Signal<U2>,
    _ signal3: Signal<U3>,
    _ signal4: Signal<U4>,
    _ signal5: Signal<U5>
  ) -> Signal where T == (U0, U1, U2, U3, U4, U5) {
    return Signal(addObserver: { observer in
      var latest: (U0?, U1?, U2?, U3?, U4?, U5?) = (nil, nil, nil, nil, nil, nil)
      func sendAll() {
        if let u0 = latest.0,
           let u1 = latest.1,
           let u2 = latest.2,
           let u3 = latest.3,
           let u4 = latest.4,
           let u5 = latest.5 {
          observer.action((u0, u1, u2, u3, u4, u5))
        }
      }
      return Disposable([
        signal0.addObserver { u0 in
          latest.0 = u0
          sendAll()
        },
        signal1.addObserver { u1 in
          latest.1 = u1
          sendAll()
        },
        signal2.addObserver { u2 in
          latest.2 = u2
          sendAll()
        },
        signal3.addObserver { u3 in
          latest.3 = u3
          sendAll()
        },
        signal4.addObserver { u4 in
          latest.4 = u4
          sendAll()
        },
        signal5.addObserver { u5 in
          latest.5 = u5
          sendAll()
        },
      ])
    })
  }
}
