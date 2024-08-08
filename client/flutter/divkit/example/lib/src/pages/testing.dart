import 'dart:convert';

import 'package:divkit/divkit.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class Box {
  final RootIsolateToken token;
  final List<String> data;
  final List<String> names;

  Box(this.data, this.names) : token = ServicesBinding.rootIsolateToken!;
}

class Item {
  final String name;
  final DivKitData src;

  const Item(this.name, this.src);
}

Future<List<Item>> process(Box box) async {
  // Register the background isolate with the root isolate.
  BackgroundIsolateBinaryMessenger.ensureInitialized(box.token);

  final it = <Item>[];
  for (int i = 0; i < box.data.length; ++i) {
    try {
      final name = box.names[i];
      final str = box.data[i];
      final json = jsonDecode(str);
      final data = DefaultDivKitData.fromScheme(
        card: json.containsKey('card') ? json['card'] : json,
        templates: json['templates'],
      );
      await data.build();
      await data.preload();
      it.add(Item(name, data));
    } catch (_) {}
  }
  return it;
}

Future<List<Item>> loadList() async {
  final Map<String, dynamic> manifestMap = json.decode(
    await rootBundle.loadString('AssetManifest.json'),
  );
  manifestMap.removeWhere(
    (key, value) =>
        !key.startsWith('assets/test_data/regression_test_data') ||
        !key.endsWith('json'),
  );

  List<String> strings = [];
  List<String> names = [];
  for (var key in manifestMap.keys) {
    strings.add(await rootBundle.loadString(key.replaceAll('../', '')));
    names.add(key
        .replaceAll('assets/test_data/regression_test_data/', '')
        .replaceAll('.json', '')
        .replaceAll('/', ' '));
  }

  return compute<Box, List<Item>>(process, Box(strings, names));
}

class TestingPage extends StatefulWidget {
  const TestingPage({super.key});

  @override
  State<StatefulWidget> createState() => _TestingPage();
}

class _TestingPage extends State<TestingPage> {
  int index = 0;

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
            final data = snapshot.data;
            if (data != null) {
              try {
                return Scaffold(
                  appBar: AppBar(
                    title: Text(data[index].name),
                    centerTitle: true,
                  ),
                  body: Column(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      Expanded(
                        child: SingleChildScrollView(
                          child: DivKitView(
                            showUnsupportedDivs: true,
                            data: data[index].src,
                          ),
                        ),
                      ),
                      const Divider(),
                      Padding(
                        padding: const EdgeInsets.symmetric(
                          vertical: 8.0,
                          horizontal: 16,
                        ),
                        child: Row(
                          children: [
                            if (index - 1 >= 0)
                              ElevatedButton(
                                onPressed: () {
                                  setState(() {
                                    index = index - 1;
                                  });
                                },
                                child: const Text('Back'),
                              ),
                            const Spacer(),
                            if (index + 1 < data.length)
                              ElevatedButton(
                                onPressed: () {
                                  setState(() {
                                    index = index + 1;
                                  });
                                },
                                child: const Text('Next'),
                              ),
                          ],
                        ),
                      ),
                    ],
                  ),
                );
              } catch (e, st) {
                return Scaffold(
                  appBar: AppBar(
                    title: Text(data[index].name),
                    centerTitle: true,
                  ),
                  body: Column(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      Expanded(child: Center(child: Text("$e\n$st"))),
                      const Divider(),
                      Padding(
                        padding: const EdgeInsets.symmetric(
                          vertical: 8.0,
                          horizontal: 16,
                        ),
                        child: Row(
                          children: [
                            if (index - 1 >= 0)
                              ElevatedButton(
                                onPressed: () {
                                  setState(() {
                                    index = index - 1;
                                  });
                                },
                                child: const Text('Back'),
                              ),
                            const Spacer(),
                            if (index + 1 < data.length)
                              ElevatedButton(
                                onPressed: () {
                                  setState(() {
                                    index = index + 1;
                                  });
                                },
                                child: const Text('Next'),
                              ),
                          ],
                        ),
                      ),
                    ],
                  ),
                );
              }
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
