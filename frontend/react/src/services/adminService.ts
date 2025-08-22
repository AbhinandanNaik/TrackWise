import { request } from './api'

export async function runNewsScan(): Promise<string> {
  return request('/api/admin/jobs/run-news-scan', { method: 'POST' })
}

export async function runPerformanceAnalysis(): Promise<string> {
  return request('/api/admin/jobs/run-performance-analysis', { method: 'POST' })
}


