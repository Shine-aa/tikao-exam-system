import { getServerTime } from '@/api/user'

/**
 * 服务器时间同步工具
 * 用于在考试等场景中，以服务器时间为准，防止客户端时间被修改
 */
class ServerTimeSync {
  constructor() {
    this.serverTimestamp = null // 服务器时间戳
    this.clientTimestamp = null // 客户端时间戳（同步时的本地时间）
    this.syncInterval = null // 定期同步定时器
    this.updateInterval = null // 更新时间定时器
    this.listeners = [] // 时间更新监听器
    this.syncIntervalMs = 30000 // 每30秒同步一次
    this.updateIntervalMs = 1000 // 每秒更新一次
  }

  /**
   * 初始化时间同步
   */
  async init() {
    try {
      await this.syncTime()
      // 启动定期同步
      this.startSync()
      // 启动定期更新
      this.startUpdate()
    } catch (error) {
      console.error('服务器时间同步初始化失败:', error)
      // 如果初始化失败，使用客户端时间作为后备
      this.serverTimestamp = Date.now()
      this.clientTimestamp = Date.now()
    }
  }

  /**
   * 同步服务器时间
   */
  async syncTime() {
    try {
      const clientTimeBefore = Date.now()
      const response = await getServerTime()
      const clientTimeAfter = Date.now()
      
      if (response.code === 200 && response.data) {
        const serverTimestamp = response.data.timestamp
        // 计算网络延迟（往返时间的一半）
        const networkDelay = (clientTimeAfter - clientTimeBefore) / 2
        // 调整服务器时间戳，减去网络延迟
        this.serverTimestamp = serverTimestamp + networkDelay
        this.clientTimestamp = clientTimeAfter
        
        console.log('服务器时间同步成功，网络延迟:', networkDelay, 'ms')
      } else {
        throw new Error('获取服务器时间失败')
      }
    } catch (error) {
      console.error('同步服务器时间失败:', error)
      // 如果同步失败，使用客户端时间作为后备（但会记录错误）
      if (!this.serverTimestamp) {
        this.serverTimestamp = Date.now()
        this.clientTimestamp = Date.now()
      }
      throw error
    }
  }

  /**
   * 启动定期同步
   */
  startSync() {
    if (this.syncInterval) {
      clearInterval(this.syncInterval)
    }
    this.syncInterval = setInterval(() => {
      this.syncTime().catch(error => {
        console.error('定期同步服务器时间失败:', error)
      })
    }, this.syncIntervalMs)
  }

  /**
   * 启动定期更新
   */
  startUpdate() {
    if (this.updateInterval) {
      clearInterval(this.updateInterval)
    }
    this.updateInterval = setInterval(() => {
      this.notifyListeners()
    }, this.updateIntervalMs)
  }

  /**
   * 停止时间同步
   */
  stop() {
    if (this.syncInterval) {
      clearInterval(this.syncInterval)
      this.syncInterval = null
    }
    if (this.updateInterval) {
      clearInterval(this.updateInterval)
      this.updateInterval = null
    }
    this.listeners = []
  }

  /**
   * 获取当前服务器时间（Date对象）
   */
  getServerTime() {
    if (!this.serverTimestamp || !this.clientTimestamp) {
      // 如果还没有同步，返回客户端时间
      return new Date()
    }
    
    // 计算时间差
    const timeDiff = Date.now() - this.clientTimestamp
    // 返回服务器时间 + 时间差
    return new Date(this.serverTimestamp + timeDiff)
  }

  /**
   * 获取当前服务器时间戳（毫秒）
   */
  getServerTimestamp() {
    if (!this.serverTimestamp || !this.clientTimestamp) {
      // 如果还没有同步，返回客户端时间戳
      return Date.now()
    }
    
    // 计算时间差
    const timeDiff = Date.now() - this.clientTimestamp
    // 返回服务器时间戳 + 时间差
    return this.serverTimestamp + timeDiff
  }

  /**
   * 添加时间更新监听器
   */
  addListener(callback) {
    if (typeof callback === 'function') {
      this.listeners.push(callback)
    }
  }

  /**
   * 移除时间更新监听器
   */
  removeListener(callback) {
    const index = this.listeners.indexOf(callback)
    if (index > -1) {
      this.listeners.splice(index, 1)
    }
  }

  /**
   * 通知所有监听器
   */
  notifyListeners() {
    const serverTime = this.getServerTime()
    this.listeners.forEach(callback => {
      try {
        callback(serverTime)
      } catch (error) {
        console.error('时间更新监听器执行失败:', error)
      }
    })
  }

  /**
   * 检查是否已同步
   */
  isSynced() {
    return this.serverTimestamp !== null && this.clientTimestamp !== null
  }
}

// 创建单例实例
const serverTimeSync = new ServerTimeSync()

export default serverTimeSync

