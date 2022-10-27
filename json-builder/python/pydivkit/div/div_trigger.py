# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field

from . import div_action


# A trigger that causes an action when activated.
class DivTrigger(BaseDiv):

    def __init__(
        self, *,
        actions: typing.List[div_action.DivAction],
        condition: bool,
        mode: typing.Optional[DivTriggerMode] = None,
    ):
        super().__init__(
            actions=actions,
            condition=condition,
            mode=mode,
        )

    actions: typing.List[div_action.DivAction] = Field(
        min_items=1, 
        description="Action when a trigger is activated.",
    )
    condition: bool = Field(
        description=(
            "Condition for activating a trigger. For example, `liked && "
            "subscribed`."
        ),
    )
    mode: typing.Optional[DivTriggerMode] = Field(
        description=(
            "Trigger activation mode:`on_condition` — a trigger is "
            "activated when thecondition changes from `false` to "
            "`true`;`on_variable` — a trigger is activatedwhen the "
            "condition is met and the variable value changes."
        ),
    )


class DivTriggerMode(str, enum.Enum):
    ON_CONDITION = "on_condition"
    ON_VARIABLE = "on_variable"


DivTrigger.update_forward_refs()
