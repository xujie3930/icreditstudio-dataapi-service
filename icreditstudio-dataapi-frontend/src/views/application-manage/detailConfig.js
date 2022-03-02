/*
 * @Author: lizheng
 * @Description:
 * @Date: 2022-03-02
 */

import {
  ENABLED_STATUS,
  CERTIFICATION_TYPE,
  TOEKN_PERIOD
} from '@/config/constant'
import { dateFormat } from '@/utils'

export const detailConfiguration = {
  base: [
    { label: '应用ID', value: '', key: 'generateId' },
    {
      label: '认证方式',
      value: '',
      key: 'certificationType',
      formatter: val => CERTIFICATION_TYPE[val]
    },
    {
      label: '是否启用',
      value: '',
      key: 'isEnable',
      color: '',
      formatter: val => ENABLED_STATUS[val]
    },
    { label: '应用名称', value: '', key: 'name' },
    { label: '应用密钥', value: '', key: 'secretContent' },
    { label: '分组名称', value: '', key: 'appGroupName' },
    { label: 'token有效期', value: '', key: 'period' },
    { label: 'IP白名单', value: '', key: 'allowIp' },
    { label: '创建人', value: '', key: 'createBy' },
    {
      label: '创建时间',
      value: '',
      key: 'createTime',
      formatter: val => dateFormat(val)
    },
    { label: '备注', value: '', key: 'desc' }
  ],

  auth: [{ label: 'API名称', value: '', key: 'apiNames', span: 24 }],

  authTime: [
    {
      label: '授权有效时间',
      value: '',
      key: 'tokenType',
      span: 24,
      formatter: ({ tokenType }) => TOEKN_PERIOD[tokenType]
    },
    {
      label: '选择日期时间',
      value: '',
      key: 'callCountType',
      span: 24,
      formatter: ({ periodBegin, periodEnd }) =>
        `${dateFormat(periodBegin)} 至 ${dateFormat(periodEnd)}`
    },
    {
      label: '调用次数类型',
      value: '',
      key: 'allowCall',
      span: 24,
      formatter: ({ allowCall, callCountType }) =>
        `${callCountType}  ${allowCall < 0 ? '' : allowCall}`
    }
  ]
}

export const detailTitleKeyMapping = {
  base: '基础信息',
  auth: '授权信息',
  authTime: '授权时间'
}