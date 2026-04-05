export interface Result<T = unknown> {
  code: number
  message: string
  data: T
  traceId?: string
}

export interface PageRequest {
  pageNo: number
  pageSize: number
}

export interface PageResult<T = unknown> {
  total: number
  pageNo: number
  pageSize: number
  records: T[]
}

export type BizStatus = 'DRAFT' | 'SUBMITTED' | 'APPROVED' | 'REJECTED' | 'CLOSED'
