import 'dart:async';

import 'package:divkit/src/utils/clockwork.dart';
import 'package:fake_async/fake_async.dart';
import 'package:flutter_test/flutter_test.dart';

extension on num {
  Duration get ms => Duration(milliseconds: round());
}

void main() {
  group('Clockwork public api test', () {
    test("duration should be set to the given duration", () {
      for (final duration in [1000.ms, 2000.ms, 3000.ms, 4000.ms]) {
        final clockwork = Clockwork(
          duration: duration,
        );
        expect(clockwork.duration, duration);
      }
    });

    test("duration should be set null if the given duration is null", () {
      final clockwork = Clockwork(
        duration: null,
      );
      expect(clockwork.duration, null);
    });

    test("duration should be set null if the given duration less or equal zero",
        () {
      for (final duration in [0.ms, -1000.ms, -2000.ms, -3000.ms, -4000.ms]) {
        final clockwork = Clockwork(
          duration: duration,
        );
        expect(clockwork.duration, null);
      }
    });

    test("interval should be set to the given interval", () {
      for (final interval in [1000.ms, 2000.ms, 3000.ms, 4000.ms]) {
        final clockwork = Clockwork(
          interval: interval,
        );
        expect(clockwork.interval, interval);
      }
    });

    test("interval should be set null if the given interval is null", () {
      final clockwork = Clockwork(
        duration: null,
      );
      expect(clockwork.duration, null);
    });

    test("interval should be set null if the given interval less or equal zero",
        () {
      for (final interval in [0.ms, -1000.ms, -2000.ms, -3000.ms, -4000.ms]) {
        final clockwork = Clockwork(
          interval: interval,
        );
        expect(clockwork.interval, null);
      }
    });
  });

  group('Clockwork implementation test', () {
    test("when clockwork inited state is stopped", () {
      fakeAsync((async) {
        final clockwork = Clockwork(
          duration: 1000.ms,
        );
        expect(clockwork.state, ClockworkState.stopped);
        expect(clockwork.isActive, isFalse);
      });
    });

    test("shouldn't start automatically", () {
      fakeAsync((async) {
        final clockwork = Clockwork(
          duration: 1000.ms,
        );
        expect(clockwork.state, ClockworkState.stopped);
        expect(clockwork.isActive, isFalse);
        async.elapse(10000.ms);
        expect(clockwork.state, ClockworkState.stopped);
        expect(clockwork.isActive, isFalse);
      });
    });

    test("when clockwork start state is started", () {
      fakeAsync((async) {
        final clockwork = Clockwork(
          duration: 1000.ms,
        );
        expect(clockwork.state, ClockworkState.stopped);
        expect(clockwork.isActive, isFalse);
        clockwork.start();
        expect(clockwork.state, ClockworkState.started);
        expect(clockwork.isActive, isTrue);
      });
    });

    test("clockwork start only once", () {
      fakeAsync((async) {
        final clockwork = Clockwork(
          duration: 2000.ms,
        );
        clockwork.start();
        async.elapse(500.ms);
        expect(clockwork.state, ClockworkState.started);
        expect(clockwork.isActive, isTrue);
        final elapsed = clockwork.elapsed;
        clockwork.start();
        async.elapse(500.ms);
        expect(clockwork.state, ClockworkState.started);
        expect(clockwork.isActive, isTrue);
        expect(clockwork.elapsed, greaterThan(elapsed));
      });
    });

    test("when clockwork started state is stopped on stop", () {
      fakeAsync((async) {
        final clockwork = Clockwork(
          duration: 1000.ms,
        );

        clockwork.start();
        expect(clockwork.state, ClockworkState.started);
        async.elapse(500.ms);
        expect(clockwork.state, ClockworkState.started);
        clockwork.stop();
        expect(clockwork.state, ClockworkState.stopped);
        async.elapse(500.ms);
        expect(clockwork.state, ClockworkState.stopped);
      });
    });

    test("when clockwork started state is paused on pause", () {
      fakeAsync((async) {
        final clockwork = Clockwork(
          duration: 2000.ms,
        );

        clockwork.start();
        expect(clockwork.state, ClockworkState.started);
        async.elapse(500.ms);
        expect(clockwork.state, ClockworkState.started);
        clockwork.pause();
        expect(clockwork.state, ClockworkState.paused);
        async.elapse(500.ms);
        expect(clockwork.state, ClockworkState.paused);
      });
    });

    test("when clockwork started state not changed on resume", () {
      fakeAsync((async) {
        final clockwork = Clockwork(
          duration: 2000.ms,
        );
        clockwork.start();
        clockwork.resume();
        expect(clockwork.state, ClockworkState.started);
        async.elapse(500.ms);
        expect(clockwork.state, ClockworkState.started);
      });
    });

    test("when clockwork started state is stopped on cancel", () {
      fakeAsync((async) {
        final clockwork = Clockwork(
          duration: 2000.ms,
        );
        clockwork.start();
        clockwork.cancel();
        expect(clockwork.state, ClockworkState.stopped);
        async.elapse(500.ms);
        expect(clockwork.state, ClockworkState.stopped);
      });
    });

    test("when clockwork started state is started on reset", () {
      fakeAsync((async) {
        final clockwork = Clockwork(
          duration: 2000.ms,
        );
        clockwork.start();
        clockwork.reset();
        expect(clockwork.state, ClockworkState.started);
        async.elapse(500.ms);
        expect(clockwork.state, ClockworkState.started);
      });
    });

    test("when clockwork started variable set to zero on reset", () {
      fakeAsync((async) {
        int count = 10;
        final clockwork = Clockwork(
          duration: 2000.ms,
          interval: 1000.ms,
          onStart: () {
            count = 0;
          },
          onReset: () {
            count = 0;
          },
          onTick: (_) {
            count += 1;
          },
        );
        expect(count, 10);
        clockwork.start();
        expect(count, 0);
        async.elapse(1000.ms);
        expect(count, 1);
        clockwork.reset();
        expect(count, 0);
        async.elapse(1000.ms);
        expect(count, 1);
        async.elapse(1000.ms);
        expect(count, 2);
      });
    });

    test("should call `onEnd` after the given duration", () {
      fakeAsync((async) {
        var called = false;
        final clockwork = Clockwork(
          duration: 1000.ms,
          onEnd: (_) {
            called = true;
          },
        );

        clockwork.start();
        expect(clockwork.state, ClockworkState.started);
        async.elapse(500.ms);
        expect(called, isFalse);
        expect(clockwork.state, ClockworkState.started);
        async.elapse(500.ms);
        expect(called, isTrue);
        expect(clockwork.state, ClockworkState.stopped);
      });
    });

    test("should do nothing after it's expired", () {
      fakeAsync((async) {
        var called = false;
        final clockwork = Clockwork(
          duration: 1000.ms,
          onEnd: (_) {
            called = true;
          },
        );

        clockwork.start();
        async.elapse(2000.ms);
        expect(called, isTrue);
        expect(clockwork.state, ClockworkState.stopped);
        called = false;
        async.elapse(2000.ms);
        expect(called, isFalse);
        expect(clockwork.state, ClockworkState.stopped);
      });
    });

    test("when clockwork started call `onEnd` actions on stop", () {
      fakeAsync((async) {
        var called = false;
        final clockwork = Clockwork(
          duration: 1000.ms,
          onEnd: (_) {
            called = true;
          },
        );

        clockwork.start();
        expect(clockwork.state, ClockworkState.started);
        clockwork.stop();
        expect(clockwork.state, ClockworkState.stopped);
        expect(called, isTrue);
      });
    });

    test("when clockwork paused call `onEnd` and state is stop on stop", () {
      fakeAsync((async) {
        var called = false;
        final clockwork = Clockwork(
          duration: 1000.ms,
          onEnd: (_) {
            called = true;
          },
        );

        clockwork.start();
        expect(clockwork.state, ClockworkState.started);
        clockwork.pause();
        expect(clockwork.state, ClockworkState.paused);
        clockwork.stop();
        expect(clockwork.state, ClockworkState.stopped);
        expect(called, isTrue);
      });
    });

    test("when clockwork paused clockwork not start", () {
      fakeAsync((async) {
        final clockwork = Clockwork(
          duration: 1000.ms,
        );

        clockwork.start();
        expect(clockwork.state, ClockworkState.started);
        async.elapse(500.ms);
        expect(clockwork.state, ClockworkState.started);
        clockwork.pause();
        expect(clockwork.state, ClockworkState.paused);
        async.elapse(500.ms);
        expect(clockwork.state, ClockworkState.paused);
        clockwork.start();
        expect(clockwork.state, ClockworkState.paused);
      });
    });

    test("when clockwork paused state is stop on stop", () {
      fakeAsync((async) {
        final clockwork = Clockwork(
          duration: 2000.ms,
        );
        clockwork.start();
        clockwork.pause();
        clockwork.stop();
        expect(clockwork.state, ClockworkState.stopped);
        async.elapse(500.ms);
        expect(clockwork.state, ClockworkState.stopped);
      });
    });

    test("when clockwork paused state is started on resume", () {
      fakeAsync((async) {
        final clockwork = Clockwork(
          duration: 2000.ms,
        );
        clockwork.start();
        clockwork.pause();
        clockwork.resume();
        expect(clockwork.state, ClockworkState.started);
        async.elapse(500.ms);
        expect(clockwork.state, ClockworkState.started);
      });
    });

    test("when clockwork paused state is stopped on cancel", () {
      fakeAsync((async) {
        final clockwork = Clockwork(
          duration: 2000.ms,
        );
        clockwork.start();
        clockwork.pause();
        clockwork.cancel();
        expect(clockwork.state, ClockworkState.stopped);
        async.elapse(500.ms);
        expect(clockwork.state, ClockworkState.stopped);
      });
    });

    test("when clockwork paused state is started on reset", () {
      fakeAsync((async) {
        final clockwork = Clockwork(
          duration: 2000.ms,
        );
        clockwork.start();
        clockwork.pause();
        clockwork.reset();
        expect(clockwork.state, ClockworkState.started);
        async.elapse(500.ms);
        expect(clockwork.state, ClockworkState.started);
      });
    });

    test("When clockwork stopped state not changed on paused", () {
      fakeAsync((async) {
        final clockwork = Clockwork(
          duration: 1000.ms,
        );

        clockwork.start();
        expect(clockwork.state, ClockworkState.started);
        async.elapse(500.ms);
        expect(clockwork.state, ClockworkState.started);
        clockwork.stop();
        expect(clockwork.state, ClockworkState.stopped);
        async.elapse(500.ms);
        expect(clockwork.state, ClockworkState.stopped);
        clockwork.pause();
        expect(clockwork.state, ClockworkState.stopped);
      });
    });

    test("clockwork paused only once", () {
      fakeAsync((async) {
        final clockwork = Clockwork(
          duration: 2000.ms,
        );
        clockwork.start();
        expect(clockwork.state, ClockworkState.started);
        async.elapse(500.ms);
        expect(clockwork.state, ClockworkState.started);
        clockwork.pause();
        expect(clockwork.state, ClockworkState.paused);
        final elapsed = clockwork.elapsed;
        async.elapse(500.ms);
        expect(clockwork.state, ClockworkState.paused);
        expect(clockwork.elapsed, equals(elapsed));
      });
    });

    test("when clockwork stopped state not changed on paused", () {
      fakeAsync((async) {
        final clockwork = Clockwork(
          duration: 2000.ms,
        );
        clockwork.pause();
        expect(clockwork.state, ClockworkState.stopped);
        async.elapse(500.ms);
        expect(clockwork.state, ClockworkState.stopped);
      });
    });

    test("when clockwork stopped state not changed on resume", () {
      fakeAsync((async) {
        final clockwork = Clockwork(
          duration: 2000.ms,
        );
        clockwork.resume();
        expect(clockwork.state, ClockworkState.stopped);
        async.elapse(500.ms);
        expect(clockwork.state, ClockworkState.stopped);
      });
    });

    test("when clockwork stopped state not changed on cancel", () {
      fakeAsync((async) {
        final clockwork = Clockwork(
          duration: 2000.ms,
        );
        clockwork.cancel();
        expect(clockwork.state, ClockworkState.stopped);
        async.elapse(500.ms);
        expect(clockwork.state, ClockworkState.stopped);
      });
    });

    test("when clockwork stopped state is started on reset", () {
      fakeAsync((async) {
        final clockwork = Clockwork(
          duration: 2000.ms,
        );
        clockwork.reset();
        expect(clockwork.state, ClockworkState.started);
        async.elapse(500.ms);
        expect(clockwork.state, ClockworkState.started);
      });
    });

    test("when clockwork started variable set to zero on reset", () {
      fakeAsync((async) {
        int count = 10;
        final clockwork = Clockwork(
          duration: 2000.ms,
          interval: 1000.ms,
          onStart: () {
            count = 0;
          },
          onReset: () {
            count = 0;
          },
          onTick: (_) {
            count += 1;
          },
        );

        clockwork.stop();
        async.elapse(1000.ms);
        expect(count, 10);
        clockwork.reset();
        expect(count, 0);
        async.elapse(1000.ms);
        expect(count, 1);
        async.elapse(1000.ms);
        expect(count, 2);
      });
    });

    test("a paused clockwork should resume it's clockwork", () {
      fakeAsync((async) {
        final clockwork = Clockwork(
          duration: 1000.ms,
        );

        clockwork.start();
        async.elapse(500.ms);
        expect(clockwork.state, ClockworkState.started);
        clockwork.pause();
        expect(clockwork.state, ClockworkState.paused);
        async.elapse(1000.ms);
        expect(clockwork.state, ClockworkState.paused);
        clockwork.resume();
        expect(clockwork.state, ClockworkState.started);
        async.elapse(1000.ms);
        expect(clockwork.state, ClockworkState.stopped);
      });
    });

    test("an active clockwork should revert it's time to zero", () {
      fakeAsync((async) {
        final clockwork = Clockwork(
          duration: 1000.ms,
        );

        clockwork.start();
        async.elapse(500.ms);
        expect(clockwork.isActive, isTrue);
        async.elapse(1000.ms);
        expect(clockwork.state, ClockworkState.stopped);
        clockwork.reset();
        expect(clockwork.isActive, isTrue);
        expect(
          clockwork.elapsed,
          lessThan(const Duration(milliseconds: 50)),
        );
      });
    });

    test("when timer ended do not run tick action", () {
      fakeAsync((async) {
        var expired = false;
        var count = 0;
        final clockwork = Clockwork(
          duration: 3000.ms,
          interval: 2000.ms,
          onTick: (_) {
            count = 1;
          },
          onEnd: (_) {
            expired = true;
          },
        );

        clockwork.start();
        expect(clockwork.isActive, isTrue);
        expect(expired, isFalse);
        expect(count, 0);
        async.elapse(2000.ms);
        expect(clockwork.isActive, isTrue);
        expect(expired, isFalse);
        expect(count, 1);
        async.elapse(1000.ms);
        expect(clockwork.isActive, isFalse);
        expect(expired, isTrue);
        expect(count, 1);
        async.elapse(1000.ms);
        expect(clockwork.isActive, isFalse);
        expect(expired, isTrue);
        expect(count, 1);
      });
    });

    test("timer with small values", () {
      fakeAsync((async) {
        var expired = false;
        var count = 0;
        final clockwork = Clockwork(
          duration: 1.ms,
          interval: 1.ms,
          onTick: (_) {
            count = 1;
          },
          onEnd: (_) {
            expired = true;
          },
        );

        clockwork.start();
        expect(clockwork.isActive, isTrue);
        async.elapse(1.ms);
        expect(clockwork.isActive, isFalse);
        expect(expired, isTrue);
        expect(count, 1);
      });
    });

    test(
        "should call it's onTick as many times as it's interval and duration requires",
        () {
      fakeAsync((async) {
        var expired = false;
        int count = 0;
        final clockwork = Clockwork(
          duration: 10000.ms,
          onEnd: (_) {
            expired = true;
          },
          interval: 1000.ms,
          onTick: (_) {
            count += 1;
          },
        );

        clockwork.start();
        async.elapse(9000.ms);
        expect(count, 9);
        expect(clockwork.tick, 9);
        expect(expired, isFalse);
        expect(clockwork.state, ClockworkState.started);

        async.elapse(1000.ms);
        expect(count, 10);
        expect(clockwork.tick, 10);
        expect(expired, isTrue);
        expect(clockwork.state, ClockworkState.stopped);

        async.elapse(1000.ms);
        expect(count, 10);
        expect(clockwork.tick, 10);
        expect(expired, isTrue);
        expect(clockwork.state, ClockworkState.stopped);
      });
    });

    test('complex [onEnd]', () {
      fakeAsync((async) {
        var expired = false;
        final clockwork = Clockwork(
          duration: 1000.ms,
          onEnd: (_) => expired = true,
        );

        clockwork.start();

        async.elapse(500.ms);
        expect(expired, false);

        clockwork.pause();

        async.elapse(500.ms);
        expect(expired, false);
        expect(clockwork.elapsed, 500.ms);

        clockwork.resume();

        async.elapse(500.ms);
        expect(expired, true);

        expired = false;

        clockwork.reset();

        async.elapse(1000.ms);
        expect(expired, true);

        expired = false;
        clockwork.cancel();

        expect(expired, false);
        expect(clockwork.duration, 1000.ms);
        expect(clockwork.elapsed, Duration.zero);
        expect(clockwork.tick, 0);
        expect(clockwork.state, ClockworkState.stopped);
      });
    });

    test('complex [onTick]', () {
      fakeAsync((async) {
        var expired = false;
        var count = 0;
        final clockwork = Clockwork(
          duration: 3000.ms,
          interval: 1000.ms,
          onTick: (_) => count++,
          onEnd: (_) => expired = true,
        );

        clockwork.start();

        async.elapse(1000.ms);
        expect(expired, false);
        expect(count, 1);

        clockwork.pause();

        async.elapse(1000.ms);
        expect(expired, false);
        expect(count, 1);

        clockwork.resume();

        async.elapse(1000.ms);
        expect(expired, false);
        expect(count, 2);

        async.elapse(1000.ms);
        expect(expired, true);
        expect(count, 3);
      });
    });
  });

  group('Zones move test', () {
    test('Call in zone `main` when created and stated in `main`', () {
      fakeAsync((async) {
        final mainZone = Zone.current.hashCode;

        final clock = Clockwork(
          duration: 5000.ms,
          interval: 1000.ms,
          onStart: () => expect(Zone.current.hashCode, mainZone),
          onReset: () => expect(Zone.current.hashCode, mainZone),
          onPause: (elapsed) => expect(Zone.current.hashCode, mainZone),
          onTick: (elapsed) => expect(Zone.current.hashCode, mainZone),
          onEnd: (elapsed) => expect(Zone.current.hashCode, mainZone),
        );

        clock.start();

        async.elapse(5000.ms);
      });
    });
    test('Call in zone `main` when created in `main` but stated in `start`',
        () {
      fakeAsync((async) {
        final mainZone = Zone.current.hashCode;

        final clock = Clockwork(
          duration: 5000.ms,
          interval: 1000.ms,
          onStart: () => expect(Zone.current.hashCode, mainZone),
          onReset: () => expect(Zone.current.hashCode, mainZone),
          onPause: (elapsed) => expect(Zone.current.hashCode, mainZone),
          onTick: (elapsed) => expect(Zone.current.hashCode, mainZone),
          onEnd: (elapsed) => expect(Zone.current.hashCode, mainZone),
        );

        runZoned(() {
          final startZone = Zone.current.hashCode;
          expect(startZone, isNot(mainZone));
          clock.start();
          return Future.value();
        });

        async.elapse(5000.ms);
      });
    });
    test('Call in zone `start` when created in `start` but stated in `main`',
        () {
      fakeAsync((async) {
        final mainZone = Zone.current.hashCode;
        late Clockwork clock;
        runZoned(() {
          final startZone = Zone.current.hashCode;
          clock = Clockwork(
            duration: 5000.ms,
            interval: 1000.ms,
            onStart: () => expect(Zone.current.hashCode, startZone),
            onReset: () => expect(Zone.current.hashCode, startZone),
            onPause: (elapsed) => expect(Zone.current.hashCode, startZone),
            onTick: (elapsed) => expect(Zone.current.hashCode, startZone),
            onEnd: (elapsed) => expect(Zone.current.hashCode, startZone),
          );
          return Future.value();
        });

        expect(Zone.current.hashCode, mainZone);
        clock.start();

        async.elapse(5000.ms);
      });
    });
  });
}
