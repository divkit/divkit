from sqlalchemy import Column, ForeignKey
from sqlalchemy.dialects.postgresql import UUID

from app.db.models.base import Base


class Templates2Tags(Base):
    __tablename__ = "templates2tags"

    id = Column(UUID, primary_key=True)
    template_id = Column(
        UUID, ForeignKey("templates.id", ondelete="CASCADE"), index=True, nullable=False
    )
    tag_id = Column(
        UUID, ForeignKey("tags.id", ondelete="CASCADE"), index=True, nullable=False
    )
