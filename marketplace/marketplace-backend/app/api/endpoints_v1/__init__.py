from app.api.endpoints_v1.tag import tag_router
from app.api.endpoints_v1.template import template_router

list_of_routes = [
    tag_router,
    template_router,
]

__all__ = [
    "list_of_routes",
]
