import type { PageResult } from './common'

export interface MdMaterialCategory {
  id?: number
  parentId?: number | null
  categoryCode: string
  categoryName: string
  status?: string
  approvalStatus?: string
}

export interface MdMaterial {
  id?: number
  materialCode: string
  materialName: string
  categoryId?: number | null
  spec?: string
  model?: string
  unit?: string
  status?: string
  approvalStatus?: string
  auditRemark?: string
}

export interface MdCustomer {
  id?: number
  customerCode: string
  customerName: string
  contactName?: string
  contactPhone?: string
  address?: string
  status?: string
  approvalStatus?: string
  auditRemark?: string
}

export interface MdSupplier {
  id?: number
  supplierCode: string
  supplierName: string
  contactName?: string
  contactPhone?: string
  address?: string
  status?: string
  approvalStatus?: string
  auditRemark?: string
}

export interface MdWarehouse {
  id?: number
  warehouseCode: string
  warehouseName: string
  warehouseType?: string
  status?: string
  approvalStatus?: string
  auditRemark?: string
}

export interface MdLocation {
  id?: number
  warehouseId?: number
  locationCode: string
  locationName: string
  status?: string
  approvalStatus?: string
  auditRemark?: string
}

export interface AuditReq {
  approved: boolean
  remark?: string
}

export interface ImportResp {
  count: number
}

export interface ExportResp {
  module: string
  status: string
  message: string
}

export type MasterPage<T> = PageResult<T>
