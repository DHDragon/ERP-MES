import type { PageResult } from './common'

export interface InventoryStock {
  id?: number
  warehouseId: number
  locationId?: number
  materialId: number
  batchNo?: string
  serialNo?: string
  status?: string
  qty?: number
  lockedQty?: number
}

export interface InventoryLedger {
  id?: number
  bizType?: string
  bizId?: number
  bizLineId?: number
  warehouseId: number
  locationId?: number
  materialId: number
  batchNo?: string
  serialNo?: string
  status?: string
  qtyBefore?: number
  qtyChange?: number
  qtyAfter?: number
  direction?: string
  remark?: string
}

export interface InventoryStockQuery {
  warehouseId?: number
  materialId?: number
  batchNo?: string
  status?: string
}

export interface InventoryCheckResult {
  qty: number
  lockedQty: number
  availableQty: number
  requiredQty?: number
  enough: boolean
}

export interface TransferOrderH {
  id?: number
  transferNo?: string
  fromWarehouseId: number
  toWarehouseId: number
  bizDate?: string
  status?: string
  approvalStatus?: string
  totalQty?: number
  executedQty?: number
  remark?: string
}

export interface TransferOrderD {
  id?: number
  transferId?: number
  lineNo?: number
  materialId: number
  batchNo?: string
  serialNo?: string
  fromLocationId?: number
  toLocationId?: number
  qty: number
  executedQty?: number
  status?: string
}

export interface CountOrderH {
  id?: number
  countNo?: string
  warehouseId: number
  bizDate?: string
  status?: string
  approvalStatus?: string
  totalQty?: number
  diffQty?: number
  remark?: string
}

export interface CountOrderD {
  id?: number
  countId?: number
  lineNo?: number
  materialId: number
  batchNo?: string
  serialNo?: string
  locationId?: number
  qtyBook?: number
  qtyActual: number
  diffQty?: number
  status?: string
}

export interface InventoryAuditReq {
  approved: boolean
  remark?: string
}

export type InventoryPage<T> = PageResult<T>
