import { FC } from 'react'
import { ReflexElement } from 'react-reflex'
import { DivKit } from '@divkitframework/react'
import { DivJson } from '@divkitframework/divkit/typings/common'

interface Props {
  className?: string
  json?: DivJson
  id: string
}

export const ReflexElementPreview: FC<Props> = ({
  className = '',
  json,
  id,
}) => {
  return (
    <ReflexElement minSize={300} maxSize={900} className={className}>
      {json && <DivKit id={id} json={json} />}
    </ReflexElement>
  )
}
