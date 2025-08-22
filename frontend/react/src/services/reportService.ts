import { request } from './api'

export async function downloadWarrantyExpiry(from: string, to: string) {
  const blob = await request(`/api/reports/warranty-expiry?from=${from}&to=${to}`) as Blob
  const url = URL.createObjectURL(blob); const a = document.createElement('a')
  a.href = url; a.download = `warranty-expiry-report.csv`; a.click(); URL.revokeObjectURL(url)
}

export async function downloadAssetAging(olderThanDays: number) {
  const blob = await request(`/api/reports/asset-aging?olderThanDays=${olderThanDays}`) as Blob
  const url = URL.createObjectURL(blob); const a = document.createElement('a')
  a.href = url; a.download = `asset-aging-report.csv`; a.click(); URL.revokeObjectURL(url)
}
