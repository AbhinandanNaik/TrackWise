import { request } from './api'

export type Article = {
  title: string
  description?: string
  url?: string
}

export async function search(keyword: string): Promise<Article[]> {
  try {
    return await request(`/api/news/search?keyword=${encodeURIComponent(keyword)}`)
  } catch {
    return []
  }
}


