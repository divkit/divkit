import 'dart:io';

import 'package:divkit/divkit.dart';
import 'package:example/src/configuration/playground_custom_handler.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../state.dart';

class ShowPage extends ConsumerStatefulWidget {
  final Map<String, dynamic> data;

  const ShowPage(this.data, {super.key});

  @override
  ConsumerState<ShowPage> createState() => _ShowPageState();
}

class _ShowPageState extends ConsumerState<ShowPage> {
  Future<DivKitData> load() async {
    final json = widget.data;
    final data = DefaultDivKitData.fromScheme(
      card: json.containsKey('card') ? json['card'] : json,
      templates: json['templates'],
    );
    await data.build();
    // Expressions only work on mobile platforms!
    if (Platform.isAndroid || Platform.isIOS) {
      await data.preload();
    }
    return data;
  }

  @override
  Widget build(BuildContext context) {
    final isNightModeEnabled = ref.watch(nightModeProvider);
    final isRtlEnabled = ref.watch(isRTLProvider);
    final reloadN = ref.watch(reloadNProvider);
    final mq = MediaQuery.of(context);

    return Scaffold(
      appBar: AppBar(
        title: FittedBox(
          child: Row(
            children: [
              Text("${mq.size.width.round()}x${mq.size.height.round() - 64}"),
              const SizedBox(width: 32),
              ElevatedButton(
                child: const Text('reload'),
                onPressed: () {
                  ref.read(reloadNProvider.notifier).state = reloadN + 1;
                },
              ),
              const SizedBox(width: 8),
              ElevatedButton(
                child: Text(isNightModeEnabled ? 'dark' : 'light'),
                onPressed: () {
                  ref.read(nightModeProvider.notifier).state =
                      !isNightModeEnabled;
                },
              ),
              const SizedBox(width: 8),
              ElevatedButton(
                child: Text(isRtlEnabled ? 'RTL' : 'LTR'),
                onPressed: () {
                  ref.read(isRTLProvider.notifier).state = !isRtlEnabled;
                },
              ),
            ],
          ),
        ),
      ),
      body: FutureBuilder<DivKitData>(
        future: load(),
        builder: (context, snapshot) {
          if (snapshot.hasData) {
            return SingleChildScrollView(
              child: DivKitView(
                key: ObjectKey(reloadN),
                data: snapshot.requireData,
                customHandler: PlaygroundAppCustomHandler(),
              ),
            );
          }
          return const Center(child: CircularProgressIndicator());
        },
      ),
    );
  }
}
