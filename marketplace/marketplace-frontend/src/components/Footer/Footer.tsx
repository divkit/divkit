import React from 'react'
import cn from 'classnames'
import TelegramIcon from '@mui/icons-material/Telegram'
import GitHubIcon from '@mui/icons-material/GitHub'
import TwitterIcon from '@mui/icons-material/Twitter'
import styles from './Footer.module.scss'
import { Container } from '@mui/material'

export const Footer = () => {
  return (
    <footer>
      <Container
        sx={{
          color: '#fff',
          display: 'flex',
          alignItems: 'center',
          gap: 3,
        }}
        className={cn(styles.footer)}>
        <a
          target='_blank'
          rel='noreferrer'
          href='https://t.me/divkit_community_ru'>
          <TelegramIcon fontSize='large' />
        </a>
        <a
          target='_blank'
          rel='noreferrer'
          href='https://github.com/divkit/divkit'>
          <GitHubIcon fontSize='large' />
        </a>
        <a
          target='_blank'
          rel='noreferrer'
          href='https://twitter.com/DivKitFramework'>
          <TwitterIcon fontSize='large' />
        </a>
      </Container>
    </footer>
  )
}
