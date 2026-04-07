import http from '../request'
import type {
  CountOrderH,
  InventoryAuditReq,
  InventoryCheckResult,
  InventoryLedger,
  InventoryStock,
  InventoryStockQuery,
  TransferOrderH,
} from '../../types/inventory'

const get = <T>(url: string, params?: Record<string, any>) => http.get<T>(url, { params })
const post = <T>(url: string, data?: any) => http.post<T>(url, data)

export const inventoryStockApi = {
  query: (data: InventoryStockQuery) => post<InventoryStock[]>('/inventory/stock/query', data),
  check: (data: {
    warehouseId: number
    locationId?: number
    materialId: number
    batchNo?: string
    serialNo?: string
    status?: string
    requiredQty?: number
  }) => post<InventoryCheckResult>('/inventory/check', data),
}

export const inventoryLedgerApi = {
  list: (params: { bizType?: string; bizId?: number; materialId?: number; warehouseId?: number }) =>
    get<InventoryLedger[]>('/inventory/ledger', params),
}

export const transferApi = {
  list: (params: { keyword?: string }) => get<TransferOrderH[]>('/inventory/transfers', params),
  detail: (id: number) => get<{ header: TransferOrderH; lines: any[] }>(`/inventory/transfers/${id}`),
  save: (data: any) => post<{ header: TransferOrderH; lines: any[] }>('/inventory/transfers', data),
  audit: (id: number, data: InventoryAuditReq) => post<TransferOrderH>(`/inventory/transfers/${id}/audit`, data),
}

export const countApi = {
  list: (params: { keyword?: string }) => get<CountOrderH[]>('/inventory/counts', params),
  detail: (id: number) => get<{ header: CountOrderH; lines: any[] }>(`/inventory/counts/${id}`),
  save: (data: any) => post<{ header: CountOrderH; lines: any[] }>('/inventory/counts', data),
  audit: (id: number, data: InventoryAuditReq) => post<CountOrderH>(`/inventory/counts/${id}/audit`, data),
}
