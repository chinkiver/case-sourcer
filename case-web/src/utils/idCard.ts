/**
 * 中国大陆 18 位身份证号工具（前端版，与后端 IdCardUtil 校验逻辑一致）。
 */

const PATTERN = /^[1-9]\d{5}(?:18|19|20)\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/

const WEIGHT = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2]

const CHECK_CODE = ['1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2']

export function isValidIdCard(idCard: string): boolean {
  if (!idCard || !PATTERN.test(idCard)) return false
  let sum = 0
  for (let i = 0; i < 17; i++) {
    sum += parseInt(idCard[i], 10) * WEIGHT[i]
  }
  const expected = CHECK_CODE[sum % 11]
  const actual = idCard[17].toUpperCase()
  return expected === actual
}

export function parseIdCard(idCard: string): {
  birthDate: string
  gender: '男' | '女'
  age: number
} | null {
  if (!isValidIdCard(idCard)) return null
  const yyyy = idCard.substring(6, 10)
  const mm = idCard.substring(10, 12)
  const dd = idCard.substring(12, 14)
  const birthDate = `${yyyy}-${mm}-${dd}`
  const genderNum = parseInt(idCard[16], 10)
  const gender: '男' | '女' = genderNum % 2 === 1 ? '男' : '女'
  const birth = new Date(`${birthDate}T00:00:00`)
  const now = new Date()
  let age = now.getFullYear() - birth.getFullYear()
  const beforeBirthday =
    now.getMonth() < birth.getMonth() ||
    (now.getMonth() === birth.getMonth() && now.getDate() < birth.getDate())
  if (beforeBirthday) age -= 1
  return { birthDate, gender, age }
}

/** 表单里实时提示：idCard 为空/格式错/合法。 */
export function describeIdCard(idCard: string): string {
  if (!idCard) return ''
  if (!PATTERN.test(idCard)) return '格式不正确（需 18 位）'
  if (!isValidIdCard(idCard)) return '校验位不通过'
  const parsed = parseIdCard(idCard)
  return parsed ? `${parsed.gender} · ${parsed.age} 岁` : ''
}