import 'package:divkit/divkit.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  group('StateManager implementation tests', () {
    test(
      'registerState',
      () async {
        final manager = DefaultDivStateManager();
        manager.registerState('root', '0');
        expect(manager.states, {'root': '0'});
        await manager.dispose();
      },
      timeout: const Timeout(Duration(seconds: 1)),
    );
    test(
      'unregisterState',
      () async {
        final manager = DefaultDivStateManager();
        manager.registerState('root', '0');
        manager.unregisterState('root');
        expect(manager.states, {});
        await manager.dispose();
      },
      timeout: const Timeout(Duration(seconds: 1)),
    );
    test(
      'updateState',
      () async {
        final manager = DefaultDivStateManager();
        manager.registerState('root', '0');
        manager.updateState('root', '1');
        expect(manager.states, {'root': '1'});
        await manager.dispose();
      },
      timeout: const Timeout(Duration(seconds: 1)),
    );
    test(
      'watch',
      () async {
        final manager = DefaultDivStateManager();
        manager.registerState('root', '0');
        manager.watch((context) => expect(context, {'root': '0'}));
        manager.updateState('root', '1');
        manager.watch((context) => expect(context, {'root': '1'}));
        await manager.dispose();
      },
      timeout: const Timeout(Duration(seconds: 1)),
    );
    test(
      'watch',
      () async {
        final manager = DefaultDivStateManager();
        manager.registerState('root', '0');
        await manager.dispose();
        expect(manager.states, {'root': '0'});
      },
      timeout: const Timeout(Duration(seconds: 1)),
    );
  });
}
