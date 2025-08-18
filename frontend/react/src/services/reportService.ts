import { request } from './api'
export async function download(kind:'usage'|'assignments'|'performance'){
  const blob = await request(`/api/reports/${kind}`) as Blob
  const url = URL.createObjectURL(blob); const a = document.createElement('a')
  a.href = url; a.download = `${kind}-report.csv`; a.click(); URL.revokeObjectURL(url)
}
