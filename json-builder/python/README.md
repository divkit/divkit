PyDIVKit examples
=================

This library is designed to work with [DivKit](http://divkit.tech/) with python.

Features:
* Declarative and imperative DivKit blocks definition
* Native Type-hints support
* Complete object-oriented API
* IDE type checks and suggestions

Object construction
-------------------

The main idea is to provide a tool for creating blocks using Python objects.

```python
import json
import pydivkit as dk

container = dk.DivContainer(
    items=[
        dk.DivGallery(
            items=[
                dk.DivText(text="Hello from pydivkit")
            ]
        )
    ]
)

print(json.dumps(container.dict(), indent=1))
# {
#  "type": "container",
#  "items": [
#   {
#    "type": "gallery",
#    "items": [
#     {
#      "type": "text",
#      "text": "Hello from pydivkit"
#     }
#    ]
#   }
#  ]
# }
```

Slider example
--------------

Following code is a rewritten slider example using pydivkit.

```python
import pydivkit as dk


slider = dk.DivData(
    log_id="sample_card",
    states=[
        dk.DivDataState(
            state_id=0,
            div=dk.DivSlider(
                width=dk.DivMatchParentSize(),
                max_value=10,
                min_value=1,
                thumb_style=dk.DivShapeDrawable(
                    color="#00b300",
                    stroke=dk.DivStroke(
                        color="#ffffff",
                        width=3,
                    ),
                    shape=dk.DivRoundedRectangleShape(
                        item_width=dk.DivFixedSize(value=32),
                        item_height=dk.DivFixedSize(value=32),
                        corner_radius=dk.DivFixedSize(value=100)
                    ),
                ),
                track_active_style=dk.DivShapeDrawable(
                    color="#00b300",
                    shape=dk.DivRoundedRectangleShape(
                        item_height=dk.DivFixedSize(value=6)
                    )
                ),
                track_inactive_style=dk.DivShapeDrawable(
                    color="#20000000",
                    shape=dk.DivRoundedRectangleShape(
                        item_height=dk.DivFixedSize(value=6)
                    )
                )
            )
        )
    ]
)

```

This example might be serialised like this:

```python
import json

print(json.dumps(slider.dict(), indent=1))
# {
#  "log_id": "sample_card",
#  "states": [
#   {
#    "div": {
#     "type": "slider",
#     "max_value": 10,
#     "min_value": 1,
#     "thumb_style": {
#      "type": "shape_drawable",
#      "color": "#00b300",
#      "shape": {
#       "type": "rounded_rectangle",
#       "corner_radius": {
#        "type": "fixed",
#        "value": 100
#       },
#       "item_height": {
#        "type": "fixed",
#        "value": 32
#       },
#       "item_width": {
#        "type": "fixed",
#        "value": 32
#       }
#      },
#      "stroke": {
#       "color": "#ffffff",
#       "width": 3
#      }
#     },
#     "track_active_style": {
#      "type": "shape_drawable",
#      "color": "#00b300",
#      "shape": {
#       "type": "rounded_rectangle",
#       "item_height": {
#        "type": "fixed",
#        "value": 6
#       }
#      }
#     },
#     "track_inactive_style": {
#      "type": "shape_drawable",
#      "color": "#20000000",
#      "shape": {
#       "type": "rounded_rectangle",
#       "item_height": {
#        "type": "fixed",
#        "value": 6
#       }
#      }
#     },
#     "width": {
#      "type": "match_parent"
#     }
#    },
#    "state_id": 0
#   }
#  ]
# }
```

Templating and DRY
------------------

Of course, manually building blocks from your code every time is boring. 
So, the first idea is to move the initialization of DivKit objects 
into functions.

```python
# Naive DRY example which strictly non-recommended
import pydivkit as dk


def get_size(value: int = 32) -> dk.DivFixedSize:
    return dk.DivFixedSize(value=value)


def get_shape() -> dk.DivShape:
    return dk.DivShape(
        item_width=get_size(),
        item_height=get_size(),
        corner_radius=get_size(100)
    )

    
slider_shape = get_shape()

slider = dk.DivData(
    log_id="sample_card",
    states=[
        dk.DivDataState(
            # other arguments
            div=dk.DivSlider(
                thumb_style=dk.DivShapeDrawable(
                    shape=slider_shape,
                    # other arguments
                ),
                # other arguments
            )
        )
    ]
)
```

Looks a little better, but this approach doesn't scale well. To simplify layout 
and save traffic, DivKit has templates. This is a way to layout similar elements
without having to declare the complete json, but just declare a template and use
this many times in similar items.

**PyDivKit supports defining templates through the inheritance.**

Let's define an example card:

```python
import json

import pydivkit as dk


class CategoriesItem(dk.DivContainer):
    """
    Class inherited from dk.DivContainer will have a template
    """

    # Special object for mark this fields a DivKit field in template
    icon_url: str = dk.Field()
    text: str = dk.Field()

    # Set defaults layout for in the template
    width = dk.DivWrapContentSize()
    background = [dk.DivSolidBackground(color="#f0f0f0")]
    content_alignment_vertical = dk.DivAlignmentVertical.CENTER
    orientation = dk.DivContainerOrientation.HORIZONTAL
    paddings = dk.DivEdgeInsets(left=12, right=12, top=10, bottom=10)
    border = dk.DivBorder(corner_radius=12)
    items = [
        dk.DivImage(
            width=dk.DivFixedSize(value=20),
            height=dk.DivFixedSize(value=20),
            margins=dk.DivEdgeInsets(right=6),

            # Special object Ref it's a reference for Field property
            image_url=dk.Ref(icon_url),
        ),
        dk.DivText(
            width=dk.DivWrapContentSize(),
            max_lines=1,

            # Special object Ref it's a reference for Field property
            text=dk.Ref(text),
        ),
    ]


BASE_URL = "https://leonardo.edadeal.io/dyn/re/segments/level1/96"


# So after class definition you might use all the `Field` marked property
# names as an argument.

gallery = dk.DivGallery(
    items=[
        CategoriesItem(
            text="Food", icon_url=f"{BASE_URL}/food.png",
        ),
        CategoriesItem(
            text="Alcohol", icon_url=f"{BASE_URL}/alcohol.png",
        ),
        CategoriesItem(
            text="Household", icon_url=f"{BASE_URL}/household.png",
        ),
    ]
)

print(json.dumps(dk.make_div(gallery), indent=1, ensure_ascii=False))
# {
#  "templates": {
#   "__main__.CategoriesItem": {
#    "type": "container",
#    "background": [
#     {
#      "type": "solid",
#      "color": "#f0f0f0"
#     }
#    ],
#    "border": {
#     "corner_radius": 12
#    },
#    "content_alignment_vertical": "center",
#    "items": [
#     {
#      "type": "image",
#      "height": {
#       "type": "fixed",
#       "value": 20
#      },
#      "$image_url": "icon_url",
#      "margins": {
#       "right": 6
#      },
#      "width": {
#       "type": "fixed",
#       "value": 20
#      }
#     },
#     {
#      "type": "text",
#      "max_lines": 1,
#      "$text": "text",
#      "width": {
#       "type": "wrap_content"
#      }
#     }
#    ],
#    "orientation": "horizontal",
#    "paddings": {
#     "bottom": 10,
#     "left": 12,
#     "right": 12,
#     "top": 10
#    },
#    "width": {
#     "type": "wrap_content"
#    }
#   }
#  },
#  "card": {
#   "log_id": "card",
#   "states": [
#    {
#     "div": {
#      "type": "gallery",
#      "items": [
#       {
#        "type": "__main__.CategoriesItem",
#        "icon_url": "https://leonardo.edadeal.io/dyn/re/segments/level1/96/food.png",
#        "text": "Food"
#       },
#       {
#        "type": "__main__.CategoriesItem",
#        "icon_url": "https://leonardo.edadeal.io/dyn/re/segments/level1/96/alcohol.png",
#        "text": "Alcohol"
#       },
#       {
#        "type": "__main__.CategoriesItem",
#        "icon_url": "https://leonardo.edadeal.io/dyn/re/segments/level1/96/household.png",
#        "text": "Household"
#       }
#      ]
#     },
#     "state_id": 0
#    }
#   ]
#  }
# }
```

Template names
--------------

By default, templates are collecting by the metaclass into shared storage when 
the class is declaring at import time, and have the 
format `{module_name}.{class_name}`.

The following example, sure will not occur in the wild, shows a warning if 
suddenly the names of the classes, and hence the templates, conflict.

```python
import pydivkit as dk


class MyTemplate(dk.DivContainer):
    width = dk.DivWrapContentSize()


class MyTemplate(dk.DivContainer):
    pass

# RuntimeWarning: Template 'test.MyTemplate' already defined in 
# <class 'test.MyTemplate'> and will be replaced to <class 'test.MyTemplate'>
```

Also, if you do not want to show the structure of your project to the outside, 
or for some reason you need to make the example above clean, you can rename the
template by declaring a special attribute `__template_name__`

```python
import pydivkit as dk


class MyTemplate(dk.DivContainer):
    width = dk.DivWrapContentSize()

print(MyTemplate.template_name)
# >>> test.MyTemplate

class MyTemplate(dk.DivContainer):
    __template_name__ = "MyTemplate2"

print(MyTemplate.template_name)
# >>> MyTemplate2
```
