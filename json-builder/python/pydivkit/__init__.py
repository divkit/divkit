# flake8: noqa: F405
from typing import Any, Dict

from pydivkit.core import Field, Ref
from pydivkit.div import *
from pydivkit.div import __all__ as __div_all__


def make_card(log_id: str, *divs: Div) -> DivData:
    return DivData(
        log_id=log_id,
        states=[
            DivDataState(state_id=state_id, div=div)
            for state_id, div in enumerate(divs)
        ],
    )


def make_div(div: Div) -> Dict[str, Any]:
    return {
        "templates": {
            tpl.template_name: tpl.template()
            for tpl in div.related_templates()
        },
        "card": make_card("card", div).dict(),
    }


__all__ = ("Field", "Ref", "make_card", "make_div") + __div_all__
