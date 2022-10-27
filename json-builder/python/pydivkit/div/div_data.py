# Generated code. Do not modify.
# flake8: noqa: F401, F405, F811

from __future__ import annotations

import enum
import typing

from pydivkit.core import BaseDiv, Field

from . import div, div_transition_selector, div_trigger, div_variable


# Root structure.
class DivData(BaseDiv):

    def __init__(
        self, *,
        log_id: str,
        states: typing.List[DivDataState],
        transition_animation_selector: typing.Optional[div_transition_selector.DivTransitionSelector] = None,
        variable_triggers: typing.Optional[typing.List[div_trigger.DivTrigger]] = None,
        variables: typing.Optional[typing.List[div_variable.DivVariable]] = None,
    ):
        super().__init__(
            log_id=log_id,
            states=states,
            transition_animation_selector=transition_animation_selector,
            variable_triggers=variable_triggers,
            variables=variables,
        )

    log_id: str = Field(
        min_length=1, 
        description="Logging ID.",
    )
    states: typing.List[DivDataState] = Field(
        min_items=1, 
        description=(
            "A set of visual element states. Each element can have a few "
            "states with adifferent layout. The states are displayed "
            "strictly one by one and switched "
            "using[action](div-action.md)."
        ),
    )
    transition_animation_selector: typing.Optional[div_transition_selector.DivTransitionSelector] = Field(
        description="Events that trigger transition animations. @deprecated",
    )
    variable_triggers: typing.Optional[typing.List[div_trigger.DivTrigger]] = Field(
        min_items=1, 
        description="Triggers for changing variables.",
    )
    variables: typing.Optional[typing.List[div_variable.DivVariable]] = Field(
        min_items=1, 
        description="Declaration of variables that can be used in an element.",
    )


class DivDataState(BaseDiv):

    def __init__(
        self, *,
        div: div.Div,
        state_id: int,
    ):
        super().__init__(
            div=div,
            state_id=state_id,
        )

    div: div.Div = Field(
        description="Contents.",
    )
    state_id: int = Field(
        description="State ID.",
    )


DivDataState.update_forward_refs()


DivData.update_forward_refs()
