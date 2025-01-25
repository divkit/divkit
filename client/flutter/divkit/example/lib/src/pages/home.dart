import 'dart:convert';

import 'package:divkit/divkit.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../configuration/playground_custom_handler.dart';
import '../configuration/playground_root_action_handler.dart';
import '../state.dart';

class HomePage extends StatelessWidget {
  const HomePage({super.key});

  Future<DivKitData> load(String name) async {
    final jsonData = await jsonDecode(
      await rootBundle.loadString(
        'assets/application/$name',
      ),
    );
    return DefaultDivKitData.fromJson(jsonData).build();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: SafeArea(
        child: OrientationBuilder(builder: (context, orientation) {
          return FutureBuilder<DivKitData>(
            key: ObjectKey(orientation),
            builder: (_, snapshot) {
              if (snapshot.hasData) {
                return DivKitView(
                  data: snapshot.requireData,
                  customHandler: PlaygroundAppCustomHandler(),
                  actionHandler: PlaygroundAppRootActionHandler(
                    navigator: navigatorKey,
                  ),
                );
              }
              return const Center(
                child: CircularProgressIndicator(),
              );
            },
            future: orientation == Orientation.portrait
                ? load('menu.json')
                : load('menu-land.json'),
          );
        }),
      ),
    );
  }
}
