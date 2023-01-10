import { useState, useEffect, useRef } from 'react'
import { Collapse, IconButton, Box } from '@mui/material'
import { ExpandMore } from '@mui/icons-material'

import cn from 'classnames'
import styles from './CollapseElement.module.scss'

interface IProps {
  children: React.ReactNode
  innerElementHeight?: number
  collapsedHeight: string
}

const COLLAPSE_OVERLAP = 3

export const CollapseElement = ({
  children,
  innerElementHeight,
  collapsedHeight,
}: IProps) => {
  const [isStretch, setIsStretch] = useState(false)
  const [isExpanded, setIsExpanded] = useState(false)
  const collapseElement = useRef<HTMLDivElement | null>(null)

  useEffect(() => {
    if (
      collapseElement.current &&
      innerElementHeight &&
      innerElementHeight >
        collapseElement.current.clientHeight + COLLAPSE_OVERLAP
    ) {
      setIsStretch(true)
    }
  }, [innerElementHeight])

  const handleExpandClick = () => {
    setIsExpanded(prevValue => !prevValue)
  }

  return (
    <>
      <Collapse
        in={isExpanded}
        collapsedSize={collapsedHeight}
        orientation='vertical'
        ref={collapseElement}
        className={styles.preview}>
        {children}
      </Collapse>
      {isStretch && (
        <Box
          component={'div'}
          sx={{
            display: 'flex',
            justifyContent: 'center',
            marginBottom: '-3.8rem',
          }}>
          <IconButton
            className={cn(styles.btn__expand, {
              [styles.btn__expanded]: isExpanded,
            })}
            onClick={handleExpandClick}
            aria-expanded={isExpanded}
            aria-label='show more'>
            <ExpandMore className={styles.btn__icon} fontSize='large' />
          </IconButton>
        </Box>
      )}
    </>
  )
}
