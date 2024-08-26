import 'dart:convert';

import 'package:divkit/divkit.dart';
import 'package:example/src/pages/testing/data.dart';
import 'package:example/src/pages/testing/scenario.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

Future<List<Item>> process(Box box) async {
  // Register the background isolate with the root isolate.
  BackgroundIsolateBinaryMessenger.ensureInitialized(box.token);

  final it = <Item>[];
  for (int i = 0; i < box.data.length; ++i) {
    try {
      final name = (box.metas[i]['title'] as String?) ?? '<no name>';
      final tags = (box.metas[i]['tags'] as List?)?.cast<String>() ?? [];

      final source = box.data[i];
      final json = jsonDecode(source);
      final data = DefaultDivKitData.fromScheme(
        card: json.containsKey('card') ? json['card'] : json,
        templates: json['templates'],
      );
      await data.build();
      await data.preload();

      it.add(Item(
        name,
        data,
        tags.toSet(),
      ));
    } catch (_) {}
  }
  return it..sort((a, b) => a.title.compareTo(b.title));
}

Future<List<Item>> loadList() async {
  final index = json.decode(
    await rootBundle
        .loadString('assets/test_data/regression_test_data/index.json'),
  );

  List<String> strings = [];
  List<Map> metas = [];
  for (var test in index['tests']) {
    final asset = 'assets/test_data/regression_test_data/${test['file']}';
    strings.add(await rootBundle.loadString(asset));
    metas.add(test);
  }

  return compute<Box, List<Item>>(process, Box(strings, metas));
}

class TestingPage extends StatefulWidget {
  const TestingPage({super.key});

  @override
  State<StatefulWidget> createState() => _TestingPage();
}

class _TestingPage extends State<TestingPage> {
  final MenuController menuController = MenuController();
  Set<String> selectedTags = {};

  void toggleTag(String tag) {
    setState(() {
      if (selectedTags.contains(tag)) {
        selectedTags.remove(tag);
      } else {
        selectedTags.add(tag);
      }
    });
  }

  @override
  Widget build(BuildContext context) => FutureBuilder<List<Item>>(
        future: loadList(),
        builder: (_, snapshot) {
          if (snapshot.hasError) {
            return Center(
              child: Text(snapshot.error.toString()),
            );
          }

          if (snapshot.hasData) {
            final data = snapshot.data
                ?.where(
                  (element) =>
                      selectedTags.isEmpty ||
                      element.tags.intersection(selectedTags).isNotEmpty,
                )
                .toList();

            if (data != null) {
              final tags = snapshot.data! //
                  .map((e) => e.tags)
                  .expand((e) => e)
                  .toSet();

              return Scaffold(
                appBar: AppBar(
                  title: const Text("Testing"),
                  actions: [
                    MenuAnchor(
                      controller: menuController,
                      menuChildren: [
                        MenuItemButton(
                          onPressed: selectedTags.isNotEmpty
                              ? () => setState(() => selectedTags = {})
                              : null,
                          child: const Text('Reset'),
                        ),
                        for (final tag in tags) //
                          CheckboxMenuButton(
                            value: selectedTags.contains(tag),
                            onChanged: (value) => toggleTag(tag),
                            child: Text(tag),
                          ),
                      ],
                      child: IconButton(
                        onPressed: () => menuController.open(),
                        icon: const Icon(Icons.tag),
                      ),
                    ),
                  ],
                ),
                body: ListView.builder(
                  padding: const EdgeInsets.all(16),
                  itemCount: data.length,
                  itemBuilder: (context, index) {
                    final item = data[index];
                    return FilledButton.tonal(
                      onPressed: () => Navigator.of(context).push(
                        MaterialPageRoute(
                          builder: (context) => ScenarioPage(
                            scenarios: data,
                            index: index,
                          ),
                        ),
                      ),
                      child: Row(
                        children: [
                          Expanded(
                            child: Text(
                              item.title,
                              overflow: TextOverflow.ellipsis,
                            ),
                          ),
                          const SizedBox(width: 8),
                          const Icon(Icons.chevron_right),
                        ],
                      ),
                    );
                  },
                ),
              );
            }
          }
          return Scaffold(
            appBar: AppBar(
              title: const Text('Loading..'),
              centerTitle: true,
            ),
            body: const Center(
              child: CircularProgressIndicator(),
            ),
          );
        },
      );

  @override
  void dispose() {
    super.dispose();
  }
}
