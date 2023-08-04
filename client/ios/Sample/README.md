# DivKit iOS. Quick start.
## Build an MVP.
Code-wise, connecting ```DivKit``` consists of three parts: initializing ```DivKitComponents```, parsing the ```JSON``` file with the layout, and adding DivView to your view hierarchy.
#### Initialize the DivKitComponents.
```DivKitComponents``` is basically just a container for client dependencies. We'll talk about them below.
#### Initialization the DivView.
To create a DivView, you need to provide a previously created DivKitComponents instance to the constructor. Subsequently, you should populate the DivView with the data to be displayed at the appropriate time.

Initialization Example:
```Swift
let divView = DivView(divKitComponents: divKitComponents)
divView.setSource(
    DivBlockProvider.Source(
        kind: .json(json),
        cardId: cardId
    ),
    debugParams: debugParams,
    shouldResetPreviousCardData: true
)
```

