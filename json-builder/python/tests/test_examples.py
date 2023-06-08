import pydivkit as dk


def test_container_example():
    container = dk.DivContainer(
        items=[dk.DivGallery(items=[dk.DivText(text="Hello from pydivkit")])]
    )

    assert container.dict() == {
        "type": "container", "items": [
            {"type": "gallery", "items": [
                {"type": "text", "text": "Hello from pydivkit"}]
             }
        ]
    }


def test_slider_example():
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

    assert slider.dict() == dict(log_id="sample_card", states=[
        dict(
            div=dict(
                type="slider",
                max_value=10,
                min_value=1,
                thumb_style=dict(
                    type="shape_drawable", color="#00b300",
                    shape=dict(
                        type="rounded_rectangle",
                        corner_radius=dict(
                            type="fixed", value=100
                        ),
                        item_height=dict(type="fixed", value=32),
                        item_width=dict(type="fixed", value=32),
                    ),
                    stroke=dict(color="#ffffff", width=3)
                ),
                track_active_style=dict(
                    type="shape_drawable",
                    color="#00b300",
                    shape=dict(
                        type="rounded_rectangle",
                        item_height=dict(type="fixed", value=6))),
                track_inactive_style=dict(
                    type="shape_drawable", color="#20000000", shape=dict(
                        type="rounded_rectangle",
                        item_height=dict(type="fixed", value=6))
                ),
                width=dict(type="match_parent")
            ),
            state_id=0
        )
    ])


def test_gallery():
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

    base_url = "https://leonardo.edadeal.io/dyn/re/segments/level1/96"

    # So after class definition you might use all the `Field` marked property
    # names as an argument.

    gallery = dk.DivGallery(
        items=[
            CategoriesItem(
                text="Food", icon_url=f"{base_url}/food.png",
            ),
            CategoriesItem(
                text="Alcohol", icon_url=f"{base_url}/alcohol.png",
            ),
            CategoriesItem(
                text="Household", icon_url=f"{base_url}/household.png",
            ),
        ]
    )

    template_name = CategoriesItem.template_name

    assert dk.make_div(gallery) == {
        'templates': {
            template_name: {
                'type': 'container',
                'background': [{'type': 'solid', 'color': '#f0f0f0'}],
                'border': {'corner_radius': 12},
                'content_alignment_vertical': 'center',
                'items': [
                    {
                        'type': 'image',
                        'height': {'type': 'fixed', 'value': 20},
                        '$image_url': 'icon_url',
                        'margins': {'right': 6},
                        'width': {'type': 'fixed', 'value': 20}
                    }, {
                        'type': 'text',
                        'max_lines': 1,
                        '$text': 'text',
                        'width': {
                            'type': 'wrap_content'
                        }
                    }
                ],
                'orientation': 'horizontal',
                'paddings': {'bottom': 10, 'left': 12, 'right': 12, 'top': 10},
                'width': {'type': 'wrap_content'}
            }
        },
        'card': {
            'log_id': 'card',
            'states': [
                {
                    'div': {
                        'type': 'gallery',
                        'items': [
                            {
                                'type': template_name,
                                'icon_url': f'{base_url}/food.png',
                                'text': 'Food'
                            }, {
                                'type': template_name,
                                'icon_url': f'{base_url}/alcohol.png',
                                'text': 'Alcohol'
                            }, {
                                'type': template_name,
                                'icon_url': f'{base_url}/household.png',
                                'text': 'Household'
                            }
                        ]
                    },
                    'state_id': 0
                }
            ]
        }
    }
